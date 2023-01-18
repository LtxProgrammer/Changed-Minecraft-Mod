package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.Gender;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.UniqueEffect;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class LatexHypnoCat extends AbstractLatexHypnoCat implements UniqueEffect {
    public LatexHypnoCat(EntityType<? extends LatexHypnoCat> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Override
    public Gender getGender() {
        return Gender.MALE;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.REPLICATION;
    }

    @Override
    public String getEffectName() {
        return "hypnosis";
    }

    public static void tugEntityLookDirection(LivingEntity livingEntity, Vec3 direction, double strength) {
        float xRotO = livingEntity.xRotO;
        float yRotO = livingEntity.yRotO;
        direction = livingEntity.getLookAngle().lerp(direction, strength);
        livingEntity.lookAt(EntityAnchorArgument.Anchor.EYES, livingEntity.getEyePosition().add(direction));
        livingEntity.xRotO = xRotO;
        livingEntity.yRotO = yRotO;
    }

    @Override
    public void effectTick(Level level, LivingEntity self) {
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

            tugEntityLookDirection(livingEntity, self.getEyePosition().subtract(livingEntity.getEyePosition()).normalize(),  0.4);
            livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120, 2, false, false), self);
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 2, false, false), self);
        });
    }
}
