package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.data.AccessorySlotContext;
import net.minecraft.world.item.ItemStack;

public interface AccessoryItem {
    default void accessoryBreak(AccessorySlotContext<?> slotContext) {}
}
