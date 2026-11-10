package net.ltxprogrammer.changed.entity.robot;

import net.ltxprogrammer.changed.block.IRobotCharger;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

import javax.annotation.Nullable;

public abstract class AbstractRobot extends PathfinderMob {
    protected static final EntityDataAccessor<Integer> DATA_ID_HURT = SynchedEntityData.defineId(AbstractRobot.class, EntityDataSerializers.INT);
    protected static final EntityDataAccessor<Float> DATA_ID_HURTDIR = SynchedEntityData.defineId(AbstractRobot.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> DATA_ID_DAMAGE = SynchedEntityData.defineId(AbstractRobot.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> DATA_ID_CHARGE = SynchedEntityData.defineId(AbstractRobot.class, EntityDataSerializers.FLOAT); // [0, 1]
    protected static final EntityDataAccessor<Boolean> DATA_ID_CHARGING = SynchedEntityData.defineId(AbstractRobot.class, EntityDataSerializers.BOOLEAN);

    protected @Nullable BlockPos closestCharger;

    protected AbstractRobot(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0f);
    }

    public abstract boolean isAffectedByWater();
    public abstract float getMaxDamage();
    public abstract ChargerType getChargerType();

    @Nullable
    public SoundEvent getRunningSound() {
        return null;
    }

    public int getRunningSoundDuration() {
        return 20;
    }

    private int runningCooldown = 0;
    public void playRunningSound() {
        if (runningCooldown > 0) {
            runningCooldown--;
            return;
        }

        runningCooldown = getRunningSoundDuration();
        SoundEvent sound = getRunningSound();
        if (sound != null && level().isClientSide)
            level().playSound(UniversalDist.getLocalPlayer(), this, sound, SoundSource.NEUTRAL, 0.5f, 1f);
    }

    public void broadcastNearbyCharger(BlockPos where, ChargerType type) {
        broadcastNearbyCharger(where, type, true);
    }

    public void broadcastNearbyCharger(BlockPos where, ChargerType type, boolean useable) {
        if (!useable && closestCharger != null && closestCharger.equals(where)) {
            closestCharger = null;
            return;
        }

        if (type != this.getChargerType())
            return;
        if (closestCharger != null) {
            if (this.blockPosition().distManhattan(where) > this.blockPosition().distManhattan(closestCharger))
                return;
        }

        closestCharger = where;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_HURT, 0);
        this.entityData.define(DATA_ID_HURTDIR, 0.0F);
        this.entityData.define(DATA_ID_DAMAGE, 0.0F);
        this.entityData.define(DATA_ID_CHARGE, 1.0F);
        this.entityData.define(DATA_ID_CHARGING, false);
    }

    public void setDamage(float value) {
        this.entityData.set(DATA_ID_DAMAGE, value);
    }

    public float getDamage() {
        return this.entityData.get(DATA_ID_DAMAGE);
    }

    public void setHurtTime(int value) {
        this.entityData.set(DATA_ID_HURT, value);
    }

    public int getHurtTime() {
        return this.entityData.get(DATA_ID_HURT);
    }

    public void setHurtDir(float value) {
        this.entityData.set(DATA_ID_HURTDIR, value);
    }

    public float getHurtDir() {
        return this.entityData.get(DATA_ID_HURTDIR);
    }

    public void setCharge(float value) {
        this.entityData.set(DATA_ID_CHARGE, value);
    }

    public float getCharge() {
        return this.entityData.get(DATA_ID_CHARGE);
    }

    public boolean isLowBattery() {
        return getCharge() < 0.15f;
    }

    protected boolean wantsToSeekCharger() {
        return isLowBattery();
    }

    // Threshold where the robot will detach to pursue its tasks
    public boolean isSufficientlyCharged() {
        return getCharge() > 0.8f;
    }

    public boolean isCharging() {
        return this.entityData.get(DATA_ID_CHARGING);
    }

    public void setCharging(boolean value) {
        this.entityData.set(DATA_ID_CHARGING, value);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("DamageLevel", getDamage());
        tag.putFloat("ChargeLevel", getCharge());
        tag.putBoolean("IsCharging", isCharging());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("DamageLevel"))
            this.setDamage(tag.getFloat("DamageLevel"));
        if (tag.contains("ChargeLevel"))
            this.setCharge(tag.getFloat("ChargeLevel"));
        if (tag.contains("IsCharging"))
            this.setCharging(tag.getBoolean("IsCharging"));
    }

    protected float getChargeDecay() {
        return 1f / (5 * 60 * 20); // 5 minutes runtime
    }

    protected float getChargeRate() {
        return 1f / (2 * 60 * 20); // 2 minutes charge
    }

    protected boolean wantsToLeaveChargerWhenFull() {
        return !this.hasCustomName();
    }

    @Override
    public void tick() {
        super.tick();

        boolean damageByWater = this.isAffectedByWater() && this.isInWater();

        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }

        if (this.getDamage() > 0.0F && !damageByWater) {
            this.setDamage(this.getDamage() - 1.0F);
        } else if (damageByWater) {
            this.setDamage(this.getDamage() + 2.0F);

            if (this.getDamage() > this.getMaxDamage()) {
                if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                    this.spawnAtLocation(this.getDropItem());
                }

                this.discard();
            }
        }

        if (this.getCharge() > 0 && !this.isCharging()) {
            this.playRunningSound();
            this.setCharge(Mth.clamp(this.getCharge() - this.getChargeDecay(), 0f, 1f));
        }

        else if (this.getCharge() < 1f && this.isCharging()) {
            this.setCharge(Mth.clamp(this.getCharge() + this.getChargeRate(), 0f, 1f));

            if (this.getCharge() >= 1f && this.wantsToLeaveChargerWhenFull())
                this.detachFromCharger();
        }
    }

    public ItemStack getDropItem() {
        return ItemStack.EMPTY;
    }

    public void loadFromItemStack(ItemStack stack) {}

    public boolean hurt(DamageSource source, float damage) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.level().isClientSide && !this.isRemoved()) {
            this.setHurtDir(-this.getHurtDir());
            this.setHurtTime(10);
            this.setDamage(this.getDamage() + damage * 10.0F);
            if (source.getEntity() instanceof LivingEntity livingEntity)
                this.setLastHurtByMob(livingEntity);
            this.markHurt();
            this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
            boolean flag = source.getEntity() instanceof Player && ((Player)source.getEntity()).getAbilities().instabuild;
            if (flag || this.getDamage() > this.getMaxDamage()) {
                if (!flag && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                    this.spawnAtLocation(this.getDropItem());
                }

                this.discard();
            }

            return true;
        } else {
            return true;
        }
    }

    @Override
    public boolean isPersistenceRequired() {
        return true;
    }

    public float roundYRotToAxis(float yRot) {
        //yRot = Mth.positiveModulo(yRot, 360F);
        yRot /= 90F;
        yRot = Math.round(yRot);
        return yRot * 90F;
    }

    @Override
    public void stopSleeping() {
        this.detachFromCharger();
        super.stopSleeping();
    }

    protected void detachFromCharger() {
        if (this.isCharging() && this.getSleepingPos().isPresent()) {
            final var chargerPos = this.getSleepingPos().get();
            final var chargerState = level().getBlockState(chargerPos);
            if (chargerState.getBlock() instanceof IRobotCharger charger) {
                charger.acceptRobotRemoved(chargerState, level(), chargerPos, this);

                // Restore nearest charger
                this.broadcastNearbyCharger(chargerPos, charger.getChargerType(), true);
            }
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        switch (reason) {
            case KILLED, DISCARDED -> this.detachFromCharger();
        }

        super.remove(reason);
    }

    public static class SeekCharger extends Goal {
        public final AbstractRobot robot;
        private final double speedModifier;

        public SeekCharger(AbstractRobot robot, double speedModifier) {
            this.robot = robot;
            this.speedModifier = speedModifier;
        }

        @Override
        public boolean canUse() {
            return robot.wantsToSeekCharger() && robot.closestCharger != null;
        }

        @Override
        public void start() {
            super.start();
            if (robot.closestCharger == null)
                return;

            robot.getNavigation().moveTo(robot.getNavigation().createPath(robot.closestCharger, 0), this.speedModifier);
        }

        @Override
        public void tick() {
            super.tick();
            if (robot.getNavigation().isDone() && robot.closestCharger != null) {
                BlockState state = robot.level().getBlockState(robot.closestCharger);
                if (state.getBlock() instanceof IRobotCharger charger) {
                    charger.acceptRobot(state, robot.level(), robot.closestCharger, robot);
                }
                robot.closestCharger = null;
            }
        }

        @Override
        public void stop() {
            super.stop();
            robot.getNavigation().stop();
        }
    }
}
