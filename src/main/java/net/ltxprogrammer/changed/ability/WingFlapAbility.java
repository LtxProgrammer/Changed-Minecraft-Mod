package net.ltxprogrammer.changed.ability;

import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeMod;

import java.util.Collection;
import java.util.Collections;

public class WingFlapAbility extends AbstractAbility<WingFlapAbilityInstance> {
    public WingFlapAbility() {
        super(WingFlapAbilityInstance::new);
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.INSTANT;
    }

    private static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.wing_flap.desc"));

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }
}
