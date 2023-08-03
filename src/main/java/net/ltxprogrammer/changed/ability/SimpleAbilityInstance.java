package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class SimpleAbilityInstance extends AbstractAbilityInstance {
    public SimpleAbilityInstance(AbstractAbility<SimpleAbilityInstance> ability, IAbstractLatex entity) {
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
        ability.tick(entity);
    }

    @Override
    public void stopUsing() {
        ability.stopUsing(entity);
    }

    @Override
    public void onRemove() {
        ability.onRemove(entity);
    }

    @Override
    public void saveData(CompoundTag tag) {
        ability.saveData(tag, entity);
    }

    @Override
    public void readData(CompoundTag tag) {
        ability.readData(tag, entity);
    }
}
