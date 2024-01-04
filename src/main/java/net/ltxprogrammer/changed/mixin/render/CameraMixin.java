package net.ltxprogrammer.changed.mixin.render;

import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.client.CameraExtender;
import net.ltxprogrammer.changed.client.renderer.LatexHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModelInterface;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.CameraUtil;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
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

    @Unique
    private <T extends LatexEntity> void adjustAnimForEntity(T latexEntity, float partialTicks) {
        if (Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(latexEntity) instanceof LatexHumanoidRenderer<?,?,?> latexHumanoid &&
                latexHumanoid.getModel(latexEntity) instanceof LatexHumanoidModelInterface latexHumanoidModel) {
            boolean shouldSit = latexEntity.isPassenger() && (latexEntity.getVehicle() != null && latexEntity.getVehicle().shouldRiderSit());
            float f = Mth.rotLerp(partialTicks, latexEntity.yBodyRotO, latexEntity.yBodyRot);
            float f1 = Mth.rotLerp(partialTicks, latexEntity.yHeadRotO, latexEntity.yHeadRot);
            float netHeadYaw = f1 - f;
            if (shouldSit && latexEntity.getVehicle() instanceof LivingEntity livingentity) {
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

            float headPitch = Mth.lerp(partialTicks, latexEntity.xRotO, latexEntity.getXRot());
            if (LivingEntityRenderer.isEntityUpsideDown(latexEntity)) {
                headPitch *= -1.0F;
                netHeadYaw *= -1.0F;
            }

            float limbSwingAmount = 0.0F;
            float limbSwing = 0.0F;
            if (!shouldSit && latexEntity.isAlive()) {
                limbSwingAmount = Mth.lerp(partialTicks, latexEntity.animationSpeedOld, latexEntity.animationSpeed);
                limbSwing = latexEntity.animationPosition - latexEntity.animationSpeed * (1.0F - partialTicks);
                if (latexEntity.isBaby()) {
                    limbSwing *= 3.0F;
                }

                if (limbSwingAmount > 1.0F) {
                    limbSwingAmount = 1.0F;
                }
            }

            var animator = latexHumanoidModel.getAnimator();
            animator.setupVariables(latexEntity, partialTicks);
            animator.setupCameraAnim(this, latexEntity,
                    limbSwing,
                    limbSwingAmount,
                    latexEntity.tickCount + partialTicks,
                    netHeadYaw,
                    headPitch
            );
        }
    }

    @Inject(method = "setup", at = @At("RETURN"))
    public void animateCamera(BlockGetter level, Entity entity, boolean p_90578_, boolean p_90579_, float partialTicks, CallbackInfo ci) {
        if (entity instanceof LatexEntity latexEntity)
            adjustAnimForEntity(latexEntity, partialTicks);

        else if (entity instanceof Player player) {
            ProcessTransfur.ifPlayerLatex(player, variant -> {
                adjustAnimForEntity(variant.getLatexEntity(), partialTicks);
            });
        }
    }

    @Inject(method = "setup", at = @At("HEAD"))
    public void setupWithTug(BlockGetter level, Entity entity, boolean firstPerson, boolean mirrored, float partialTicks, CallbackInfo ci) {
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

    @Inject(method = "setPosition(Lnet/minecraft/world/phys/Vec3;)V", at = @At("HEAD"), cancellable = true)
    protected void setPositionAndAdjustForVariant(Vec3 vec, CallbackInfo callbackInfo) {
        Camera self = (Camera)(Object)this;
        if (self.getEntity() instanceof Player player && !player.isSpectator()) {
            ProcessTransfur.ifPlayerLatex(player, variant -> {
                float z = variant.getParent().cameraZOffset;
                var look = self.getLookVector().copy();
                if (Math.abs(look.x()) < 0.0001f && Math.abs(look.z()) < 0.0001f)
                    look = self.getUpVector().copy();
                look.mul(1.0f, 0.0f, 1.0f);
                look.normalize();
                var newVec = vec.add(look.x() * z, 0.0f, look.z() * z);
                self.position = newVec;
                self.blockPosition.set(newVec.x, newVec.y, newVec.z);
                callbackInfo.cancel();
            });
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
