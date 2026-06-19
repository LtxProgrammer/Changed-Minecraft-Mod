package net.ltxprogrammer.changed.ability.active.multiarm;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;

public class MantleAbility extends AbstractAbility<MantleAbilityInstance> {
    public MantleAbility() {
        super(MantleAbilityInstance::new);
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 30;
    }

    @Override
    public boolean shouldApplyCoolDown(IAbstractChangedEntity entity) {
        return false; // Manually applied
    }
}
