package net.ltxprogrammer.changed.entity.projectile;

import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

public class GasParticle extends ThrowableProjectile {
    private static final EntityDataAccessor<Integer> COLOR = SynchedEntityData.defineId(GasParticle.class, EntityDataSerializers.INT);
    public static final int DISSIPATE_TIME = 15;
    public LatexVariant<?> variant = null;

    public GasParticle(EntityType<? extends GasParticle> type, Level level) {
        super(type, level);
    }

    public GasParticle setVariant(LatexVariant<?> variant) {
        this.variant = variant; return this;
    }

    public GasParticle setColor(ChangedParticles.Color3 color) {
        this.entityData.set(COLOR, color.toInt()); return this;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        TagUtil.putResourceLocation(tag, "LatexVariant", variant.getFormId());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("LatexVariant"))
            this.variant = ChangedRegistry.LATEX_VARIANT.get().getValue(TagUtil.getResourceLocation(tag, "LatexVariant"));
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity entity) {
        if (entity instanceof LivingEntity livingEntity)
            return LatexVariant.getEntityVariant(livingEntity) == null;
        else
            return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.tickCount > DISSIPATE_TIME)
            this.discard();
    }

    @Override
    protected float getGravity() {
        return 0.015f;
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        super.onHitEntity(result);

        if (variant != null && result.getEntity() instanceof LivingEntity livingEntity) {
            ProcessTransfur.progressTransfur(livingEntity, (int)Mth.lerp((float)this.tickCount / DISSIPATE_TIME, 3500.0f, 500.f), variant.getFormId());
            this.discard();
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(COLOR, ChangedParticles.Color3.WHITE.toInt());
    }

    public ChangedParticles.Color3 getColor() {
        return ChangedParticles.Color3.fromInt(this.getEntityData().get(COLOR));
    }
}
