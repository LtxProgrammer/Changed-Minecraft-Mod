package net.ltxprogrammer.changed.ability.active.multiarm;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.ability.active.SimpleToggleAbility;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.Collections;

public class ExcavateAbility extends SimpleToggleAbility {
    public ExcavateAbility() {
        super(false);
    }

    private static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.excavate.desc"));
    private static final Component ENABLE = Component.translatable("ability.changed.excavate.enable");
    private static final Component DISABLE = Component.translatable("ability.changed.excavate.disable");

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }

    @Override
    public void startUsing(IAbstractChangedEntity entity) {
        super.startUsing(entity);
        entity.displayClientMessage(isActive(entity) ? ENABLE : DISABLE, true);
    }
}
