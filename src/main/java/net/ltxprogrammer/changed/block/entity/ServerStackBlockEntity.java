package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.computers.BasicNIC;
import net.ltxprogrammer.changed.computers.DiscData;
import net.ltxprogrammer.changed.computers.Permissions;
import net.ltxprogrammer.changed.computers.generator.FileSystemGenerator;
import net.ltxprogrammer.changed.computers.protocol.*;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class ServerStackBlockEntity extends BlockEntity implements NetworkInterface {
    public final RandomSource random = RandomSource.create();

    public final BasicNIC nic;

    /// Parallels an HDD or SSD in a computer. Saves with the block entity.
    protected DiscData primaryDisc;

    public ServerStackBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ChangedBlockEntities.SERVER_STACK.get(), blockPos, blockState);
        this.nic = new BasicNIC(Address.forBlock(blockPos));

        primaryDisc = createFileSystem(random);
    }

    protected void saveAdditional(CompoundTag tag) {
        tag.put("fs", this.primaryDisc.serialize());
    }

    public void load(CompoundTag tag) {
        this.primaryDisc = new DiscData(tag.getCompound("fs"), this::setChanged);
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

    public void handleFrame(ServerLevel level, Address source, Frame packet) {
        if (packet instanceof DiscoveryProtocol discoveryProtocol && !discoveryProtocol.isReply()) {
            Set<Class<?>> protocols = new HashSet<>();
            protocols.add(DiscoveryProtocol.class);
            protocols.add(DeviceInfoProtocol.Query.class);
            protocols.add(FileSystemShareProtocol.Query.class);
            nic.sendOutFrame(level, source, discoveryProtocol.intersect(protocols));
        }

        if (packet == DeviceInfoProtocol.Query.INSTANCE) {
            nic.sendOutFrame(level, source, new DeviceInfoProtocol(
                    Component.literal("Server"),
                    this.getBlockPos(),
                    Changed.modResource("server")
            ));
        }

        if (packet == FileSystemShareProtocol.Query.INSTANCE) {
            nic.sendOutFrame(level, source, new FileSystemShareProtocol(
                    primaryDisc.generateIfNecessary(level, this.configureDirectory()).getRootFolder(),
                    Permissions.READ_ONLY
            ));
        }
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, ServerStackBlockEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            blockEntity.nic.tick(serverLevel, blockPos);
            blockEntity.nic.processFrames(serverLevel, blockEntity::handleFrame);
        }
    }

    protected FileSystemGenerator.DirectoryConsumer configureDirectory() {
        return (dir, path) -> {
            /*switch (dir) {
                case HOME_DIR -> homeDirectory = path;
                case BIN_DIR -> binariesDirectory = path;
            }*/
        };
    }

    protected DiscData createFileSystem(RandomSource random) {
        return new DiscData(this::setChanged, Changed.modResource("default_server"), random.nextLong());
    }
}
