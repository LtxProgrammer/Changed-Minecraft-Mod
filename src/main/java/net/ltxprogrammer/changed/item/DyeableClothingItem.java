package net.ltxprogrammer.changed.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;

public interface DyeableClothingItem extends DyeableLeatherItem {
    @Override
    default int getColor(ItemStack itemStack) {
        CompoundTag compoundtag = itemStack.getTagElement(TAG_DISPLAY);
        return compoundtag != null && compoundtag.contains(TAG_COLOR, 99) ? compoundtag.getInt(TAG_COLOR) : this.getDefaultColor();
    }

    default int getDefaultColor() {
        return DEFAULT_LEATHER_COLOR;
    }
}
