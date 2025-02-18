package net.ltxprogrammer.changed.mixin.entity;

import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.GrabEntityAbility;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.entity.SeatEntity;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.util.StackUtil;
import net.minecraft.commands.CommandSource;
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
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
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

    @Unique
    private boolean isCallingIsInWall = false;

    @Inject(method = "isInWall", at = @At("HEAD"))
    public void startIsInWall(CallbackInfoReturnable<Boolean> cir) {
        isCallingIsInWall = true;
    }

    @Inject(method = "isInWall", at = @At("TAIL"))
    public void endIsInWall(CallbackInfoReturnable<Boolean> cir) {
        isCallingIsInWall = false;
    }

    @Inject(method = "getEyePosition()Lnet/minecraft/world/phys/Vec3;", at = @At("HEAD"), cancellable = true)
    public final void getEyePosition(CallbackInfoReturnable<Vec3> callback) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(asEntity()), (player, variant) -> {
            float z = Mth.lerp(variant.getMorphProgression(), 0.0f, variant.getParent().cameraZOffset);
            if (Math.abs(z) < 0.0001f) return;
            if (isCallingIsInWall) return; // Ignore
            double yRot = Math.toRadians(player.yBodyRot);

            var vec = new Vec3(player.getX(), player.getEyeY(), player.getZ());
            callback.setReturnValue(vec.add(-Math.sin(yRot) * z, 0.0f, Math.cos(yRot) * z));
        });
    }

    @Inject(method = "getEyePosition(F)Lnet/minecraft/world/phys/Vec3;", at = @At("HEAD"), cancellable = true)
    public final void getEyePosition(float v, CallbackInfoReturnable<Vec3> callback) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(asEntity()), (player, variant) -> {
            float z = Mth.lerp(variant.getMorphProgression(), 0.0f, variant.getParent().cameraZOffset);
            if (Math.abs(z) < 0.0001f) return;
            if (isCallingIsInWall) return; // Ignore
            double yRot = Math.toRadians(Mth.rotLerp(v, player.yBodyRotO, player.yBodyRot));

            double d0 = Mth.lerp(v, player.xo + -Math.sin(yRot) * z, player.getX() + -Math.sin(yRot) * z);
            double d1 = Mth.lerp(v, player.yo, player.getY()) + (double) player.getEyeHeight();
            double d2 = Mth.lerp(v, player.zo + Math.cos(yRot) * z, player.getZ() + Math.cos(yRot) * z);
            callback.setReturnValue(new Vec3(d0, d1, d2));
        });
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

    @Shadow public abstract Vec3 getEyePosition();

    @Inject(method = "updateInWaterStateAndDoFluidPushing", at = @At("RETURN"), cancellable = true)
    protected void updateInWaterStateAndDoFluidPushing(CallbackInfoReturnable<Boolean> callback) {
        if (this.updateFluidHeightAndDoFluidPushing(ChangedTags.Fluids.LATEX, 0.007D))
            callback.setReturnValue(true);
    }

    @Inject(method = "isInvisible", at = @At("RETURN"), cancellable = true)
    public void hideSeatedEntity(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && (Entity)(Object)this instanceof LivingEntity livingEntity) {
            if (livingEntity.vehicle != null && livingEntity.vehicle instanceof SeatEntity seat) {
                if (seat.shouldSeatedBeInvisible()) {
                    cir.setReturnValue(true);
                }
            }
        }

        if (this instanceof LivingEntityDataExtension ext) {
            boolean shouldRender = AbstractAbility.getAbilityInstanceSafe(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get())
                    .map(ability -> !(ability.suited && !ability.grabbedHasControl))
                    .orElse(true);

            if (!shouldRender) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "isSilent", at = @At("RETURN"), cancellable = true)
    public void makeGrabbedEntityQuiet(CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof LivingEntityDataExtension ext) {
            boolean shouldRender = AbstractAbility.getAbilityInstanceSafe(ext.getGrabbedBy(), ChangedAbilities.GRAB_ENTITY_ABILITY.get())
                    .map(ability -> !(ability.suited && !ability.grabbedHasControl))
                    .orElse(true);

            if (!shouldRender) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "isAttackable", at = @At("RETURN"), cancellable = true)
    public void ignoreGrabbedEntities(CallbackInfoReturnable<Boolean> cir) {
        if ((Entity)(Object)this instanceof LivingEntity livingEntity)
            GrabEntityAbility.getGrabberSafe(livingEntity).flatMap(grabber -> grabber.getAbilityInstanceSafe(ChangedAbilities.GRAB_ENTITY_ABILITY.get()))
                    .ifPresent(ability -> {
                        if (ability.suited)
                            cir.setReturnValue(false);
                    });
    }
}