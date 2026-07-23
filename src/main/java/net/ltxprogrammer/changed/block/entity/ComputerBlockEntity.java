package net.ltxprogrammer.changed.block.entity;

import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.computers.*;
import net.ltxprogrammer.changed.computers.protocol.*;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;

public class ComputerBlockEntity extends BaseContainerBlockEntity implements StackedContentsCompatible, NetworkInterface {
    public final RandomSource random = RandomSource.create();

    /// Act as slots for hot swappable devices (e.g. CD)
    public NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    public final BasicNIC nic;
    public Path currentWorkingDirectory;
    public Path homeDirectory;
    public Path binariesDirectory;

    /// Parallels an HDD or SSD in a computer. Saves with the block entity.
    protected DiscData primaryDisc;
    /// Mapping of drive letter to mounted disc (C -> primaryDisc)
    protected final Byte2ObjectMap<SourcedDiscData> mountedFileSystems = new Byte2ObjectArrayMap<>();

    public boolean mountDisc(SourcedDiscData disc) {
        byte nextLetter = 'C';
        while (mountedFileSystems.containsKey(nextLetter)) {
            nextLetter++;
            if (nextLetter > 'Z')
                nextLetter = 'A';
            if (nextLetter == 'C')
                return false;
        }

        return mountDisc(nextLetter, disc);
    }

    public boolean mountDisc(byte driveLetter, SourcedDiscData disc) {
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
        var it = mountedFileSystems.byte2ObjectEntrySet().iterator();
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
        var it = mountedFileSystems.byte2ObjectEntrySet().iterator();
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

    public boolean unmountDisc(byte driveLetter) {
        var unmounted = mountedFileSystems.remove(driveLetter);
        if (unmounted != null) {
            unmounted.writeBack();
            return true;
        }

        return false;
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
        return new ComputerMenu(id, inventory, this);
    }

    public ComputerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ChangedBlockEntities.COMPUTER.get(), blockPos, blockState);
        nic = new BasicNIC(Address.forBlock(blockPos.immutable()));
        nic.logicalAddress = this.random.nextInt();

        primaryDisc = createFileSystem(random);
        this.mountDisc(SourcedDiscData.wrap(primaryDisc));
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
                this.mountDisc(SourcedDiscData.fromItem(currentItem));
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
        for(ItemStack itemstack : this.items) {
            contents.accountStack(itemstack);
        }
    }

    protected void saveAdditional(CompoundTag tag) {
        ContainerHelper.saveAllItems(tag, this.items);
        tag.put("fs", this.primaryDisc.serialize());
    }

    public void load(CompoundTag tag) {
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        this.unmountDisc(primaryDisc);
        this.primaryDisc = new DiscData(tag.getCompound("fs"));
        this.mountDisc(SourcedDiscData.wrap(primaryDisc));
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

    public void visitMountedFileSystems(BiConsumer<Byte, DiscData> consumer) {
        for (var entry : mountedFileSystems.byte2ObjectEntrySet()) {
            consumer.accept(entry.getByteKey(), entry.getValue().getDiscData());
        }
    }

    public @Nullable DiscData getFileSystem(byte driveLetter) {
        var sourcedData = mountedFileSystems.get(driveLetter);
        if (sourcedData == null)
            return null;
        return sourcedData.getDiscData();
    }

    public @Nullable DiscData getFileSystem(Path drive) {
        var driveText = drive.toString();
        if (driveText.length() != 3)
            return null;
        return getFileSystem(driveText.getBytes()[0]);
    }

    public @Nullable File getFile(Path path) {
        var driveName = path.getRoot();
        var fs = getFileSystem(driveName);
        if (fs != null)
            return primaryDisc.getFile(driveName.relativize(path));
        return null;
    }

    public Optional<File> getFileSafe(Path path) {
        var driveName = path.getRoot();
        var fs = getFileSystem(driveName);
        if (fs != null)
            return Optional.ofNullable(primaryDisc.getFile(driveName.relativize(path)));
        return Optional.empty();
    }

    public @Nullable Folder getFolder(Path path) {
        var driveName = path.getRoot();
        var fs = getFileSystem(driveName);
        if (fs != null)
            return primaryDisc.getFolder(driveName.relativize(path));
        return null;
    }

    public Optional<Folder> getFolderSafe(Path path) {
        var driveName = path.getRoot();
        var fs = getFileSystem(driveName);
        if (fs != null)
            return Optional.ofNullable(fs.getFolder(driveName.relativize(path)));
        return Optional.empty();
    }

    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    protected DiscData createFileSystem(RandomSource random) {
        var data = new DiscData();
        currentWorkingDirectory = DiscData.generatePCFileSystem(data, random);
        homeDirectory = currentWorkingDirectory;
        binariesDirectory = Path.of("C:/Binaries/");
        return data;
    }
}
