package net.ltxprogrammer.changed.entity.ai;

import net.ltxprogrammer.changed.entity.beast.IFlyingCapable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class FlyingApproachMeleeAttackGoal<T extends PathfinderMob & IFlyingCapable> extends MeleeAttackGoal {
    protected final T mob;
    protected final double speedModifier;
    protected final double startFlyingDistanceThresholdSqr;
    protected final double stopFlyingDistanceThreshold;
    private boolean scheduleRecomputeWhenGrounded = false;

    public FlyingApproachMeleeAttackGoal(T mob, double speedModifier, boolean followWhenNotSeen, double startFlyingDistanceThreshold, double stopFlyingDistanceThreshold) {
        super(mob, speedModifier, followWhenNotSeen);
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.startFlyingDistanceThresholdSqr = startFlyingDistanceThreshold * startFlyingDistanceThreshold;
        this.stopFlyingDistanceThreshold = stopFlyingDistanceThreshold * stopFlyingDistanceThreshold;
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity != null && livingentity.isAlive() && scheduleRecomputeWhenGrounded)
            return true;
        return super.canContinueToUse();
    }

    @Override
    public void tick() {
        boolean shouldBeFlying = wantsToFly();

        if (!shouldBeFlying && scheduleRecomputeWhenGrounded && mob.onGround()) {
            this.scheduleRecomputeWhenGrounded = false;
            this.forcePathUpdate();
        }

        if (shouldBeFlying != mob.isFlying()) {
            mob.setFlying(shouldBeFlying);
            if (!this.forcePathUpdate() && !mob.isFlying())
                scheduleRecomputeWhenGrounded = true;
        }

        super.tick();
    }

    protected boolean forcePathUpdate() {
        LivingEntity target = this.mob.getTarget();
        if (target != null) {
            var newPath = this.mob.getNavigation().createPath(target, 0);
            boolean success = this.mob.getNavigation().moveTo(newPath, this.speedModifier);
            if (success)
                this.path = newPath;
            return success;
        }

        return false;
    }

    protected boolean wantsToFly() {
        boolean canSafelyLand = mob.canSafelyLandHere();
        boolean currentlyFlying = mob.isFlying();

        if (mob.getTarget() != null) {
            var target = mob.getTarget();
            double distSqr = target.distanceToSqr(mob);
            if (distSqr > startFlyingDistanceThresholdSqr)
                return true; // Start flying if target is far away enough
            if (distSqr > stopFlyingDistanceThreshold && currentlyFlying)
                return true; // Keep flying if target is far away enough
            if (target.getY() - mob.getY() > 2)
                return true; // Start flying if target is above
            return !canSafelyLand;
        }

        return currentlyFlying;
    }
}
