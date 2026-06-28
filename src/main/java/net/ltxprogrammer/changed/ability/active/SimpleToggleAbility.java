package net.ltxprogrammer.changed.ability.active;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;

// A simple ability that is either on or off
public abstract class SimpleToggleAbility extends AbstractAbility<SimpleToggleAbilityInstance> {
    public SimpleToggleAbility(boolean startActive) {
        super((ability, entity) -> new SimpleToggleAbilityInstance(ability, entity, startActive));
    }

    public boolean isActive(IAbstractChangedEntity entity) {
        var instance = entity.getAbilityInstance(this);
        return instance != null && instance.isActive();
    }

    public void forceActive(IAbstractChangedEntity entity, boolean state) {
        var instance = entity.getAbilityInstance(this);
        if (instance != null)
            instance.forceActive(state);
    }
}
