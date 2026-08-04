package net.ltxprogrammer.changed.ability.active;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.network.chat.*;

import java.util.Collection;
import java.util.Collections;

public class FriendlyTransfurAbility extends AbstractAbility<FriendlyTransfurAbilityInstance> {
    public static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.friendly_transfur.desc"));

    public FriendlyTransfurAbility() {
        super(FriendlyTransfurAbilityInstance::new);
    }

    @Override
    public int getChargeTime(IAbstractChangedEntity entity) {
        return 20 * 5;
    }

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }
}
