package net.ltxprogrammer.changed.block;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public interface WearableBlock {
    EquipmentSlot getEquipmentSlot();
    void wearTick(LivingEntity entity, ItemStack itemStack);

    @Mod.EventBusSubscriber
    class Event {
        @SubscribeEvent
        public static void onEntityTick(LivingEvent.LivingUpdateEvent event) {
            event.getEntityLiving().getArmorSlots().forEach(itemStack -> {
                if (itemStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof WearableBlock wearableBlock) {
                    wearableBlock.wearTick(event.getEntityLiving(), itemStack);
                }
            });
        }
    }
}
