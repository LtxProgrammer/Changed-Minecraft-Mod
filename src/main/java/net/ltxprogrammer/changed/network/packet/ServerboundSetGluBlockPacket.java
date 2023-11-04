package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.block.entity.GluBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundSetGluBlockPacket implements ChangedPacket {
    private final BlockPos pos;
    private final int size;
    private final boolean hasDoor;
    private final GluBlockEntity.JointType jointType;
    private final String finalState;

    public ServerboundSetGluBlockPacket(FriendlyByteBuf buffer) {
        this.pos = buffer.readBlockPos();
        this.size = buffer.readInt();
        this.hasDoor = buffer.readBoolean();
        this.jointType = GluBlockEntity.JointType.byName(buffer.readUtf()).orElse(GluBlockEntity.JointType.ENTRANCE);
        this.finalState = buffer.readUtf();
    }

    public ServerboundSetGluBlockPacket(BlockPos pos, int size, boolean hasDoor, GluBlockEntity.JointType jointType, String finalState) {
        this.pos = pos;
        this.size = size;
        this.hasDoor = hasDoor;
        this.jointType = jointType;
        this.finalState = finalState;
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeInt(size);
        buffer.writeBoolean(hasDoor);
        buffer.writeUtf(jointType.getSerializedName());
        buffer.writeUtf(finalState);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        var level = context.getSender().level;
        var blockState = level.getBlockState(pos);
        var blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof GluBlockEntity gluBlockEntity) {
            gluBlockEntity.setSize(size);
            gluBlockEntity.setHasDoor(hasDoor);
            gluBlockEntity.setJointType(jointType);
            gluBlockEntity.setFinalState(finalState);
            gluBlockEntity.setChanged();
            level.sendBlockUpdated(pos, blockState, blockState, 3);
        }

        context.setPacketHandled(true);
    }
}
