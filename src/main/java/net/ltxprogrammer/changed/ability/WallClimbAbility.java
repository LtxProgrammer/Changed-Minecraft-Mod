package net.ltxprogrammer.changed.ability;

import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Collections;

public class WallClimbAbility extends AbstractAbility<WallClimbAbilityInstance> {
    public WallClimbAbility() {
        super(WallClimbAbilityInstance::new);
    }

    private static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.wall_climb.desc"));

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }
}
