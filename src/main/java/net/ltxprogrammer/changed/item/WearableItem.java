package net.ltxprogrammer.changed.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public interface WearableItem {
    void wearTick(LivingEntity entity, ItemStack itemStack);
    boolean customWearRenderer();
    boolean allowedToKeepWearing(LivingEntity entity);

    @Mod.EventBusSubscriber
    class Event {
        @SubscribeEvent
        public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
            event.getEntityLiving().getArmorSlots().forEach(itemStack -> {
                if (itemStack.getItem() instanceof WearableItem wearableItem) {
                    if (!wearableItem.allowedToKeepWearing(event.getEntityLiving())) {
                        ItemStack nStack = itemStack.copy();
                        Block.popResource(event.getEntityLiving().getLevel(), event.getEntityLiving().blockPosition(), nStack);
                        itemStack.setCount(0);
                        return;
                    }

                    wearableItem.wearTick(event.getEntityLiving(), itemStack);
                }
            });
        }
    }
}
