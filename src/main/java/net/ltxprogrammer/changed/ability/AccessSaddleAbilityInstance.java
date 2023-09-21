package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.init.ChangedGameRules;
import net.ltxprogrammer.changed.world.inventory.TaurSaddleMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class AccessSaddleAbilityInstance extends AbstractAbilityInstance {
    public ItemStack saddle = ItemStack.EMPTY;
    public ItemStack chest = ItemStack.EMPTY;

    public AccessSaddleAbilityInstance(AbstractAbility<AccessSaddleAbilityInstance> ability, IAbstractLatex entity) {
        super(ability, entity);
    }

    @Override
    public boolean canUse() {
        return ability.canUse(entity);
    }

    @Override
    public boolean canKeepUsing() {
        return ability.canKeepUsing(entity);
    }

    @Override
    public void startUsing() {
        ability.startUsing(entity);
    }

    @Override
    public void tick() {
        if (entity.getContainerMenu() instanceof TaurSaddleMenu taurSaddleMenu)
            if (taurSaddleMenu.tick(this))
                ability.setDirty(this);
    }

    @Override
    public void stopUsing() {
        ability.stopUsing(entity);
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
        if (entity.isDeadOrDying() && entity.getLevel().getGameRules().getBoolean(ChangedGameRules.RULE_KEEP_FORM))
            return;

        if (saddle != null)
            entity.drop(saddle, true);
        if (chest != null)
            entity.drop(chest, true);
        saddle = null;
        chest = null;
    }
}
