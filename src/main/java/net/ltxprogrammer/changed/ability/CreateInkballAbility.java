package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
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
    public boolean canUse(Player player, LatexVariant<?> variant) {
        return player.getFoodData().getFoodLevel() > 6.0f || player.isCreative();
    }

    @Override
    public boolean canKeepUsing(Player player, LatexVariant<?> variant) { return false; }

    @Override
    public void startUsing(Player player, LatexVariant<?> variant) {
        player.addItem(new ItemStack(ChangedItems.LATEX_INKBALL.get()));
        player.causeFoodExhaustion(1.4f);
    }
    @Override
    public void tick(Player player, LatexVariant<?> variant) {}

    @Override
    public void stopUsing(Player player, LatexVariant<?> variant) {}
}
