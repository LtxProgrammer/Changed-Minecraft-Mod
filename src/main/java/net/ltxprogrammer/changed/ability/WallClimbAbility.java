package net.ltxprogrammer.changed.ability;

public class WallClimbAbility extends AbstractAbility<WallClimbAbilityInstance> {
    public WallClimbAbility() {
        super(WallClimbAbilityInstance::new);
    }
}
