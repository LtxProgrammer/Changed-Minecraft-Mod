package net.ltxprogrammer.changed.block;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;

public interface WearableBlock {
    EquipmentSlot getEquipmentSlot();
    void wearTick(LivingEntity entity, ItemStack itemStack);

    static void handleTickEvent(LivingEvent.LivingTickEvent event) {
        event.getEntity().getArmorSlots().forEach(itemStack -> {
            if (itemStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof WearableBlock wearableBlock) {
                wearableBlock.wearTick(event.getEntity(), itemStack);
            }
        });
    }
}
