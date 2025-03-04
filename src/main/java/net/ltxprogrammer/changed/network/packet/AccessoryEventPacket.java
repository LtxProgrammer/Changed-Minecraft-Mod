package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.inventory.AccessoryAccessMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Packet intended to sync accessory slots with clients
 * Client copies all data into given entity
 * Server opens the sender's accessory menu
 */
public class AccessoryEventPacket implements ChangedPacket {
    private final int entityId;
    private final AccessorySlotType slotType;
    private final int eventId;

    public AccessoryEventPacket(int entityId, AccessorySlotType slotType, int eventId) {
        this.entityId = entityId;
        this.slotType = slotType;
        this.eventId = eventId;
    }

    public AccessoryEventPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.slotType = ChangedRegistry.ACCESSORY_SLOTS.get().getValue(buffer.readInt());
        this.eventId = buffer.readInt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(entityId);
        buffer.writeInt(ChangedRegistry.ACCESSORY_SLOTS.get().getID(slotType));
        buffer.writeInt(eventId);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        final var context = contextSupplier.get();

        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT && UniversalDist.getLevel().getEntity(entityId) instanceof LivingEntity entity) {
            AccessorySlots.getForEntity(entity).flatMap(slots -> slots.getItem(slotType))
                    .ifPresent(itemStack -> slotType.handleEvent(entity, itemStack, eventId));

            context.setPacketHandled(true);
        }
    }
}
