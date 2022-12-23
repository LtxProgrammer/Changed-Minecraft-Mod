package net.ltxprogrammer.changed.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public interface WearableItem {
    void wearTick(LivingEntity entity, ItemStack itemStack);
    boolean customWearRenderer();

    @Mod.EventBusSubscriber
    class Event {
        @SubscribeEvent
        public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
            event.getEntityLiving().getArmorSlots().forEach(itemStack -> {
                if (itemStack.getItem() instanceof WearableItem wearableBlock) {
                    wearableBlock.wearTick(event.getEntityLiving(), itemStack);
                }
            });
        }
    }
}
