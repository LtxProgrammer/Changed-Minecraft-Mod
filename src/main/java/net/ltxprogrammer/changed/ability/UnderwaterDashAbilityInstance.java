package net.ltxprogrammer.changed.ability;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

public class UnderwaterDashAbilityInstance extends AbstractAbilityInstance {
    private int boostTicks = 0;
    private Vec3 boostAngle = Vec3.ZERO;
    private boolean breached = false;
    private double velocityOnReentry = 0.0d;
    private int reentryTicks = 0;

    public UnderwaterDashAbilityInstance(AbstractAbility<?> ability, IAbstractChangedEntity entity) {
        super(ability, entity);
    }

    @Override
    public boolean canUse() {
        boolean inWater = entity.getEntity().isEyeInFluidType(ForgeMod.WATER_TYPE.get());
        if (inWater && entity.getEntity().hasPose(Pose.SWIMMING) && entity.getEntity().isSprinting())
            return true;
        if (!inWater && !entity.getEntity().onGround() && !entity.getEntity().onClimbable())
            return true;
        return false;
    }

    @Override
    public boolean canKeepUsing() {
        return true;
    }

    @Override
    public void startUsing() {
        boolean inWater = entity.getEntity().isEyeInFluidType(ForgeMod.WATER_TYPE.get());

        if (inWater) {
            this.boostTicks = 15;
            this.boostAngle = entity.getEntity().getLookAngle();
            this.breached = false;
        } else { // Start breach, dive
            this.boostTicks = 0;
            this.breached = true;
        }

        this.velocityOnReentry = 0.0d;
        this.reentryTicks = 0;
    }

    @Override
    public void tick() {

    }

    @Override
    public void tickIdle() {
        var self = entity.getEntity();
        var swimSpeed = self.getAttribute(ForgeMod.SWIM_SPEED.get());

        double baselineSpeed = self.getSpeed() * (swimSpeed != null ? swimSpeed.getValue() : 1.0) * 4.0;

        if (this.boostTicks > 0) {
            self.setPose(Pose.SWIMMING);
            self.setSprinting(true);
            this.boostTicks--;

            this.breached = !self.isEyeInFluidType(ForgeMod.WATER_TYPE.get());
            var fullControlAngle = entity.getEntity().getLookAngle();
            this.boostAngle = new Vec3(
                    Mth.lerp(0.05, boostAngle.x, fullControlAngle.x),
                    Mth.lerp(0.05, boostAngle.y, fullControlAngle.y),
                    Mth.lerp(0.05, boostAngle.z, fullControlAngle.z)
            );

            self.setDeltaMovement(this.boostAngle.multiply(baselineSpeed, baselineSpeed, baselineSpeed));

            if (this.breached) {
                this.boostTicks = 0;
                self.setDeltaMovement(this.boostAngle.multiply(baselineSpeed * 1.5, baselineSpeed * 1.5, baselineSpeed * 1.5));
            }
        }

        else if (this.breached) {
            self.setPose(Pose.SWIMMING);
            self.setSprinting(true);

            this.breached = !self.isEyeInFluidType(ForgeMod.WATER_TYPE.get());
            double dx = self.position().x - self.xo;
            double dy = self.position().y - self.yo;
            double dz = self.position().z - self.zo;
            this.velocityOnReentry = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (!this.breached) {
                this.reentryTicks = 20;
                this.boostAngle = self.getDeltaMovement().normalize();
            }

            if (self.onGround() || self.onClimbable()) {
                this.breached = false;
                this.reentryTicks = 0;
            }
        }

        else if (this.reentryTicks > 0) {
            self.setPose(Pose.SWIMMING);
            self.setSprinting(true);
            this.reentryTicks--;

            double decay = this.reentryTicks / 20.0d;
            double speed = baselineSpeed * (0.8d + velocityOnReentry / 2.0d) * decay;
            var fullControlAngle = entity.getEntity().getLookAngle();
            this.boostAngle = new Vec3(
                    Mth.lerp(0.25, boostAngle.x, fullControlAngle.x),
                    Mth.lerp(0.25, boostAngle.y, fullControlAngle.y),
                    Mth.lerp(0.25, boostAngle.z, fullControlAngle.z)
            );
            self.move(MoverType.SELF, this.boostAngle.multiply(speed, speed, speed));
        }
    }

    @Override
    public void stopUsing() {

    }
}
