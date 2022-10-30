package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.init.ChangedTabs;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Supplier;

public class AbstractLatexBucket extends BucketItem {
    public AbstractLatexBucket(Supplier<? extends Fluid> supplier) {
        super(supplier, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).rarity(Rarity.COMMON).tab(ChangedTabs.TAB_CHANGED_ITEMS));
    }

    public static Supplier<Item> from(Supplier<? extends Fluid> fluid) {
        return () -> new AbstractLatexBucket(fluid);
    }
}
