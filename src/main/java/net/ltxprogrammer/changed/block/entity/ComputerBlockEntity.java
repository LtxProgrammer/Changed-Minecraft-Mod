package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.computers.BasicNIC;
import net.ltxprogrammer.changed.computers.DiscData;
import net.ltxprogrammer.changed.computers.File;
import net.ltxprogrammer.changed.computers.Folder;
import net.ltxprogrammer.changed.computers.protocol.DiscoveryProtocol;
import net.ltxprogrammer.changed.computers.protocol.Frame;
import net.ltxprogrammer.changed.computers.protocol.NetworkInterface;
import net.ltxprogrammer.changed.computers.protocol.Packet;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.Util;
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
import java.util.Optional;
import java.util.Set;

public class ComputerBlockEntity extends BaseContainerBlockEntity implements StackedContentsCompatible, NetworkInterface {
    public final RandomSource random = RandomSource.create();

    public NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);
    public final BasicNIC nic;
    public Path currentWorkingDirectory;
    public Path homeDirectory;
    public Path binariesDirectory;
    public DiscData localFileSystem = Util.make(new DiscData(), data -> {
        currentWorkingDirectory = DiscData.generatePCFileSystem(data, random);
        homeDirectory = currentWorkingDirectory;
        binariesDirectory = Path.of("C:/Binaries/");
    });
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
    }

    public boolean isEmpty() {
        for(ItemStack itemstack : this.items) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public ItemStack getItem(int p_58328_) {
        return this.items.get(p_58328_);
    }

    public ItemStack removeItem(int p_58330_, int p_58331_) {
        return ContainerHelper.removeItem(this.items, p_58330_, p_58331_);
    }

    public ItemStack removeItemNoUpdate(int p_58387_) {
        return ContainerHelper.takeItem(this.items, p_58387_);
    }

    public void setItem(int slotId, ItemStack stack) {
        ItemStack existingItem = this.items.get(slotId);
        boolean flag = !stack.isEmpty() && ItemStack.isSameItemSameTags(stack, existingItem);
        this.items.set(slotId, stack);
        if (stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }

        if (slotId == 0 && !flag) {
            this.setChanged();
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

    public boolean canPlaceItem(int slotId, ItemStack stack) {
        if (slotId == 0)
            return stack.is(ChangedItems.COMPACT_DISC.get());
        else
            return false;
    }

    public void clearContent() {
        this.items.clear();
    }

    public void fillStackedContents(StackedContents contents) {
        for(ItemStack itemstack : this.items) {
            contents.accountStack(itemstack);
        }
    }

    protected void saveAdditional(CompoundTag tag) {
        ContainerHelper.saveAllItems(tag, this.items);
        tag.put("fs", this.localFileSystem.serialize());
    }

    public void load(CompoundTag tag) {
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        this.localFileSystem = new DiscData(tag.getCompound("fs"));
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
            if (menu != null)
                protocols.addAll(menu.currentApplication().getNetworkProtocols());
            nic.sendPacket(level, logicalSource, discoveryProtocol.intersect(protocols));
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

    public DiscData getFileSystem(Path drive) {
        return localFileSystem;
    }

    public @Nullable File getFile(Path path) {
        var driveName = path.getRoot();
        var fs = getFileSystem(driveName);
        if (fs != null)
            return localFileSystem.getFile(driveName.relativize(path));
        return null;
    }

    public Optional<File> getFileSafe(Path path) {
        var driveName = path.getRoot();
        var fs = getFileSystem(driveName);
        if (fs != null)
            return Optional.ofNullable(localFileSystem.getFile(driveName.relativize(path)));
        return Optional.empty();
    }

    public @Nullable Folder getFolder(Path path) {
        var driveName = path.getRoot();
        var fs = getFileSystem(driveName);
        if (fs != null)
            return localFileSystem.getFolder(driveName.relativize(path));
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
}
