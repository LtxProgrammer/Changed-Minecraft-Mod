package net.ltxprogrammer.changed.mixin.entity;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.commands.CommandSource;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> implements Nameable, EntityAccess, CommandSource, net.minecraftforge.common.extensions.IForgeEntity {
    protected EntityMixin(Class<Entity> baseClass) {
        super(baseClass);
    }

    @Unique
    private @NotNull Entity asEntity() { return (Entity)(Object)this; }

    @Inject(method = "getTicksRequiredToFreeze", at = @At("HEAD"), cancellable = true)
    public void getTicksRequiredToFreeze(CallbackInfoReturnable<Integer> callback) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(asEntity()), variant -> {
            callback.setReturnValue(variant.getChangedEntity().getTicksRequiredToFreeze());
        });
    }

    @Inject(method = "isSwimming", at = @At("HEAD"), cancellable = true)
    public void isSwimming(CallbackInfoReturnable<Boolean> ci) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull((Entity)(Object)this), variant -> {
            if (!variant.getChangedEntity().isAllowedToSwim())
                ci.setReturnValue(false);
        });
    }

    @Inject(method = "getEyeHeight(Lnet/minecraft/world/entity/Pose;Lnet/minecraft/world/entity/EntityDimensions;)F", at = @At("RETURN"), cancellable = true)
    protected void getEyeHeight(Pose pose, EntityDimensions dimensions, CallbackInfoReturnable<Float> callback) {
        if ((asEntity()) instanceof ChangedEntity le) {
            callback.setReturnValue(dimensions.height * le.getEyeHeightMul());
        }

        else ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(asEntity()), variant -> {
            ChangedEntity ChangedEntity = variant.getChangedEntity();
            final float morphProgress = variant.getMorphProgression();

            if (morphProgress < 1f) {
                //callback.setReturnValue(Mth.lerp(morphProgress, callback.getReturnValue(), ChangedEntity.getEyeHeight(pose)));
            } else {
                callback.setReturnValue(ChangedEntity.getEyeHeight(pose));
            }
        });
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    public void interact(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> callback) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(asEntity()), variant -> {
            callback.setReturnValue(variant.getChangedEntity().interact(player, hand));
        });
    }

    @Inject(method = "getPassengersRidingOffset", at = @At("HEAD"), cancellable = true)
    public void getPassengersRidingOffset(CallbackInfoReturnable<Double> callback) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(asEntity()), variant -> {
            callback.setReturnValue(variant.getChangedEntity().getPassengersRidingOffset());
        });
    }

    @Inject(method = "getEyePosition()Lnet/minecraft/world/phys/Vec3;", at = @At("HEAD"), cancellable = true)
    public final void getEyePosition(CallbackInfoReturnable<Vec3> callback) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(asEntity()), (player, variant) -> {
            float z = variant.getParent().cameraZOffset;
            var vec = new Vec3(player.getX(), player.getEyeY(), player.getZ());
            var look = player.getLookAngle().multiply(1.0, 0.0, 1.0).normalize();
            if (Math.abs(look.x()) < 0.0001f && Math.abs(look.z()) < 0.0001f)
                look = player.getUpVector(1.0f).multiply(1.0, 0.0, 1.0).normalize();
            callback.setReturnValue(vec.add(look.x() * z, 0.0f, look.z() * z));
        });
    }

    @Inject(method = "getEyePosition(F)Lnet/minecraft/world/phys/Vec3;", at = @At("HEAD"), cancellable = true)
    public final void getEyePosition(float v, CallbackInfoReturnable<Vec3> callback) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(asEntity()), (player, variant) -> {
            float z = variant.getParent().cameraZOffset;
            if (Math.abs(z) < 0.0001f) return;
            var look = player.getLookAngle().multiply(1.0, 0.0, 1.0).normalize();
            if (Math.abs(look.x()) < 0.0001f && Math.abs(look.z()) < 0.0001f)
                look = player.getUpVector(1.0f).multiply(1.0, 0.0, 1.0).normalize();

            double d0 = Mth.lerp(v, player.xo + look.x() * z, player.getX() + look.x() * z);
            double d1 = Mth.lerp(v, player.yo, player.getY()) + (double) player.getEyeHeight();
            double d2 = Mth.lerp(v, player.zo + look.z() * z, player.getZ() + look.z() * z);
            callback.setReturnValue(new Vec3(d0, d1, d2));
        });
    }

    @Inject(method = "isInWall", at = @At("HEAD"), cancellable = true)
    public void isInWall(CallbackInfoReturnable<Boolean> callback) {
        if (asEntity() instanceof Player player && ProcessTransfur.isPlayerLatex(player)) {
            if (player.noPhysics) {
                callback.setReturnValue(false);
            } else {
                float f = player.getDimensions(player.getPose()).width * 0.8F;
                AABB aabb = AABB.ofSize(new Vec3(player.getX(), player.getEyeY(), player.getZ()), (double)f, 1.0E-6D, (double)f);
                callback.setReturnValue(BlockPos.betweenClosedStream(aabb).anyMatch((p_201942_) -> {
                    BlockState blockstate = player.level.getBlockState(p_201942_);
                    return !blockstate.isAir() && blockstate.isSuffocating(player.level, p_201942_) && Shapes.joinIsNotEmpty(blockstate.getCollisionShape(player.level, p_201942_).move((double)p_201942_.getX(), (double)p_201942_.getY(), (double)p_201942_.getZ()), Shapes.create(aabb), BooleanOp.AND);
                }));
            }
        }
    }

    @Inject(method = "canEnterPose", at = @At("HEAD"), cancellable = true)
    public void canEnterPose(Pose pose, CallbackInfoReturnable<Boolean> callback) {
        if (!((Entity)(Object)this instanceof LivingEntity livingEntity)) return;

        ProcessTransfur.getEntityVariant(livingEntity).flatMap(variant -> {
            if (!variant.hasLegs && livingEntity.isEyeInFluid(FluidTags.WATER)) {
                return Optional.of(pose == Pose.SWIMMING);
            } else
                return Optional.empty();
        }).ifPresent(callback::setReturnValue);
    }

    @Shadow public abstract boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> tag, double scale);

    @Inject(method = "updateInWaterStateAndDoFluidPushing", at = @At("RETURN"), cancellable = true)
    protected void updateInWaterStateAndDoFluidPushing(CallbackInfoReturnable<Boolean> callback) {
        if (this.updateFluidHeightAndDoFluidPushing(ChangedTags.Fluids.LATEX, 0.007D))
            callback.setReturnValue(true);
    }
}