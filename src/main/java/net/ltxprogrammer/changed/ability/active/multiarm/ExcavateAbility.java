package net.ltxprogrammer.changed.ability.active.multiarm;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Collections;

public class ExcavateAbility extends AbstractAbility<ExcavateAbilityInstance> {
    public ExcavateAbility() {
        super(ExcavateAbilityInstance::new);
    }

    public static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.excavate.desc"));
    public static final Component ENABLE = Component.translatable("ability.changed.excavate.enable");
    public static final Component DISABLE = Component.translatable("ability.changed.excavate.disable");

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }
}
