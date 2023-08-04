package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.world.inventory.ExtraHandsMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ExtraHandsAbility extends SimpleAbility {
    @Override
    public boolean canUse(IAbstractLatex entity) {
        return true;
    }

    @Override
    public boolean canKeepUsing(IAbstractLatex entity) {
        return entity.getContainerMenu() instanceof ExtraHandsMenu;
    }

    @Override
    public void startUsing(IAbstractLatex entity) {
        entity.openMenu(new SimpleMenuProvider((p_52229_, p_52230_, p_52231_) ->
                new ExtraHandsMenu(p_52229_, p_52230_, null), ExtraHandsMenu.CONTAINER_TITLE));
    }

    @Override
    public void tick(IAbstractLatex entity) {
        if (entity.getContainerMenu() instanceof ExtraHandsMenu extraHandsMenu)
            extraHandsMenu.tick(entity);
    }

    @Override
    public void stopUsing(IAbstractLatex entity) {
        entity.closeContainer();
    }

    @Override
    public void onRemove(IAbstractLatex entity) {
        CompoundTag tag = entity.getPersistentData();
        if (tag.contains("changed:extra_hands_rh"))
            entity.drop(ItemStack.of(tag.getCompound("changed:extra_hands_rh")), true);
        if (tag.contains("changed:extra_hands_lh"))
            entity.drop(ItemStack.of(tag.getCompound("changed:extra_hands_lh")), true);
        tag.remove("changed:extra_hands_rh");
        tag.remove("changed:extra_hands_lh");
    }
}
