package net.ltxprogrammer.changed.block.entity;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.computers.DiscData;
import net.ltxprogrammer.changed.computers.File;
import net.ltxprogrammer.changed.computers.Folder;
import net.ltxprogrammer.changed.computers.protocol.*;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.*;

public class RouterBlockEntity extends BlockEntity implements NetworkInterface {
    public final RandomSource random = RandomSource.create();

    protected final NetworkInterface.Address physicalAddress;
    protected int logicalAddress;

    protected final List<NetworkInterface.Address> remoteConnectedPhysicalAddresses = new ArrayList<>();
    protected final Queue<Pair<Integer, Packet>> unprocessedPackets = new ArrayDeque<>();
    protected final Queue<Pair<NetworkInterface.Address, Frame>> outboundFrames = new ArrayDeque<>();

    public Path currentWorkingDirectory;
    public Path homeDirectory;
    public Path binariesDirectory;
    public DiscData localFileSystem = Util.make(new DiscData(), data -> {
        currentWorkingDirectory = DiscData.generatePCFileSystem(data, random);
        homeDirectory = currentWorkingDirectory;
        binariesDirectory = Path.of("C:/Binaries/");
    });

    public RouterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ChangedBlockEntities.ROUTER.get(), blockPos, blockState);
        this.physicalAddress = Address.forBlock(blockPos);
    }

    protected void saveAdditional(CompoundTag tag) {
        tag.put("fs", this.localFileSystem.serialize());
    }

    public void load(CompoundTag tag) {
        this.localFileSystem = new DiscData(tag.getCompound("fs"));
    }

    @Override
    public void acceptFrame(ServerLevel level, Address physicalSource, Frame dataFrame) {
        if (dataFrame instanceof IPFrame<?> ipFrame) {
            if (ipFrame.logicalDestination() == this.logicalAddress) // Consume packet
                unprocessedPackets.add(Pair.of(ipFrame.logicalSource(), ipFrame.packet()));
            else if (ipFrame.isBroadcast()) { // Consume and forward packet
                unprocessedPackets.add(Pair.of(ipFrame.logicalSource(), ipFrame.packet()));
                remoteConnectedPhysicalAddresses.forEach(remoteAddress -> {
                    if (remoteAddress.equals(physicalSource))
                        return;
                    outboundFrames.add(Pair.of(remoteAddress, dataFrame));
                });
            } else { // TODO: smarter forwarding
                remoteConnectedPhysicalAddresses.forEach(remoteAddress -> {
                    if (remoteAddress.equals(physicalSource))
                        return;
                    outboundFrames.add(Pair.of(remoteAddress, dataFrame));
                });
            }
        } else if (dataFrame instanceof NetworkDiscoveryFrame networkDiscoveryFrame) {
            if (!networkDiscoveryFrame.isReply()) {
                if (networkDiscoveryFrame.commitConnection() && !this.remoteConnectedPhysicalAddresses.contains(physicalSource))
                    this.remoteConnectedPhysicalAddresses.add(physicalSource);
                NetworkInterface.sendFrameToAddress(level, physicalSource, this.physicalAddress,
                        networkDiscoveryFrame.reply());
            }
        }
    }

    @Override
    public void sendFrame(ServerLevel level, Frame dataFrame) {

    }

    public void handlePacket(ServerLevel level, int logicalSource, Packet packet) {
        /*if (packet instanceof DiscoveryProtocol discoveryProtocol && !discoveryProtocol.isReply()) {
            Set<Class<?>> protocols = new HashSet<>();
            protocols.add(DiscoveryProtocol.class);
            if (menu != null)
                protocols.addAll(menu.currentApplication().getNetworkProtocols());
            nic.sendPacket(level, logicalSource, discoveryProtocol.intersect(protocols));
        }

        if (menu != null)
            menu.currentApplication().handlePacket(level, logicalSource, packet);*/
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, RouterBlockEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            for (var packet : blockEntity.unprocessedPackets) {
                blockEntity.handlePacket(serverLevel, packet.getFirst(), packet.getSecond());
            }

            for (var frame : blockEntity.outboundFrames) {
                NetworkInterface.sendFrameToAddress(serverLevel, frame.getFirst(), blockEntity.physicalAddress, frame.getSecond());
            }
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
