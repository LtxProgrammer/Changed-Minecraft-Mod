package net.ltxprogrammer.changed.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.function.Supplier;

// DO NOT USE OUTSIDE OF RENDERING GUI TABS
public abstract class DelayedItemStack {
    private static final Field itemSupplierField;

    static {
        Field itemSupplierField1;
        try {
            itemSupplierField1 = ItemStack.class.getDeclaredField("itemSupplier");
            itemSupplierField1.setAccessible(true);
        } catch (Exception ignored) {
            itemSupplierField1 = null;
        }
        itemSupplierField = itemSupplierField1;
    }

    public static ItemStack of(@NotNull Supplier<? extends Item> itemSupplier) {
        ItemStack stack = new ItemStack((ItemLike)null);
        try {
            itemSupplierField.set(stack, itemSupplier);
        } catch (Exception ignored) {}
        return stack;
    }
}
