package net.ltxprogrammer.changed.block.entity;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.computers.DiscData;
import net.ltxprogrammer.changed.computers.File;
import net.ltxprogrammer.changed.computers.Folder;
import net.ltxprogrammer.changed.computers.generator.ConfiguredFileSystemGenerators;
import net.ltxprogrammer.changed.computers.generator.FileSystemGenerator;
import net.ltxprogrammer.changed.computers.protocol.*;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.util.CollectionUtil;
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

public class WLANRouterBlockEntity extends BlockEntity implements NetworkInterface {
    public final RandomSource random = RandomSource.create();

    protected final NetworkInterface.Address physicalAddress;
    protected int logicalAddress;

    protected final List<NetworkInterface.Address> remoteConnectedPhysicalAddresses = new ArrayList<>();
    protected final Queue<Pair<Integer, Packet>> unprocessedPackets = new ArrayDeque<>();
    protected final Queue<Pair<NetworkInterface.Address, Frame>> outboundFrames = new ArrayDeque<>();

    public Path currentWorkingDirectory;
    public Path homeDirectory;
    public Path binariesDirectory;
    public DiscData localFileSystem = Util.make(new DiscData(this::setChanged), data -> {
        var generator = ConfiguredFileSystemGenerators.getGenerator(Changed.modResource("default_pc"));
        if (generator == null)
            return;

        generator.generate(random, data, this.configureDirectory());
        currentWorkingDirectory = homeDirectory;
    });

    protected FileSystemGenerator.DirectoryConsumer configureDirectory() {
        return (dir, path) -> {
            switch (dir) {
                case HOME_DIR -> homeDirectory = path;
                case BIN_DIR -> binariesDirectory = path;
            }
        };
    }

    public WLANRouterBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ChangedBlockEntities.WLAN_ROUTER.get(), blockPos, blockState);
        this.physicalAddress = Address.forBlock(blockPos);
        this.logicalAddress = this.random.nextInt();
    }

    protected void saveAdditional(CompoundTag tag) {
        tag.put("fs", this.localFileSystem.serialize());
    }

    public void load(CompoundTag tag) {
        this.localFileSystem = new DiscData(tag.getCompound("fs"), this::setChanged);
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

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, WLANRouterBlockEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            CollectionUtil.deplete(blockEntity.unprocessedPackets, packet -> {
                blockEntity.handlePacket(serverLevel, packet.getFirst(), packet.getSecond());
            });

            CollectionUtil.deplete(blockEntity.outboundFrames, frame -> {
                NetworkInterface.sendFrameToAddress(serverLevel, frame.getFirst(), blockEntity.physicalAddress, frame.getSecond());
            });
        }
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public DiscData getFileSystem(Path drive) {
        return localFileSystem;
    }

    public Either<File, File.Error> getFile(Path path) {
        var driveName = path.getRoot();
        var fs = getFileSystem(driveName);
        if (fs != null)
            return fs.getFile(driveName.relativize(path));
        return Either.right(File.Error.FILESYSTEM_NOT_FOUND);
    }

    public @Nullable Folder getFolder(Path path) {
        var driveName = path.getRoot();
        var fs = getFileSystem(driveName);
        if (fs != null)
            return fs.getFolder(driveName.relativize(path));
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
