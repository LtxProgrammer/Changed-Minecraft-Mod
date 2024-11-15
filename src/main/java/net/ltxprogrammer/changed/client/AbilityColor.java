package net.ltxprogrammer.changed.client;

import net.ltxprogrammer.changed.ability.AbstractAbilityInstance;

import java.util.Optional;

public interface AbilityColor {
    Optional<Integer> getColor(AbstractAbilityInstance abilityInstance, int layer);
}
