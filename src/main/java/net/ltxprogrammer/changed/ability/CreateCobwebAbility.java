package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CreateCobwebAbility extends SimpleAbility {
    @Override
    public ResourceLocation getId() {
        return Changed.modResource("create_cobweb");
    }

    @Override
    public boolean canUse(Player player, LatexVariant<?> variant) {
        return true;
    }

    @Override
    public boolean canKeepUsing(Player player, LatexVariant<?> variant) { return false; }

    @Override
    public void startUsing(Player player, LatexVariant<?> variant) {
        player.addItem(new ItemStack(Items.COBWEB));
        player.causeFoodExhaustion(1.4f);
    }
    @Override
    public void tick(Player player, LatexVariant<?> variant) {}

    @Override
    public void stopUsing(Player player, LatexVariant<?> variant) {}
}
