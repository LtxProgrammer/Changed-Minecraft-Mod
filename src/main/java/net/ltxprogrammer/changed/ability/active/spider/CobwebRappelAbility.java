package net.ltxprogrammer.changed.ability.active.spider;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Collections;

public class CobwebRappelAbility extends AbstractAbility<CobwebRappelAbilityInstance> {
    public CobwebRappelAbility() {
        super(CobwebRappelAbilityInstance::new);
    }

    private static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.cobweb_rappel.desc"));

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }
}
