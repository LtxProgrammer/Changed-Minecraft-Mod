package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TransfurEntityEventPacket implements ChangedPacket {
    private final int entityId;
    private final byte eventId;

    public TransfurEntityEventPacket(Player host, byte event) {
        entityId = host.getId();
        eventId = event;
    }

    public TransfurEntityEventPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.eventId = buffer.readByte();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityId);
        buffer.writeByte(this.eventId);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        var context = contextSupplier.get();
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(UniversalDist.getLevel().getEntity(entityId)), variant -> {
                variant.getChangedEntity().handleEntityEvent(eventId);
                context.setPacketHandled(true);
            });
        }
    }
}
