package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class CreateInkballAbility extends SimpleAbility {
    @Override
    public ResourceLocation getId() {
        return Changed.modResource("create_inkball");
    }

    @Override
    public boolean canUse(Player player, LatexVariantInstance<?> variant) {
        return player.getFoodData().getFoodLevel() > 6.0f || player.isCreative();
    }

    @Override
    public boolean canKeepUsing(Player player, LatexVariantInstance<?> variant) { return false; }

    @Override
    public void startUsing(Player player, LatexVariantInstance<?> variant) {
        player.addItem(new ItemStack(ChangedItems.LATEX_INKBALL.get()));
        player.causeFoodExhaustion(1.4f);
    }
    @Override
    public void tick(Player player, LatexVariantInstance<?> variant) {}

    @Override
    public void stopUsing(Player player, LatexVariantInstance<?> variant) {}
}
