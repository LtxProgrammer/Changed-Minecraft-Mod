package net.ltxprogrammer.changed.ability.active;

import net.ltxprogrammer.changed.ability.AbstractAbility;

import java.util.function.BiFunction;

public class HypnosisControlAbility extends AbstractAbility<HypnosisControlAbilityInstance> {
    public HypnosisControlAbility() {
        super(HypnosisControlAbilityInstance::new);
    }
}
