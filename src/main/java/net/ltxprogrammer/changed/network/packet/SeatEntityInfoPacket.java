package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.entity.SeatableBlockEntity;
import net.ltxprogrammer.changed.entity.SeatEntity;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Optional;
import java.util.function.Supplier;

public class SeatEntityInfoPacket implements ChangedPacket {
    private BlockPos position;
    private Optional<Integer> id;

    public SeatEntityInfoPacket(BlockPos position) {
        this.position = position;
        this.id = Optional.empty();
    }

    public SeatEntityInfoPacket(SeatEntity entity) {
        this.position = entity.getAttachedBlockPos();
        this.id = Optional.of(entity.getId());
    }

    public SeatEntityInfoPacket(FriendlyByteBuf buffer) {
        this.position = buffer.readBlockPos();
        this.id = buffer.readOptional(FriendlyByteBuf::readInt);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(this.position);
        buffer.writeOptional(this.id, FriendlyByteBuf::writeInt);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) { // Echo with entity
            final SeatEntityInfoPacket response;
            if (context.getSender().getLevel().getBlockEntity(this.position) instanceof SeatableBlockEntity seat) {
                if (seat.getEntityHolder() != null)
                    response = new SeatEntityInfoPacket(seat.getEntityHolder());
                else
                    response = new SeatEntityInfoPacket(this.position);
            } else {
                response = new SeatEntityInfoPacket(this.position);
            }

            Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(context::getSender), response);
            context.setPacketHandled(true);
        } else {
            if (UniversalDist.getLevel().getBlockEntity(this.position) instanceof SeatableBlockEntity seat) {
                this.id.ifPresentOrElse(id -> {
                    if (seat.getEntityHolder() != null) {
                        seat.getEntityHolder().setId(id);
                    } else {
                        var entity = ChangedEntities.SEAT_ENTITY.get().create(UniversalDist.getLevel());
                        entity.setId(id);
                        seat.setEntityHolder(entity);
                    }
                }, () -> {
                    seat.setEntityHolder(null);
                });
                context.setPacketHandled(true);
            }
        }
    }
}
