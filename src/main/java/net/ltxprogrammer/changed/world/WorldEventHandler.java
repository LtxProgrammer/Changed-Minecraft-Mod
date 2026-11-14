package net.ltxprogrammer.changed.world;

import net.ltxprogrammer.changed.data.AccessorySlots;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public abstract class WorldEventHandler {
    public static void onEmptySwingEvent(PlayerInteractEvent.LeftClickEmpty event) {
        AccessorySlots.getForEntity(event.getEntity()).ifPresent(slots -> slots.onEntitySwing(event.getHand()));
    }

    public static void onEntityAttackedEvent(LivingHurtEvent event) {
        AccessorySlots.getForEntity(event.getEntity()).map(slots -> slots.onEntityDamage(event.getSource(), event.getAmount()))
                .ifPresent(event::setAmount);
    }
}
