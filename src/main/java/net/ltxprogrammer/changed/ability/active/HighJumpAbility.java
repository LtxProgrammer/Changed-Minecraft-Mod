package net.ltxprogrammer.changed.ability.active;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Pose;

import java.util.Collection;
import java.util.Collections;

public class HighJumpAbility extends SimpleToggleAbility {
    public HighJumpAbility() {
        super(false);
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return true;
    }

    private static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.high_jump.desc"));
    private static final Component ENABLE = Component.translatable("ability.changed.high_jump.enable");
    private static final Component DISABLE = Component.translatable("ability.changed.high_jump.disable");

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
