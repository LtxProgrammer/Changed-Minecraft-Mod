package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet intended to sync NPC abilities with clients
 */
public class AccessorySyncPacket implements ChangedPacket {
    private final int entityId;
    private final AccessorySlots slots;

    public AccessorySyncPacket(int entityId, AccessorySlots slots) {
        this.entityId = entityId;
        this.slots = slots;
    }

    public AccessorySyncPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.slots = new AccessorySlots();
        slots.readNetwork(buffer);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(entityId);
        slots.writeNetwork(buffer);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        final var context = contextSupplier.get();

        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT && UniversalDist.getLevel().getEntity(entityId) instanceof LivingEntity entity) {
            AccessorySlots.getForEntity(entity).ifPresent(accessorySlots -> accessorySlots.setAll(this.slots));

            context.setPacketHandled(true);
        }
    }
}
