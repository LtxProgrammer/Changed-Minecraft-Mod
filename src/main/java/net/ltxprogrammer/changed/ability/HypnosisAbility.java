package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.entity.variant.LatexVariantInstance;
import net.ltxprogrammer.changed.util.CameraUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;

public class HypnosisAbility extends SimpleAbility {
    @Override
    public void tick(IAbstractLatex entity) {
        var self = entity.getEntity();
        var level = entity.getLevel();

        super.tick(entity);
        level.getNearbyEntities(Mob.class, TargetingConditions.DEFAULT, self,
                AABB.ofSize(self.position(), 3.0, 3.0, 3.0)).forEach(mob -> {
            if (mob instanceof LatexEntity)
                return;

            if (mob.getTarget() != null && mob.getTarget().is(self)) {
                mob.setTarget(null);
            }
        });

        level.getNearbyEntities(LivingEntity.class, TargetingConditions.DEFAULT, self,
                AABB.ofSize(self.position(), 10.0, 10.0, 10.0)).forEach(livingEntity -> {
            if (LatexVariant.getEntityVariant(livingEntity) != null)
                return;
            if (livingEntity.getLookAngle().dot(self.getEyePosition().subtract(livingEntity.getEyePosition()).normalize()) < 0.85f)
                return;

            CameraUtil.tugEntityLookDirection(livingEntity, self,  0.4);
            livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120, 2, false, false), self);
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 2, false, false), self);
        });
    }
}
