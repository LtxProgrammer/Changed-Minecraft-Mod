package net.ltxprogrammer.changed.entity.ai;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class LookAtPlayerButNotHostGoal extends Goal {
    public static final float DEFAULT_PROBABILITY = 0.02F;
    protected final ChangedEntity mob;
    @Nullable
    protected Entity lookAt;
    protected final float lookDistance;
    private int lookTime;
    protected final float probability;
    private final boolean onlyHorizontal;
    protected final Class<? extends LivingEntity> lookAtType;
    protected final TargetingConditions lookAtContext;

    public LookAtPlayerButNotHostGoal(ChangedEntity mob, Class<? extends LivingEntity> clazz, float lookDistance) {
        this(mob, clazz, lookDistance, 0.02F);
    }

    public LookAtPlayerButNotHostGoal(ChangedEntity mob, Class<? extends LivingEntity> clazz, float lookDistance, float probability) {
        this(mob, clazz, lookDistance, probability, false);
    }

    public LookAtPlayerButNotHostGoal(ChangedEntity mob, Class<? extends LivingEntity> clazz, float lookDistance, float probability, boolean onlyHorizontal) {
        this.mob = mob;
        this.lookAtType = clazz;
        this.lookDistance = lookDistance;
        this.probability = probability;
        this.onlyHorizontal = onlyHorizontal;
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
        if (clazz == Player.class) {
            this.lookAtContext = TargetingConditions.forNonCombat().range(lookDistance).selector((target) -> {
                return EntitySelector.notRiding(mob).and(entity -> entity != mob.getUnderlyingPlayer()).test(target);
            });
        } else {
            this.lookAtContext = TargetingConditions.forNonCombat().range(lookDistance);
        }

    }

    public boolean canUse() {
        if (this.mob.getRandom().nextFloat() >= this.probability) {
            return false;
        } else {
            if (this.mob.getTarget() != null) {
                this.lookAt = this.mob.getTarget();
            }

            if (this.lookAtType == Player.class) {
                this.lookAt = this.mob.level.getNearestPlayer(this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
            } else {
                this.lookAt = this.mob.level.getNearestEntity(this.mob.level.getEntitiesOfClass(this.lookAtType, this.mob.getBoundingBox().inflate((double)this.lookDistance, 3.0D, (double)this.lookDistance), (p_148124_) -> {
                    return true;
                }), this.lookAtContext, this.mob, this.mob.getX(), this.mob.getEyeY(), this.mob.getZ());
            }

            return this.lookAt != null;
        }
    }

    public boolean canContinueToUse() {
        if (!this.lookAt.isAlive()) {
            return false;
        } else if (this.mob.distanceToSqr(this.lookAt) > (double)(this.lookDistance * this.lookDistance)) {
            return false;
        } else {
            return this.lookTime > 0;
        }
    }

    public void start() {
        this.lookTime = this.adjustedTickDelay(40 + this.mob.getRandom().nextInt(40));
    }

    public void stop() {
        this.lookAt = null;
    }

    public void tick() {
        if (this.lookAt.isAlive()) {
            double d0 = this.onlyHorizontal ? this.mob.getEyeY() : this.lookAt.getEyeY();
            this.mob.getLookControl().setLookAt(this.lookAt.getX(), d0, this.lookAt.getZ());
            --this.lookTime;
        }
    }
}
