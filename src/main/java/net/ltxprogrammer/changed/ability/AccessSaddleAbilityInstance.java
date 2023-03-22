package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.world.inventory.TaurSaddleMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class AccessSaddleAbilityInstance extends AbstractAbilityInstance {
    public ItemStack saddle = ItemStack.EMPTY;
    public ItemStack chest = ItemStack.EMPTY;

    public AccessSaddleAbilityInstance(AbstractAbility<AccessSaddleAbilityInstance> ability, Player player, LatexVariantInstance<?> variant) {
        super(ability, player, variant);
    }

    @Override
    public boolean canUse() {
        return ability.canUse(player, variant);
    }

    @Override
    public boolean canKeepUsing() {
        return ability.canKeepUsing(player, variant);
    }

    @Override
    public void startUsing() {
        ability.startUsing(player, variant);
    }

    @Override
    public void tick() {
        if (((TaurSaddleMenu)player.containerMenu).tick(this))
            ability.setDirty(this);
    }

    @Override
    public void stopUsing() {
        ability.stopUsing(player, variant);
    }

    @Override
    public void saveData(CompoundTag tag) {
        super.saveData(tag);
        if (saddle != null)
            tag.put("saddle", saddle.serializeNBT());
        if (chest != null)
            tag.put("chest", chest.serializeNBT());
    }

    @Override
    public void readData(CompoundTag tag) {
        super.readData(tag);
        if (tag.contains("saddle"))
            saddle = ItemStack.of(tag.getCompound("saddle"));
        if (tag.contains("chest"))
            chest = ItemStack.of(tag.getCompound("chest"));
    }

    @Override
    public void onRemove() {
        super.onRemove();
        if (player.isDeadOrDying() && player.level.getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_FORM))
            return;

        if (saddle != null)
            player.drop(saddle, true);
        if (chest != null)
            player.drop(chest, true);
        saddle = null;
        chest = null;
    }
}
