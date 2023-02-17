package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.util.CameraUtil;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LatexHypnoCat extends AbstractLatexHypnoCat implements UniqueEffect, PatronOC {
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

            CameraUtil.tugEntityLookDirection(livingEntity, self,  0.4);
            livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 120, 2, false, false), self);
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 2, false, false), self);
        });
    }

    @Override
    public ChangedParticles.Color3 getHairColor(int layer) {
        return ChangedParticles.Color3.WHITE;
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collections.NONE;
    }
}
