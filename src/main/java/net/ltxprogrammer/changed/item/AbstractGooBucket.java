package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.GooType;
import net.ltxprogrammer.changed.fluid.AbstractLatexFluid;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class AbstractGooBucket extends BucketItem {
    public final Supplier<? extends AbstractLatexFluid> fluid;

    public AbstractGooBucket(Supplier<? extends AbstractLatexFluid> supplier) {
        super(supplier, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).rarity(Rarity.COMMON).tab(ChangedTabs.TAB_CHANGED_ITEMS));
        this.fluid = supplier;
    }

    public static Supplier<AbstractGooBucket> from(Supplier<? extends AbstractLatexFluid> fluid) {
        return () -> new AbstractGooBucket(fluid);
    }

    @Nullable
    public GooType getLatexType() {
        for (GooType t : GooType.values()) {
            if (this == t.gooBucket.get())
                return t;
        }

        return GooType.NEUTRAL;
    }
}
