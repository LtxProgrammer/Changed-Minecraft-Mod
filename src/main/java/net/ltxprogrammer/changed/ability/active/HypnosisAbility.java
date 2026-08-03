package net.ltxprogrammer.changed.ability.active;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedVariantFeatures;
import net.ltxprogrammer.changed.util.CameraUtil;
import net.minecraft.network.chat.Component;
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
    protected int getSlownessLevel(double potency) {
        if (potency < 1.0)
            return 1;
        if (potency < 2.0)
            return 2;
        return 4;
    }

    protected int getFatigueLevel(double potency) {
        if (potency < 1.0)
            return 1;
        if (potency < 2.0)
            return 2;
        return 3;
    }

    protected int getWeaknessLevel(double potency) {
        if (potency < 1.0)
            return 0;
        if (potency < 2.0)
            return 0;
        return 1;
    }

    @Override
    public void tick(IAbstractChangedEntity entity) {
        var self = entity.getEntity();
        var level = entity.getLevel();

        super.tick(entity);

        double allure = entity.getFeatureLevel(ChangedVariantFeatures.HYPNOSIS_ALLURE.get()) * 0.05 + 0.05;
        double potency = entity.getFeatureLevel(ChangedVariantFeatures.HYPNOSIS_POTENCY.get());
        boolean remoteControl = entity.hasFeature(ChangedVariantFeatures.HYPNOSIS_CONTROL.get());

        int slowness = getSlownessLevel(potency);
        int fatigue = getFatigueLevel(potency);
        int weakness = getWeaknessLevel(potency);

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

            CameraUtil.tugEntityLookDirection(livingEntity, self, allure);
            livingEntity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 70, 0, false, false), self);
            if (slowness > 0)
                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, slowness - 1, false, false), self);
            if (fatigue > 0)
                livingEntity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 5, fatigue - 1, false, false), self);
            if (weakness > 0)
                livingEntity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 5, weakness - 1, false, false), self);
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

    private static final Collection<Component> DESCRIPTION = Collections.singleton(Component.translatable("ability.changed.hypnosis.desc"));

    @Override
    public Collection<Component> getAbilityDescription(IAbstractChangedEntity entity) {
        return DESCRIPTION;
    }
}
