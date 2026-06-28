package net.ltxprogrammer.changed.ability.active;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.nbt.CompoundTag;

public class SimpleToggleAbilityInstance extends SimpleAbilityInstance {
    protected boolean active;

    public SimpleToggleAbilityInstance(AbstractAbility<SimpleToggleAbilityInstance> ability, IAbstractChangedEntity entity, boolean startActive) {
        super(ability, entity);
        this.active = startActive;
    }

    public boolean isActive() {
        return active;
    }

    public void forceActive(boolean state) {
        this.active = state;
    }

    @Override
    public void startUsing() {
        this.active = !this.active;
        super.startUsing();
    }

    @Override
    public void saveData(CompoundTag tag) {
        super.saveData(tag);
        tag.putBoolean("active", this.active);
    }

    @Override
    public void readData(CompoundTag tag) {
        super.readData(tag);
        this.active = tag.getBoolean("active");
    }
}
