package net.ltxprogrammer.changed.entity.ai;

import net.ltxprogrammer.changed.entity.beast.IFlyingCapable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.*;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RandomStrollOrHoverGoal<T extends PathfinderMob & IFlyingCapable> extends RandomStrollGoal {
    protected final T mob;
    protected final IntProvider walkingTicks;
    protected final IntProvider flyingTicks;
    protected boolean wantedPosInAir = false;
    protected int minimumTickCount = 0;

    public RandomStrollOrHoverGoal(T mob, double speedModifier, IntProvider walkingTicks, IntProvider flyingTicks) {
        super(mob, speedModifier);
        this.mob = mob;
        this.walkingTicks = walkingTicks;
        this.flyingTicks = flyingTicks;
    }

    public RandomStrollOrHoverGoal(T mob, double speedModifier, int interval, IntProvider walkingTicks, IntProvider flyingTicks) {
        super(mob, speedModifier, interval);
        this.mob = mob;
        this.walkingTicks = walkingTicks;
        this.flyingTicks = flyingTicks;
    }

    public RandomStrollOrHoverGoal(T mob, double speedModifier, int interval, boolean checkNoActionTime, IntProvider walkingTicks, IntProvider flyingTicks) {
        super(mob, speedModifier, interval, checkNoActionTime);
        this.mob = mob;
        this.walkingTicks = walkingTicks;
        this.flyingTicks = flyingTicks;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void start() {
        if (wantedPosInAir && !this.mob.isFlying()) {
            this.mob.setFlying(true);
            minimumTickCount = this.mob.tickCount + flyingTicks.sample(this.mob.getRandom());
        }
        super.start();
    }

    @Override
    public void tick() {
        super.tick();
        if (!wantedPosInAir && this.mob.isFlying() && this.mob.canSafelyLandHere()) {
            this.mob.setFlying(false);
            minimumTickCount = this.mob.tickCount + walkingTicks.sample(this.mob.getRandom());
            start(); // Navigation stopped from flying change, restart navigation
        }
    }

    protected @Nullable Vec3 getHoverPosition() {
        Vec3 lookAngle = mob.getViewVector(0.0F);
        int maxHorizontalDistance = 8;
        Vec3 hoverPos = HoverRandomPos.getPos(mob, maxHorizontalDistance, 7, lookAngle.x, lookAngle.z, ((float)Math.PI / 2F), 3, 1);
        if (hoverPos != null)
            return hoverPos;
        else
            return AirAndWaterRandomPos.getPos(mob, maxHorizontalDistance, 4, -2, lookAngle.x, lookAngle.z, (float)Math.PI / 2F);
    }

    @Override
    protected @Nullable Vec3 getPosition() {
        boolean currentlyFlying = mob.isFlying();

        boolean beInAir = (mob.tickCount >= minimumTickCount) != currentlyFlying;

        if (beInAir) {
            this.wantedPosInAir = true;
            return this.getHoverPosition();
        } else {
            if (currentlyFlying) { // Try to land
                Vec3 landPos = getPosBiased(this.mob, this.mob.getNavigationForFlyingState(false), 10, 7, 0, -5, 0);
                if (landPos != null) {
                    this.wantedPosInAir = false;
                    return landPos;
                }

                this.wantedPosInAir = true;
                return this.getHoverPosition();
            } else {
                this.wantedPosInAir = false;
                return super.getPosition();
            }
        }
    }

    public static @Nullable Vec3 getPosBiased(PathfinderMob mob, PathNavigation navigation, int maxHorizontalDistance, int maxVerticalDistance, int biasX, int biasY, int biasZ) {
        boolean flag = GoalUtils.mobRestricted(mob, maxHorizontalDistance);
        return RandomPos.generateRandomPos(mob, () -> {
            BlockPos blockpos = RandomPos.generateRandomDirection(mob.getRandom(), maxHorizontalDistance, maxVerticalDistance)
                    .offset(biasX, biasY, biasZ);
            return generateRandomPosTowardDirection(mob, navigation, maxHorizontalDistance, flag, blockpos);
        });
    }

    @Nullable
    private static BlockPos generateRandomPosTowardDirection(PathfinderMob mob, PathNavigation navigation, int maxHorizontalDistance, boolean mobRestricted, BlockPos direction) {
        BlockPos blockpos = RandomPos.generateRandomPosTowardDirection(mob, maxHorizontalDistance, mob.getRandom(), direction);
        return !GoalUtils.isOutsideLimits(blockpos, mob)
                && !GoalUtils.isRestricted(mobRestricted, mob, blockpos)
                && !GoalUtils.isNotStable(navigation, blockpos)
                && !GoalUtils.hasMalus(mob, blockpos)
                ? blockpos : null;
    }
}
