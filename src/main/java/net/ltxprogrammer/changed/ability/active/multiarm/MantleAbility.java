package net.ltxprogrammer.changed.ability.active.multiarm;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Collections;

public class MantleAbility extends AbstractAbility<MantleAbilityInstance> {
    public MantleAbility() {
        super(MantleAbilityInstance::new);
    }

    public static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.mantle.desc"));

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
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
