package net.ltxprogrammer.changed.block.entity;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.computers.BasicNIC;
import net.ltxprogrammer.changed.computers.DiscData;
import net.ltxprogrammer.changed.computers.SourcedDiscData;
import net.ltxprogrammer.changed.computers.generator.ConfiguredFileSystemGenerators;
import net.ltxprogrammer.changed.computers.protocol.*;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;

public class ServerStackBlockEntity extends BlockEntity implements NetworkInterface {
    public final RandomSource random = RandomSource.create();

    public final BasicNIC nic;

    /// Parallels an HDD or SSD in a computer. Saves with the block entity.
    protected DiscData primaryDisc;

    public ServerStackBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ChangedBlockEntities.SERVER_STACK.get(), blockPos, blockState);
        nic = new BasicNIC(Address.forBlock(blockPos.immutable()));
        nic.logicalAddress = this.random.nextInt();

        primaryDisc = createFileSystem(random);
    }

    protected void saveAdditional(CompoundTag tag) {
        tag.put("fs", this.primaryDisc.serialize());
    }

    public void load(CompoundTag tag) {
        this.primaryDisc = new DiscData(tag.getCompound("fs"), this::setChanged);
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
        if (packet instanceof DiscoveryProtocol discoveryProtocol && !discoveryProtocol.isReply()) {
            Set<Class<?>> protocols = new HashSet<>();
            protocols.add(DiscoveryProtocol.class);
            protocols.add(DeviceInfoProtocol.Query.class);
            nic.sendPacket(level, logicalSource, discoveryProtocol.intersect(protocols));
        }

        if (packet == DeviceInfoProtocol.Query.INSTANCE) {
            nic.sendPacket(level, logicalSource, new DeviceInfoProtocol(
                    Component.literal("Server"),
                    this.getBlockPos(),
                    Changed.modResource("server")
            ));
        }
    }

    public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, ServerStackBlockEntity blockEntity) {
        if (level instanceof ServerLevel serverLevel) {
            blockEntity.nic.tick(serverLevel, blockPos);
            blockEntity.nic.processPackets(serverLevel, blockEntity::handlePacket);
        }
    }

    protected DiscData createFileSystem(RandomSource random) {
        var data = new DiscData(this::setChanged);
        var generator = ConfiguredFileSystemGenerators.getGenerator(Changed.modResource("default_server"));
        if (generator == null)
            return data;

        generator.generate(random, data, (label, path) -> {});
        return data;
    }
}
