package net.ltxprogrammer.changed.data;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public record AccessorySlotContext<T extends LivingEntity>(T wearer, AccessorySlotType slotType, ItemStack stack) {
    public static <T extends LivingEntity> AccessorySlotContext<T> of(T wearer, AccessorySlotType slotType) {
        return new AccessorySlotContext<>(wearer, slotType,
                AccessorySlots.getForEntity(wearer).flatMap(slots -> slots.getItem(slotType)).orElse(ItemStack.EMPTY));
    }
}
