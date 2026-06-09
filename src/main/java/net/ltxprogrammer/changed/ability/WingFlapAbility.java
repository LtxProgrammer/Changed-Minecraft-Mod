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
    public int getCoolDown(IAbstractChangedEntity entity) {
        return switch (this.getAbilityLevel(entity)) {
            case 0 -> 40;
            default -> 20;
        };
    }

    public static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.wing_flap.desc"));

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }
}
