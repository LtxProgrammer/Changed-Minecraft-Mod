package net.ltxprogrammer.changed.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;

public class DyeableClothingItem extends ClothingItem implements DyeableLeatherItem {
    public DyeableClothingItem() {
        super();
    }

    public DyeableClothingItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getColor(ItemStack itemStack) {
        CompoundTag compoundtag = itemStack.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : this.getDefaultColor();
    }

    protected int getDefaultColor() {
        return DyeableLeatherItem.DEFAULT_LEATHER_COLOR;
    }
}
