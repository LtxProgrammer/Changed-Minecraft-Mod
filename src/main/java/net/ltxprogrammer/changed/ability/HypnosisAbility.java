package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.util.CameraUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.Collection;
import java.util.Collections;

public class HypnosisAbility extends SimpleAbility {
    @Override
    public void tick(IAbstractChangedEntity entity) {
        var self = entity.getEntity();
        var level = entity.getLevel();

        super.tick(entity);
        level.getNearbyEntities(Mob.class, TargetingConditions.DEFAULT, self,
                AABB.ofSize(self.position(), 3.0, 3.0, 3.0)).forEach(mob -> {
            if (mob instanceof ChangedEntity)
                return;

            if (mob.getTarget() != null && mob.getTarget().is(self)) {
                mob.setTarget(null);
            }
        });

        level.getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, self,
                AABB.ofSize(self.position(), 10.0, 10.0, 10.0)).forEach(livingEntity -> {
            if (TransfurVariant.getEntityVariant(livingEntity) != null)
                return;
            if (livingEntity.getLookAngle().dot(self.getEyePosition().subtract(livingEntity.getEyePosition()).normalize()) < 0.85f)
                return;
            if (livingEntity instanceof Player && !Changed.config.server.playerControllingAbilities.get())
                return;

            CameraUtil.tugEntityLookDirection(livingEntity, self,  0.125);
            livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120, 2, false, false), self);
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 2, false, false), self);
        });
    }

    @Override
    public UseType getUseType(IAbstractChangedEntity entity) {
        return UseType.HOLD;
    }

    @Override
    public boolean canUse(IAbstractChangedEntity entity) {
        return true;
    }

    @Override
    public boolean canKeepUsing(IAbstractChangedEntity entity) {
        return true;
    }

    private static final Collection<Component> DESCRIPTION = Collections.singleton(new TranslatableComponent("ability.changed.hypnosis.desc"));

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }
}
