package net.ltxprogrammer.changed.ability;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Collection;
import java.util.Collections;

public class SirenSingAbility extends AbstractAbility<SirenSingAbilityInstance> {
    public SirenSingAbility() {
        super(SirenSingAbilityInstance::new);
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.HOLD;
    }

    @Override
    public int getCoolDown(IAbstractChangedEntity entity) {
        return 90 * 20; // 90 Seconds
    }

    private static final Collection<Component> DESCRIPTION = Collections.singleton(new TranslatableComponent("ability.changed.siren_sing.desc"));

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }
}
