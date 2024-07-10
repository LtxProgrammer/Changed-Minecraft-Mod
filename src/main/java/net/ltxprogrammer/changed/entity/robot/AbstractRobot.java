package net.ltxprogrammer.changed.entity.robot;

import net.ltxprogrammer.changed.block.IRobotCharger;
import net.ltxprogrammer.changed.init.ChangedItems;
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
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;

import javax.annotation.Nullable;

public abstract class AbstractRobot extends PathfinderMob {
    private static final EntityDataAccessor<Integer> DATA_ID_HURT = SynchedEntityData.defineId(AbstractRobot.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ID_HURTDIR = SynchedEntityData.defineId(AbstractRobot.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DATA_ID_DAMAGE = SynchedEntityData.defineId(AbstractRobot.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_ID_CHARGE = SynchedEntityData.defineId(AbstractRobot.class, EntityDataSerializers.FLOAT); // [0, 1]

    protected @Nullable BlockPos closestCharger;

    protected AbstractRobot(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
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
        if (sound != null && level.isClientSide)
            level.playSound(UniversalDist.getLocalPlayer(), this, sound, SoundSource.NEUTRAL, 0.5f, 1f);
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
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new SeekCharger(this));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_HURT, 0);
        this.entityData.define(DATA_ID_HURTDIR, 1);
        this.entityData.define(DATA_ID_DAMAGE, 0.0F);
        this.entityData.define(DATA_ID_CHARGE, 1.0F);
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

    public void setHurtDir(int value) {
        this.entityData.set(DATA_ID_HURTDIR, value);
    }

    public int getHurtDir() {
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

    @Override
    public void tick() {
        super.tick();

        boolean damageByWater = this.isAffectedByWater() && this.isInWater();

        this.setSpeed((float) this.getAttributeValue(Attributes.MOVEMENT_SPEED));

        if (this.getHurtTime() > 0) {
            this.setHurtTime(this.getHurtTime() - 1);
        }

        if (this.getDamage() > 0.0F && !damageByWater) {
            this.setDamage(this.getDamage() - 1.0F);
        } else if (damageByWater) {
            this.setDamage(this.getDamage() + 2.0F);

            if (this.getDamage() > this.getMaxDamage()) {
                if (this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                    this.spawnAtLocation(this.getDropItem());
                }

                this.discard();
            }
        }

        if (this.getCharge() > 0) {
            this.playRunningSound();
            this.setCharge(this.getCharge() - (0.01f / 60f));
        }
    }

    public ItemLike getDropItem() {
        return ChangedItems.ROOMBA.get();
    }

    public boolean hurt(DamageSource source, float damage) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.level.isClientSide && !this.isRemoved()) {
            this.setHurtDir(-this.getHurtDir());
            this.setHurtTime(10);
            this.setDamage(this.getDamage() + damage * 10.0F);
            if (source.getEntity() instanceof LivingEntity livingEntity)
                this.setLastHurtByMob(livingEntity);
            this.markHurt();
            this.gameEvent(GameEvent.ENTITY_DAMAGED, source.getEntity());
            boolean flag = source.getEntity() instanceof Player && ((Player)source.getEntity()).getAbilities().instabuild;
            if (flag || this.getDamage() > this.getMaxDamage()) {
                if (!flag && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
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
    public boolean save(CompoundTag tag) {
        tag.putFloat("charge", getCharge());
        return super.save(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        setCharge(tag.getFloat("charge"));
        super.load(tag);
    }

    public static class SeekCharger extends Goal {
        public final AbstractRobot robot;

        public SeekCharger(AbstractRobot robot) {
            this.robot = robot;
        }

        @Override
        public boolean canUse() {
            return robot.isLowBattery() && robot.closestCharger != null;
        }

        @Override
        public void start() {
            super.start();
            if (robot.closestCharger == null)
                return;

            robot.getNavigation().moveTo(robot.getNavigation().createPath(robot.closestCharger, 0), 0.75);
        }

        @Override
        public void tick() {
            super.tick();
            if (robot.getNavigation().isDone()) {
                BlockState state = robot.level.getBlockState(robot.closestCharger);
                if (state.getBlock() instanceof IRobotCharger charger) {
                    charger.acceptRobot(state, robot.level, robot.closestCharger, robot);
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
