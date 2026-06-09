package net.ltxprogrammer.changed.entity.projectile;

import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class WindGust extends ThrowableProjectile {
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
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.5d, 0.8d, 0.8d));
            livingEntity.knockback(0.8d, -pushAngle.x, -pushAngle.z);
        }
    }
}
