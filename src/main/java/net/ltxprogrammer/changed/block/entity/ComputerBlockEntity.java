package net.ltxprogrammer.changed.block.entity;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.computers.*;
import net.ltxprogrammer.changed.computers.generator.ConfiguredFileSystemGenerators;
import net.ltxprogrammer.changed.computers.generator.FileSystemGenerator;
import net.ltxprogrammer.changed.computers.protocol.*;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ComputerBlockEntity extends BaseContainerBlockEntity implements StackedContentsCompatible, NetworkInterface {
    public final RandomSource random = RandomSource.create();

    /// Act as slots for hot swappable devices (e.g. CD)
    public NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    public final BasicNIC nic;
    public LexicalPath.Absolute currentWorkingDirectory;
    public LexicalPath.Absolute homeDirectory;
    public LexicalPath.Absolute binariesDirectory;

    /// Parallels an HDD or SSD in a computer. Saves with the block entity.
    protected DiscData primaryDisc;
    /// Mapping of drive letter to mounted disc (C -> primaryDisc)
    protected final Char2ObjectMap<SourcedDiscData> mountedFileSystems = new Char2ObjectArrayMap<>();

    public Optional<Character> mountDisc(SourcedDiscData disc) {
        char nextLetter = 'C';
        while (mountedFileSystems.containsKey(nextLetter)) {
            nextLetter++;
            if (nextLetter > 'Z')
                nextLetter = 'A';
            if (nextLetter == 'C')
                return Optional.empty();
        }

        return mountDisc(nextLetter, disc) ? Optional.of(nextLetter) : Optional.empty();
    }

    public boolean mountDisc(char driveLetter, SourcedDiscData disc) {
        if (disc == null)
            return false; // Do not mount a null disc
        if (driveLetter < 'A' || driveLetter > 'Z')
            return false; // Do not mount outside the letter range
        if (mountedFileSystems.containsValue(disc))
            return false; // Do not double mount
        mountedFileSystems.put(driveLetter, disc);
        return true;
    }

    public boolean unmountDisc(DiscData disc) {
        var it = mountedFileSystems.char2ObjectEntrySet().iterator();
        while (it.hasNext()) {
            var entry = it.next();
            if (entry.getValue().matchesOrigin(disc)) {
                entry.getValue().writeBack();
                it.remove();
                return true;
            }
        }

        return false;
    }

    public boolean unmountDisc(ItemStack disc) {
        var it = mountedFileSystems.char2ObjectEntrySet().iterator();
        while (it.hasNext()) {
            var entry = it.next();
            if (entry.getValue().matchesOrigin(disc)) {
                entry.getValue().writeBack();
                it.remove();
                return true;
            }
        }

        return false;
    }

    public boolean unmountDisc(char driveLetter) {
        var unmounted = mountedFileSystems.remove(driveLetter);
        if (unmounted != null) {
            unmounted.writeBack();
            return true;
        }

        return false;
    }

    protected void unmountAll() {
        var it = mountedFileSystems.char2ObjectEntrySet().iterator();
        while (it.hasNext()) {
            var entry = it.next();
            entry.getValue().writeBack();
            it.remove();
        }
    }

    public void writeBackAll() {
        for (var entry : mountedFileSystems.char2ObjectEntrySet()) {
            entry.getValue().writeBack();
        }
    }

    public boolean eject(char driveLetter) {
        if (!mountedFileSystems.containsKey(driveLetter))
            return false;
        var sourcedDisc = mountedFileSystems.get(driveLetter);
        if (!sourcedDisc.canEject())
            return false;
        sourcedDisc.eject();
        return true;
    }

    @Nullable
    public ServerPlayer activeUser;

    @Nullable
    public ComputerMenu getActiveMenu() {
        if (activeUser == null)
            return null;
        if (activeUser.containerMenu instanceof ComputerMenu computerMenu)
            return computerMenu;
        return null;
    }

    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.changed.computer");
    }

    protected @NotNull AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory) {
        var menu = new ComputerMenu(id, inventory, this);
        menu.syncBlockEntity();
        return menu;
    }

    public ComputerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ChangedBlockEntities.COMPUTER.get(), blockPos, blockState);
        nic = new BasicNIC(Address.forBlock(blockPos.immutable()));
        nic.logicalAddress = this.random.nextInt();

        primaryDisc = createFileSystem(random);
        this.mountDisc(SourcedDiscData.wrap(primaryDisc, false));
    }

    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public ItemStack getItem(int slot) {
        return this.items.get(slot);
    }

    public ItemStack removeItem(int slot, int count) {
        var slotted =  ContainerHelper.removeItem(this.items, slot, count);
        this.handleSlotChanged(slot, slotted, this.items.get(slot));
        return slotted;
    }

    public ItemStack removeItemNoUpdate(int slot) {
        var slotted = ContainerHelper.takeItem(this.items, slot);
        this.handleSlotChanged(slot, slotted, this.items.get(slot));
        return slotted;
    }

    public void setItem(int slot, ItemStack stack) {
        ItemStack existingItem = this.items.get(slot);
        boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameTags(stack, existingItem);
        this.items.set(slot, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

        if (slot == 0 && !flag) {
            this.setChanged();
        }

        this.handleSlotChanged(slot, existingItem, stack);
    }

    protected void handleSlotChanged(int slot, ItemStack previousItem, ItemStack currentItem) {
        if (slot == 0) {
            if (!previousItem.isEmpty())
                this.unmountDisc(previousItem);

            if (!currentItem.isEmpty())
                this.mountDisc(SourcedDiscData.fromItem(currentItem, this::setChanged, disc -> {
                    this.setItem(slot, ItemStack.EMPTY);
                    level.playSound(null, getBlockPos(), ChangedSounds.COMPUTER_DISC_EJECT.get(), SoundSource.BLOCKS, 1.0f, 0.9F + level.random.nextFloat() * 0.2F);
                    Block.popResource(level, getBlockPos(), disc);
                }));
        }
    }

    public int getContainerSize() {
        return this.items.size();
    }

    public boolean stillValid(Player player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        } else {
            return player.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
        }
    }

    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot == 0)
            return stack.is(ChangedItems.COMPACT_DISC.get());
        else
            return false;
    }

    public void clearContent() {
        for (int index = 0; index < this.items.size(); ++index) {
            this.handleSlotChanged(index, this.items.set(index, ItemStack.EMPTY), ItemStack.EMPTY);
        }
    }

    public void fillStackedContents(StackedContents contents) {
        for (ItemStack itemstack : this.items) {
            contents.accountStack(itemstack);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        this.writeBackAll();
        ContainerHelper.saveAllItems(tag, this.items);
        tag.put("fs", this.primaryDisc.serialize());
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = this.saveWithoutMetadata();

        CompoundTag mounted = new CompoundTag();
        for (var entry : mountedFileSystems.char2ObjectEntrySet()) {
            CompoundTag discInfo = new CompoundTag();
            discInfo.put("fs", entry.getValue().getDiscData().generateIfNecessary(this.configureDirectory(entry.getCharKey())).serialize());
            discInfo.putBoolean("ejectable", entry.getValue().canEject());
            mounted.put(String.valueOf(entry.getCharKey()), discInfo);
        }

        tag.remove("fs");
        tag.put("mounted", mounted);

        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
        for (int slot = 0; slot < this.items.size(); ++slot)
            this.handleSlotChanged(slot, ItemStack.EMPTY, this.items.get(slot));
        if (tag.contains("fs")) {
            this.unmountDisc(primaryDisc);
            this.primaryDisc = new DiscData(tag.getCompound("fs"), this::setChanged);
            this.mountDisc(SourcedDiscData.wrap(primaryDisc, false));
        } else {
            this.unmountAll();
            var mounted = tag.getCompound("mounted");
            mounted.getAllKeys().forEach(letter -> {
                CompoundTag discInfo = mounted.getCompound(letter);
                mountedFileSystems.put(letter.charAt(0), SourcedDiscData.wrap(new DiscData(discInfo.getCompound("fs"), this::setChanged),
                        discInfo.getBoolean("ejectable")));
            });
        }
    }

    @Override
    public void acceptFrame(ServerLevel level, Address physicalSource, Frame dataFrame) {
        nic.acceptFrame(level, physicalSource, dataFrame);
    }

    @Override
    public void sendFrame(ServerLevel level, Frame dataFrame) {
        nic.sendFrame(level, dataFrame);
    }

    public void handlePacket(ServerLevel level, int logicalSource, Packet packet) {
        var menu = this.getActiveMenu();

        if (packet instanceof DiscoveryProtocol discoveryProtocol && !discoveryProtocol.isReply()) {
            Set<Class<?>> protocols = new HashSet<>();
            protocols.add(DiscoveryProtocol.class);
            protocols.add(DeviceInfoProtocol.Query.class);
            protocols.add(FileSystemShareProtocol.Query.class);
            if (menu != null)
                protocols.addAll(menu.currentApplication().getNetworkProtocols());
            nic.sendPacket(level, logicalSource, discoveryProtocol.intersect(protocols));
        }

        if (packet == DeviceInfoProtocol.Query.INSTANCE) {
            nic.sendPacket(level, logicalSource, new DeviceInfoProtocol(
                    Component.literal("Computer"),
                    this.getBlockPos(),
                    Changed.modResource("computer")
            ));
        }

        if (menu != null)
            menu.currentApplication().handlePacket(level, logicalSource, packet);
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, ComputerBlockEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            blockEntity.nic.tick(serverLevel, blockPos);
            var menu = blockEntity.getActiveMenu();
            if (menu != null)
                menu.currentApplication().serverTick(serverLevel);
            blockEntity.nic.processPackets(serverLevel, blockEntity::handlePacket);
        }
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public void visitMountedFileSystems(FileSystemVisitor consumer) {
        for (var entry : mountedFileSystems.char2ObjectEntrySet()) {
            consumer.visit(
                    entry.getCharKey(),
                    entry.getValue().getDiscData().generateIfNecessary(this.configureDirectory(entry.getCharKey())),
                    entry.getValue().canEject()
            );
        }
    }

    public @Nullable DiscData getFileSystem(char driveLetter) {
        var sourcedData = mountedFileSystems.get(driveLetter);
        if (sourcedData == null)
            return null;
        return sourcedData.getDiscData().generateIfNecessary(this.configureDirectory(driveLetter));
    }

    public @Nullable DiscData getFileSystem(LexicalPath.Absolute drive) {
        return getFileSystem(drive.getDriveLetter());
    }

    public Either<File, File.Error> getFile(LexicalPath.Absolute path) {
        var driveName = path.getRoot();
        var fs = getFileSystem(driveName);
        if (fs != null)
            return fs.getFile(driveName.relativize(path));
        return Either.right(File.Error.FILESYSTEM_NOT_FOUND);
    }

    public Permissions getFilePermissions(LexicalPath.Absolute path) {
        var driveName = path.getRoot();
        var fs = getFileSystem(driveName);
        if (fs != null)
            return fs.getPermissions();
        return Permissions.NONE;
    }

    /**
     * Like {@link #getFile(LexicalPath.Absolute)}, but returns a file error if the type does not match.
     */
    public Either<File, File.Error> getFileOfType(LexicalPath.Absolute path, File.Type type) {
        var f = getFile(path);
        if (f.left().isPresent()) {
            if (f.left().get().type != type)
                return Either.right(File.Error.INVALID_TYPE);
        }

        return f;
    }

    public Either<File, File.Error> createFile(LexicalPath.Absolute path, File.Type type) {
        var driveName = path.getRoot();
        var fs = getFileSystem(driveName);
        if (fs != null)
            return fs.createFile(driveName.relativize(path), type);
        return Either.right(File.Error.FILESYSTEM_NOT_FOUND);
    }

    public Either<File, File.Error> getOrCreateFile(LexicalPath.Absolute path, File.Type type) {
        var f = getFileOfType(path, type);
        if (f.left().isPresent())
            return f;
        if (f.right().isPresent() && f.right().get() == File.Error.INVALID_TYPE)
            return f;
        return createFile(path, type);
    }

    public @Nullable Folder getFolder(LexicalPath.Absolute path) {
        var driveName = path.getRoot();
        var fs = getFileSystem(driveName);
        if (fs != null)
            return fs.getFolder(driveName.relativize(path));
        return null;
    }

    public Optional<Folder> getFolderSafe(LexicalPath.Absolute path) {
        var driveName = path.getRoot();
        var fs = getFileSystem(driveName);
        if (fs != null)
            return Optional.ofNullable(fs.getFolder(driveName.relativize(path)));
        return Optional.empty();
    }

    public Optional<File.Error> copyFileOrFolder(LexicalPath.Absolute from, LexicalPath.Absolute to) {
        var oldParentPath = from.getParent();
        var newParentPath = to.getParent();
        if (oldParentPath == null || newParentPath == null)
            return Optional.of(File.Error.INVALID_PATH);

        Folder oldParent = this.getFolder(oldParentPath);
        Folder newParent = this.getFolder(newParentPath);
        if (oldParent == null || newParent == null)
            return Optional.of(File.Error.INVALID_PATH);
        Permissions oldPerms = getFilePermissions(oldParentPath);
        Permissions newPerms = getFilePermissions(newParentPath);
        if (!oldPerms.canRead())
            return Optional.of(File.Error.NO_READ_PERMISSION);
        if (!newPerms.canWrite())
            return Optional.of(File.Error.NO_WRITE_PERMISSION);

        String oldName = from.getFileName().toString();
        String newName = to.getFileName().toString();

        if (oldParent.files.containsKey(oldName)) { // Copy file
            File oldFile = oldParent.files.get(oldName);
            return newParent.createFile(newName, oldFile.type).ifLeft(newFile -> {
                newFile.setContent(oldFile.getContent());
            }).right();
        } else if (oldParent.folders.containsKey(oldName)) { // Copy folder
            Folder oldFolder = oldParent.folders.get(oldName);
            return newParent.createFolder(newName).ifLeft(newFolder -> {
                newFolder.deserialize(oldFolder.serialize());
            }).right();
        } else {
            return Optional.of(File.Error.INVALID_PATH);
        }
    }

    public Optional<File.Error> moveFileOrFolder(LexicalPath.Absolute from, LexicalPath.Absolute to) {
        var oldParentPath = from.getParent();
        var newParentPath = to.getParent();
        if (oldParentPath == null || newParentPath == null)
            return Optional.of(File.Error.INVALID_PATH);

        Folder oldParent = this.getFolder(oldParentPath);
        Folder newParent = this.getFolder(newParentPath);
        if (oldParent == null || newParent == null)
            return Optional.of(File.Error.INVALID_PATH);
        Permissions oldPerms = getFilePermissions(oldParentPath);
        Permissions newPerms = getFilePermissions(newParentPath);
        if (!oldPerms.canRead())
            return Optional.of(File.Error.NO_READ_PERMISSION);
        if (!newPerms.canWrite() || !oldPerms.canWrite())
            return Optional.of(File.Error.NO_WRITE_PERMISSION);

        String oldName = from.getFileName().toString();
        String newName = to.getFileName().toString();

        if (oldParent.files.containsKey(oldName)) { // Copy file
            File oldFile = oldParent.files.get(oldName);
            return newParent.createFile(newName, oldFile.type).ifLeft(newFile -> {
                newFile.setContent(oldFile.getContent());
                oldParent.files.remove(oldName);
                oldParent.markModified();
            }).right();
        } else if (oldParent.folders.containsKey(oldName)) { // Copy folder
            Folder oldFolder = oldParent.folders.get(oldName);
            return newParent.createFolder(newName).ifLeft(newFolder -> {
                newFolder.deserialize(oldFolder.serialize());
                oldParent.folders.remove(oldName);
                oldParent.markModified();
            }).right();
        } else {
            return Optional.of(File.Error.INVALID_PATH);
        }
    }

    public Optional<File.Error> removeFileOrFolder(LexicalPath.Absolute path) {
        var parentPath = path.getParent();
        if (parentPath == null)
            return Optional.of(File.Error.INVALID_PATH);

        Folder parent = this.getFolder(parentPath);
        if (parent == null)
            return Optional.of(File.Error.INVALID_PATH);
        Permissions perms = getFilePermissions(parentPath);
        if (!perms.canWrite())
            return Optional.of(File.Error.NO_WRITE_PERMISSION);

        String name = path.getFileName().toString();

        if (parent.files.containsKey(name)) { // Remove file
            parent.files.remove(name);
            parent.markModified();
            return Optional.empty();
        } else if (parent.folders.containsKey(name)) { // Remove folder
            parent.folders.remove(name);
            parent.markModified();
            return Optional.empty();
        } else {
            return Optional.of(File.Error.INVALID_PATH);
        }
    }

    protected FileSystemGenerator.DirectoryConsumer configureDirectory(char driveLetter) {
        var driveRoot = LexicalPath.fromDriveLetter(driveLetter);
        return (dir, path) -> {
            switch (dir) {
                case HOME_DIR -> homeDirectory = driveRoot.resolve(path);
                case BIN_DIR -> binariesDirectory = driveRoot.resolve(path);
            }
        };
    }

    protected DiscData createFileSystem(RandomSource random) {
        var data = new DiscData(this::setChanged);
        var generator = ConfiguredFileSystemGenerators.getGenerator(Changed.modResource("default_pc"));
        if (generator == null)
            return data;

        generator.generate(random, data, this.configureDirectory('C'));
        currentWorkingDirectory = homeDirectory;
        return data;
    }
}
