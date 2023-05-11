package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class SimpleCreateItemAbility extends SimpleAbility {
    public SimpleCreateItemAbility(Supplier<ItemStack> itemSupplier, float exhaustion, float minimumHunger) {
        this.itemSupplier = itemSupplier;
        this.exhaustion = exhaustion;
        this.minimumHunger = minimumHunger;
    }

    private final Supplier<ItemStack> itemSupplier;
    private final float exhaustion;
    private final float minimumHunger;

    @Override
    public boolean canUse(Player player, LatexVariantInstance<?> variant) {
        return player.getFoodData().getFoodLevel() > minimumHunger || player.isCreative();
    }

    @Override
    public boolean canKeepUsing(Player player, LatexVariantInstance<?> variant) { return false; }

    @Override
    public void startUsing(Player player, LatexVariantInstance<?> variant) {
        var item = itemSupplier.get();
        if (!player.addItem(item))
            player.drop(item, false);
        if (!player.isCreative())
            player.causeFoodExhaustion(exhaustion);
    }
}
