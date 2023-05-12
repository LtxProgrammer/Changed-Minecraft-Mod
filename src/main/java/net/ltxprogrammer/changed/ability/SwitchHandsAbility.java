package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SwitchHandsAbility extends SimpleAbility {
    @Override
    public boolean canUse(Player player, LatexVariantInstance<?> variant) {
        return variant.abilityInstances.containsKey(ChangedAbilities.EXTRA_HANDS.get());
    }

    @Override
    public boolean canKeepUsing(Player player, LatexVariantInstance<?> variant) {
        return false;
    }

    @Override
    public void startUsing(Player player, LatexVariantInstance<?> variant) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        CompoundTag tag = player.getPersistentData();
        if (tag.contains("changed:extra_hands_rh"))
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.of(tag.getCompound("changed:extra_hands_rh")));
        if (tag.contains("changed:extra_hands_lh"))
            player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.of(tag.getCompound("changed:extra_hands_lh")));

        tag.put("changed:extra_hands_rh", mainHand.serializeNBT());
        tag.put("changed:extra_hands_lh", offHand.serializeNBT());
    }

    @Override
    public void tick(Player player, LatexVariantInstance<?> variant) {}

    @Override
    public void stopUsing(Player player, LatexVariantInstance<?> variant) {}
}
