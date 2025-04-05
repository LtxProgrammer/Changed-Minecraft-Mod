package net.ltxprogrammer.changed.mixin.render;

import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.ability.GrabEntityAbility;
import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.block.SeatableBlock;
import net.ltxprogrammer.changed.client.CameraExtender;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModelInterface;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.entity.SeatEntity;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.CameraUtil;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin implements CameraExtender {
    @Shadow protected abstract void setPosition(Vec3 p_90582_);

    @Shadow public abstract Vec3 getPosition();

    @Shadow private Entity entity;

    @Shadow @Final private Vector3f forwards;

    @Shadow @Final private Vector3f left;

    @Shadow @Final private Vector3f up;

    @Shadow public abstract void setup(BlockGetter p_90576_, Entity p_90577_, boolean p_90578_, boolean p_90579_, float p_90580_);

    @Shadow protected abstract void move(double p_90569_, double p_90570_, double p_90571_);

    @Unique
    private <T extends ChangedEntity> void adjustAnimForEntity(T changedEntity, float partialTicks) {
        if (Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(changedEntity) instanceof AdvancedHumanoidRenderer<?,?,?> latexHumanoid &&
                latexHumanoid.getModel(changedEntity) instanceof AdvancedHumanoidModelInterface AdvancedHumanoidModel) {
            boolean shouldSit = changedEntity.isPassenger() && (changedEntity.getVehicle() != null && changedEntity.getVehicle().shouldRiderSit());
            float f = Mth.rotLerp(partialTicks, changedEntity.yBodyRotO, changedEntity.yBodyRot);
            float f1 = Mth.rotLerp(partialTicks, changedEntity.yHeadRotO, changedEntity.yHeadRot);
            float netHeadYaw = f1 - f;
            if (shouldSit && changedEntity.getVehicle() instanceof LivingEntity livingentity) {
                f = Mth.rotLerp(partialTicks, livingentity.yBodyRotO, livingentity.yBodyRot);
                netHeadYaw = f1 - f;
                float f3 = Mth.wrapDegrees(netHeadYaw);
                if (f3 < -85.0F) {
                    f3 = -85.0F;
                }

                if (f3 >= 85.0F) {
                    f3 = 85.0F;
                }

                f = f1 - f3;
                if (f3 * f3 > 2500.0F) {
                    f += f3 * 0.2F;
                }

                netHeadYaw = f1 - f;
            }

            float headPitch = Mth.lerp(partialTicks, changedEntity.xRotO, changedEntity.getXRot());
            if (LivingEntityRenderer.isEntityUpsideDown(changedEntity)) {
                headPitch *= -1.0F;
                netHeadYaw *= -1.0F;
            }

            float limbSwingAmount = 0.0F;
            float limbSwing = 0.0F;
            if (!shouldSit && changedEntity.isAlive()) {
                limbSwingAmount = Mth.lerp(partialTicks, changedEntity.animationSpeedOld, changedEntity.animationSpeed);
                limbSwing = changedEntity.animationPosition - changedEntity.animationSpeed * (1.0F - partialTicks);
                if (changedEntity.isBaby()) {
                    limbSwing *= 3.0F;
                }

                if (limbSwingAmount > 1.0F) {
                    limbSwingAmount = 1.0F;
                }
            }

            var animator = AdvancedHumanoidModel.getAnimator(changedEntity);
            animator.setupVariables(changedEntity, partialTicks);
            animator.setupCameraAnim(this, changedEntity,
                    limbSwing,
                    limbSwingAmount,
                    changedEntity.tickCount + partialTicks,
                    netHeadYaw,
                    headPitch
            );
        }
    }

    @Inject(method = "setup", at = @At("RETURN"))
    public void animateCamera(BlockGetter level, Entity entity, boolean p_90578_, boolean p_90579_, float partialTicks, CallbackInfo ci) {
        if (entity.isSpectator()) return;
        if (entity instanceof ChangedEntity ChangedEntity)
            adjustAnimForEntity(ChangedEntity, partialTicks);

        else if (entity instanceof Player player) {
            ProcessTransfur.ifPlayerTransfurred(player, variant -> {
                adjustAnimForEntity(variant.getChangedEntity(), partialTicks);
            });
        }

        if (entity.getVehicle() instanceof SeatEntity seatEntity) {
            seatEntity.getAttachedBlockState().ifPresent(state -> {
                if (!(state.getBlock() instanceof SeatableBlock seatable)) return;
                var offset = seatable.getSleepOffset(level, state, seatEntity.getAttachedBlockPos());

                this.move(offset.x, offset.y, offset.z);
            });
        }
    }

    @Inject(method = "setup", at = @At("HEAD"), cancellable = true)
    public void setupWithTug(BlockGetter level, Entity entity, boolean firstPerson, boolean mirrored, float partialTicks, CallbackInfo ci) {
        if (!(entity instanceof LivingEntity livingEntity))
            return;

        LivingEntity controlling = GrabEntityAbility.getControllingEntity(livingEntity);
        if (controlling != livingEntity) {
            this.setup(level, controlling, firstPerson, mirrored, partialTicks);
            ci.cancel();
            return;
        }

        if (!(entity instanceof Player player))
            return;

        var tug = CameraUtil.getTugData(player);
        if (tug != null) {
            Vec3 direction = player.getLookAngle().lerp(tug.getDirection(player, partialTicks), tug.strength);
            player.lookAt(EntityAnchorArgument.Anchor.EYES, player.getEyePosition().add(direction));
            player.yRotO = player.getYRot();
            player.xRotO = player.getXRot();
        }
    }

    @Unique
    private Double lastYRot = null;

    @Unique
    private float getEntityZOffset(LivingEntity entity) {
        var playerVariantOffset = ProcessTransfur.getPlayerTransfurVariantSafe(EntityUtil.playerOrNull(entity)).map(variant ->
                Mth.lerp(variant.getMorphProgression(Minecraft.getInstance().getDeltaFrameTime()), 0.0f, variant.getParent().cameraZOffset));
        if (playerVariantOffset.isPresent())
            return playerVariantOffset.get();

        var grabberVariantOffset = GrabEntityAbility.getGrabberSafe(entity).map(IAbstractChangedEntity::getSelfVariant).map(variant ->
                variant.cameraZOffset);
        if (grabberVariantOffset.isPresent())
            return grabberVariantOffset.get() + 0.5f;

        if (entity instanceof ChangedEntity changedEntity) {
            var variant = changedEntity.getSelfVariant();
            if (variant != null)
                return variant.cameraZOffset;
        }

        return 0f;
    }

    @Inject(method = "setPosition(Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"), cancellable = true)
    protected void setPositionAndAdjustForVariant(Vec3 vec, CallbackInfo callbackInfo) {
        Camera self = (Camera)(Object)this;
        if (self.getEntity() instanceof Player player && player.isSpectator()) {
            return;
        }

        if (self.getEntity() instanceof LivingEntity livingEntity) {
            float zOffset = getEntityZOffset(livingEntity);
            if (Mth.equal(zOffset, 0f))
                return;

            if (Minecraft.getInstance().options.getCameraType().isFirstPerson())
                zOffset *= 2f;
            double yRot = Math.toRadians(Mth.rotLerp(Minecraft.getInstance().getDeltaFrameTime(), livingEntity.yBodyRotO, livingEntity.yBodyRot));
            if (lastYRot == null)
                lastYRot = yRot;
            yRot = Mth.lerp(0.2f, lastYRot, yRot);
            lastYRot = yRot;
            var newVec = vec.add(-Math.sin(yRot) * zOffset, 0.0f, Math.cos(yRot) * zOffset);
            self.position = newVec;
            self.blockPosition.set(newVec.x, newVec.y, newVec.z);
            callbackInfo.cancel();
        }
    }

    @Override
    public void setCameraPosition(Vec3 position) {
        this.setPosition(position);
    }

    @Override
    public Vec3 getCameraPosition() {
        return this.getPosition();
    }

    @Override
    public Vec3 getFacingDirection() {
        return new Vec3(this.forwards.x(), this.forwards.y(), this.forwards.z());
    }

    @Override
    public Vec3 getUpDirection() {
        return new Vec3(this.up.x(), this.up.y(), this.up.z());
    }

    @Override
    public Vec3 getLeftDirection() {
        return new Vec3(this.left.x(), this.left.y(), this.left.z());
    }
}
