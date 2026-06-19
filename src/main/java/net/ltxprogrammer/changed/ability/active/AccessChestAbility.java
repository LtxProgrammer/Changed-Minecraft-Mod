package net.ltxprogrammer.changed.ability.active;

import net.ltxprogrammer.changed.ability.AbstractAbility;

public class AccessChestAbility extends AbstractAbility<AccessChestAbilityInstance> {
    public AccessChestAbility() {
        super(AccessChestAbilityInstance::new);
    }
}
