package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.world.inventory.ExtraHandsMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ExtraHandsAbility extends SimpleAbility {
    @Override
    public ResourceLocation getId() {
        return Changed.modResource("extra_hands");
    }

    @Override
    public boolean canUse(Player player, LatexVariant<?> variant) {
        return true;
    }

    @Override
    public boolean canKeepUsing(Player player, LatexVariant<?> variant) {
        return player.containerMenu instanceof ExtraHandsMenu;
    }

    @Override
    public void startUsing(Player player, LatexVariant<?> variant) {
        player.openMenu(new SimpleMenuProvider((p_52229_, p_52230_, p_52231_) ->
                new ExtraHandsMenu(p_52229_, p_52230_, null), ExtraHandsMenu.CONTAINER_TITLE));
    }

    @Override
    public void tick(Player player, LatexVariant<?> variant) {
        ((ExtraHandsMenu)player.containerMenu).tick(player);
    }

    @Override
    public void stopUsing(Player player, LatexVariant<?> variant) {
        player.closeContainer();
    }

    @Override
    public void onRemove(Player player, LatexVariant<?> variant) {
        CompoundTag tag = player.getPersistentData();
        if (tag.contains("changed:extra_hands_rh"))
            player.drop(ItemStack.of(tag.getCompound("changed:extra_hands_rh")), true);
        if (tag.contains("changed:extra_hands_lh"))
            player.drop(ItemStack.of(tag.getCompound("changed:extra_hands_lh")), true);
        tag.remove("changed:extra_hands_rh");
        tag.remove("changed:extra_hands_lh");
    }
}
