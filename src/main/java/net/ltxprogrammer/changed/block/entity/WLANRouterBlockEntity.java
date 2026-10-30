package net.ltxprogrammer.changed.block.entity;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.computers.*;
import net.ltxprogrammer.changed.computers.generator.FileSystemGenerator;
import net.ltxprogrammer.changed.computers.protocol.*;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.util.CollectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.*;

public class WLANRouterBlockEntity extends BlockEntity implements Router {
    public final RandomSource random = RandomSource.create();

    protected record OutboundFrame(Address origin, Address immediateSource, @Nullable Address destination, Frame frame, int ttl) {}

    private final BasicNIC nic;
    protected final Queue<OutboundFrame> outboundFrames = new ArrayDeque<>();

    public DiscData localFileSystem = new DiscData(this::setChanged, Changed.modResource("default_wlan_router"), random.nextLong());

    protected FileSystemGenerator.DirectoryConsumer configureDirectory() {
        return (dir, path) -> {};
    }

    public WLANRouterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ChangedBlockEntities.WLAN_ROUTER.get(), blockPos, blockState);
        this.nic = new BasicNIC(Address.forBlock(blockPos));
    }

    protected void saveAdditional(CompoundTag tag) {
        tag.put("fs", this.localFileSystem.serialize());
    }

    public void load(CompoundTag tag) {
        this.localFileSystem = new DiscData(tag.getCompound("fs"), this::setChanged);
    }

    public void handleFrame(ServerLevel level, NetworkInterface.Address logicalSource, Frame packet) {

    }

    @Override
    public void acceptNeighbor(ServerLevel level, Address physicalSource) {
        nic.acceptNeighbor(level, physicalSource);
    }

    @Override
    public void acceptFrame(ServerLevel level, Address physicalSource, Frame dataFrame) {
        nic.acceptFrame(level, physicalSource, dataFrame);
    }

    @Override
    public void sendOutFrame(ServerLevel level, @Nullable Address destination, Frame frame, int ttl) {
        nic.sendOutFrame(level, destination, frame, ttl);
    }

    @Override
    public void handoffUnicastFrame(ServerLevel level, Address origin, Address immediateSource, Address destination, Frame frame, int ttl) {
        outboundFrames.add(new OutboundFrame(origin, immediateSource, destination, frame, ttl - 1));
    }

    @Override
    public void handoffBroadcastFrame(ServerLevel level, Address origin, Address immediateSource, Frame frame, int ttl) {
        outboundFrames.add(new OutboundFrame(origin, immediateSource, null, frame, ttl - 1));
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, WLANRouterBlockEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            blockEntity.nic.tick(serverLevel, blockPos);
            blockEntity.nic.processFrames(serverLevel, blockEntity::handleFrame);

            CollectionUtil.deplete(blockEntity.outboundFrames, frame -> {
                NetworkInterface.sendFrameToAddress(serverLevel, frame.destination(), frame.origin(), blockEntity.nic.physicalAddress, frame.immediateSource(), frame.frame(), frame.ttl(), blockEntity.nic.nearbyAddresses);
            });
        }
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public DiscData getFileSystem(LexicalPath drive) {
        return localFileSystem.generateIfNecessary(this.level, this.configureDirectory());
    }

    public Either<File, File.Error> getFile(LexicalPath path) {
        var driveName = path.getRoot();
        var fs = getFileSystem(driveName);
        if (fs != null)
            return fs.getFile(driveName.relativize(path));
        return Either.right(File.Error.FILESYSTEM_NOT_FOUND);
    }

    public @Nullable Folder getFolder(LexicalPath path) {
        var driveName = path.getRoot();
        var fs = getFileSystem(driveName);
        if (fs != null)
            return fs.getFolder(driveName.relativize(path));
        return null;
    }

    public Optional<Folder> getFolderSafe(LexicalPath path) {
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
