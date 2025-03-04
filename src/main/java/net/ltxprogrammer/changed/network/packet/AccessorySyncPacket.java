package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.data.AccessorySlots;
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
public class AccessorySyncPacket implements ChangedPacket {
    private final int entityId;
    private final boolean partial;
    private final AccessorySlots slots;

    public AccessorySyncPacket(int entityId, AccessorySlots slots) {
        this.entityId = entityId;
        this.partial = false;
        this.slots = slots;
    }

    public AccessorySyncPacket(int entityId, Map<AccessorySlotType, ItemStack> slots) {
        this.entityId = entityId;
        this.partial = true;
        this.slots = new AccessorySlots(null);
        this.slots.initialize(slots::containsKey, ignored -> {});
        slots.forEach(this.slots::setItem);
    }

    public AccessorySyncPacket(FriendlyByteBuf buffer) {
        this.entityId = buffer.readInt();
        this.partial = buffer.readBoolean();
        this.slots = new AccessorySlots(null);
        slots.readNetwork(buffer);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(entityId);
        buffer.writeBoolean(partial);
        slots.writeNetwork(buffer);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        final var context = contextSupplier.get();

        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT && UniversalDist.getLevel().getEntity(entityId) instanceof LivingEntity entity) {
            AccessorySlots.getForEntity(entity).ifPresent(accessorySlots -> accessorySlots.setAll(this.slots, !partial));

            context.setPacketHandled(true);
        } else if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
            context.getSender().openMenu(new SimpleMenuProvider((id, inv, player) -> new AccessoryAccessMenu(id, player), TextComponent.EMPTY));

            context.setPacketHandled(true);
        }
    }
}
