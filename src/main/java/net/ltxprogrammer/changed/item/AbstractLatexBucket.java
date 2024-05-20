package net.ltxprogrammer.changed.item;

import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.fluid.AbstractLatexFluid;
import net.ltxprogrammer.changed.init.ChangedTabs;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.*;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class AbstractLatexBucket extends BucketItem {
    public final Supplier<? extends AbstractLatexFluid> fluid;

    public AbstractLatexBucket(Supplier<? extends AbstractLatexFluid> supplier) {
        super(supplier, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1).rarity(Rarity.COMMON).tab(ChangedTabs.TAB_CHANGED_ITEMS));
        this.fluid = supplier;
    }

    public static Supplier<AbstractLatexBucket> from(Supplier<? extends AbstractLatexFluid> fluid) {
        return () -> new AbstractLatexBucket(fluid);
    }

    @Nullable
    public LatexType getLatexType() {
        for (LatexType t : LatexType.values()) {
            if (this == t.gooBucket.get())
                return t;
        }

        return LatexType.NEUTRAL;
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidBucketWrapper(stack);
    }
}
