package net.ltxprogrammer.changed.ability;

import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.gameevent.GameEvent;
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
        if (ability.getAbilityLevel(entity) > 0 && !inWater && !entity.getEntity().onGround() && !entity.getEntity().onClimbable())
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
            this.playBoostSound();
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

    protected void playBoostSound() {
        var random = entity.getEntity().getRandom();
        entity.getEntity().playSound(ChangedSounds.UNDERWATER_BOOST.get(), 1.0f, 1.0F + (random.nextFloat() - random.nextFloat()) * 0.4F);
    }

    protected void spawnBoostParticles(float scale) {
        if (!entity.getLevel().isClientSide())
            return;

        int maxCount = (int)(4 * scale);
        if (maxCount <= 0)
            return;

        var random = entity.getEntity().getRandom();
        int particleCount = random.nextInt(0, maxCount);
        var self = entity.getEntity();
        for (int i = 0; i < particleCount; ++i) {
            entity.getLevel().addParticle(
                    ParticleTypes.BUBBLE,
                    self.position().x + random.nextFloat() - 0.5f,
                    self.position().y + random.nextFloat() - 0.5f,
                    self.position().z + random.nextFloat() - 0.5f,
                    self.getDeltaMovement().x * 0.5,
                    self.getDeltaMovement().y * 0.5,
                    self.getDeltaMovement().z * 0.5
            );
        }
    }

    protected void spawnBreachParticles() {
        if (!entity.getLevel().isClientSide())
            return;

        var self = this.entity.getEntity();
        var level = this.entity.getLevel();
        var random = self.getRandom();
        Vec3 vec3 = self.getDeltaMovement();

        float f2 = (float)Mth.floor(self.getY());

        for(int i = 0; (float)i < 1.0F + self.getBbWidth() * 20.0F; ++i) {
            double d0 = (random.nextDouble() * 2.0D - 1.0D) * (double)self.getBbWidth();
            double d1 = (random.nextDouble() * 2.0D - 1.0D) * (double)self.getBbWidth();
            level.addParticle(ParticleTypes.BUBBLE, self.getX() + d0, (double)(f2 + 1.0F), self.getZ() + d1, vec3.x, vec3.y - random.nextDouble() * (double)0.2F, vec3.z);
        }

        for(int j = 0; (float)j < 1.0F + self.getBbWidth() * 20.0F; ++j) {
            double d2 = (random.nextDouble() * 2.0D - 1.0D) * (double)self.getBbWidth();
            double d3 = (random.nextDouble() * 2.0D - 1.0D) * (double)self.getBbWidth();
            level.addParticle(ParticleTypes.SPLASH, self.getX() + d2, (double)(f2 + 1.0F), self.getZ() + d3, vec3.x, vec3.y, vec3.z);
        }

        self.gameEvent(GameEvent.SPLASH);
    }

    @Override
    public void tickIdle() {
        var self = entity.getEntity();
        var swimSpeed = self.getAttribute(ForgeMod.SWIM_SPEED.get());
        int level = ability.getAbilityLevel(entity);

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
                this.spawnBreachParticles();
            } else {
                this.spawnBoostParticles(1.0f);
            }
        }

        else if (this.breached) {
            if (level <= 0) {
                this.breached = false;
                this.reentryTicks = 0;
                return;
            }

            self.setPose(Pose.SWIMMING);
            self.setSprinting(true);

            this.breached = !self.isEyeInFluidType(ForgeMod.WATER_TYPE.get());
            double dx = self.position().x - self.xo;
            double dy = self.position().y - self.yo;
            double dz = self.position().z - self.zo;
            this.velocityOnReentry = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (self.onGround() || self.onClimbable()) {
                this.breached = false;
                this.reentryTicks = 0;
            }

            if (!this.breached) {
                this.reentryTicks = 20;
                this.boostAngle = self.getDeltaMovement().normalize();
                this.playBoostSound();
            }
        }

        else if (this.reentryTicks > 0) {
            if (level <= 0 || !self.isEyeInFluidType(ForgeMod.WATER_TYPE.get())) {
                this.reentryTicks = 0;
                return;
            }

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
            this.spawnBoostParticles((float)decay);
        }
    }

    @Override
    public void stopUsing() {

    }
}
