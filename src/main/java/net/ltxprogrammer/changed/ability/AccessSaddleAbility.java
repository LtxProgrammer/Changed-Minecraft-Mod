package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.world.inventory.CentaurSaddleMenu;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;

public class AccessSaddleAbility extends AbstractAbility<AccessSaddleAbilityInstance> {
    public AccessSaddleAbility() {
        super(AccessSaddleAbilityInstance::new);
    }

    @Override
    public ResourceLocation getId() {
        return Changed.modResource("access_saddle");
    }

    @Override
    public boolean canUse(Player player, LatexVariant<?> variant) {
        return true;
    }

    @Override
    public boolean canKeepUsing(Player player, LatexVariant<?> variant) {
        return player.containerMenu instanceof CentaurSaddleMenu;
    }

    @Override
    public void startUsing(Player player, LatexVariant<?> variant) {
        player.openMenu(new SimpleMenuProvider((p_52229_, p_52230_, p_52231_) ->
                new CentaurSaddleMenu(p_52229_, p_52230_, null), CentaurSaddleMenu.CONTAINER_TITLE));
    }

    @Override
    public void stopUsing(Player player, LatexVariant<?> variant) {
        player.closeContainer();
    }
}
