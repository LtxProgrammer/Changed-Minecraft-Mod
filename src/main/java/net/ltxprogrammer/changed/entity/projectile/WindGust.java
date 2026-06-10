package net.ltxprogrammer.changed.entity.projectile;

import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class WindGust extends ThrowableProjectile {
    private static final EntityDataAccessor<Integer> STRENGTH = SynchedEntityData.defineId(WindGust.class, EntityDataSerializers.INT);
    public static final int DISSIPATE_TIME = 15;

    public WindGust(EntityType<? extends WindGust> type, Level level) {
        super(type, level);
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity entity) {
        if (entity instanceof LivingEntity livingEntity)
            return TransfurVariant.getEntityVariant(livingEntity) == null;
        else
            return false;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(STRENGTH, Color3.WHITE.toInt());
    }

    public int getStrength() {
        return this.getEntityData().get(STRENGTH);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > DISSIPATE_TIME)
            this.discard();
    }

    @Override
    protected float getGravity() {
        return 0.0f;
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);

        if (result.getEntity() instanceof LivingEntity livingEntity && livingEntity.isPushable()) {
            var pushAngle = this.getDeltaMovement().normalize();
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.8d, 0.8d, 0.8d));

            int strength = this.getStrength();
            if (strength >= 0) {
                livingEntity.knockback(0.8d, -pushAngle.x, -pushAngle.z);
            } if (strength >= 1) {
                livingEntity.knockback(0.4d, -pushAngle.x, -pushAngle.z);

                Entity owner = this.getOwner();
                DamageSource damagesource;
                if (owner == null) {
                    damagesource = ChangedDamageSources.GALE_WIND_BURST.source(level().registryAccess(), this);
                } else {
                    damagesource = ChangedDamageSources.GALE_WIND_BURST.source(level().registryAccess(), owner);
                    if (owner instanceof LivingEntity livingOwner) {
                        livingOwner.setLastHurtMob(livingEntity);
                    }
                }
                livingEntity.hurt(damagesource, 3.0f);
            }
        }
    }

    public void setStrength(int level) {
        this.entityData.set(STRENGTH, level);
    }
}
