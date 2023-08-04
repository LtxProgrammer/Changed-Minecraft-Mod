package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.world.inventory.TaurSaddleMenu;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;

public class AccessSaddleAbility extends AbstractAbility<AccessSaddleAbilityInstance> {
    public AccessSaddleAbility() {
        super(AccessSaddleAbilityInstance::new);
    }

    @Override
    public boolean canUse(IAbstractLatex entity) {
        return true;
    }

    @Override
    public boolean canKeepUsing(IAbstractLatex entity) {
        return entity.getContainerMenu() instanceof TaurSaddleMenu;
    }

    @Override
    public void startUsing(IAbstractLatex entity) {
        entity.openMenu(new SimpleMenuProvider((p_52229_, p_52230_, p_52231_) ->
                new TaurSaddleMenu(p_52229_, p_52230_, null), TaurSaddleMenu.CONTAINER_TITLE));
    }

    @Override
    public void stopUsing(IAbstractLatex entity) {
        entity.closeContainer();
    }
}
