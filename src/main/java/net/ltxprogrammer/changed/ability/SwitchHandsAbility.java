package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class SwitchHandsAbility extends SimpleAbility {
    @Override
    public boolean canUse(IAbstractLatex entity) {
        return entity.getAbilityInstance(ChangedAbilities.EXTRA_HANDS.get()) != null;
    }

    @Override
    public boolean canKeepUsing(IAbstractLatex entity) {
        return false;
    }

    @Override
    public void startUsing(IAbstractLatex entity) {
        ItemStack mainHand = entity.getEntity().getMainHandItem();
        ItemStack offHand = entity.getEntity().getOffhandItem();

        entity.getEntity().setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        entity.getEntity().setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        CompoundTag tag = entity.getPersistentData();
        if (tag.contains("changed:extra_hands_rh"))
            entity.getEntity().setItemInHand(InteractionHand.MAIN_HAND, ItemStack.of(tag.getCompound("changed:extra_hands_rh")));
        if (tag.contains("changed:extra_hands_lh"))
            entity.getEntity().setItemInHand(InteractionHand.OFF_HAND, ItemStack.of(tag.getCompound("changed:extra_hands_lh")));

        tag.put("changed:extra_hands_rh", mainHand.serializeNBT());
        tag.put("changed:extra_hands_lh", offHand.serializeNBT());
    }

    @Override
    public void tick(IAbstractLatex entity) {}

    @Override
    public void stopUsing(IAbstractLatex entity) {}
}
