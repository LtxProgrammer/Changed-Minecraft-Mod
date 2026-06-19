package net.ltxprogrammer.changed.ability.active.multiarm;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Collections;

public class SwitchHandsAbility extends AbstractAbility<SwitchHandsAbilityInstance> {
    public SwitchHandsAbility() {
        super(SwitchHandsAbilityInstance::new);
    }

    private static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.switch_hands.desc"));

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }
}
