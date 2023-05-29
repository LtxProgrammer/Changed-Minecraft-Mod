package net.ltxprogrammer.changed.mixin.entity;

import net.ltxprogrammer.changed.block.WearableBlock;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.fluid.AbstractLatexFluid;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.ltxprogrammer.changed.item.SpecializedAnimations;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.LocalUtil;
import net.ltxprogrammer.changed.util.Util;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityDataExtension {
    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Unique
    public int controlDisabledFor = 0;

    @Override
    public int getNoControlTicks() {
        return controlDisabledFor;
    }

    @Override
    public void setNoControlTicks(int ticks) {
        this.controlDisabledFor = ticks;
    }

    @Inject(method = "updateFallFlying", at = @At("HEAD"), cancellable = true)
    private void updateFallFlying(CallbackInfo callback) {
        if (this.level.isClientSide) return;
        ProcessTransfur.ifPlayerLatex(Util.playerOrNull(this), (player, variant) -> {
            if (variant.getParent().canGlide) {
                this.setSharedFlag(7, player.isFallFlying() && !player.isOnGround() && !player.isPassenger() && !player.hasEffect(MobEffects.LEVITATION));
                callback.cancel();
            }
        });
    }

    @Inject(method = "onClimbable", at = @At("HEAD"), cancellable = true)
    public void onClimbable(CallbackInfoReturnable<Boolean> callback) {
        ProcessTransfur.ifPlayerLatex(Util.playerOrNull(this), (variant) -> {
            if (variant.getParent().canClimb && this.horizontalCollision)
                callback.setReturnValue(true);
        });
    }

    @Inject(method = "getJumpBoostPower", at = @At("RETURN"), cancellable = true)
    public void getJumpBoostPower(CallbackInfoReturnable<Double> callback) {
        ProcessTransfur.ifPlayerLatex(Util.playerOrNull(this), (variant) -> {
            callback.setReturnValue(callback.getReturnValue() * variant.getParent().jumpStrength);
        });
    }

    @Inject(method = "hasEffect", at = @At("HEAD"), cancellable = true)
    public void hasEffect(MobEffect effect, CallbackInfoReturnable<Boolean> callback) {
        ProcessTransfur.ifPlayerLatex(Util.playerOrNull(this), (variant) -> {
            if (variant.getParent().nightVision && effect.equals(MobEffects.NIGHT_VISION))
                callback.setReturnValue(true);
            if (variant.getParent().breatheMode.canBreatheWater() && effect.equals(MobEffects.CONDUIT_POWER))
                callback.setReturnValue(true);
            if (variant.getParent().noVision && effect.equals(MobEffects.BLINDNESS))
                callback.setReturnValue(true);
        });
    }

    @Inject(method = "getEffect", at = @At("HEAD"), cancellable = true)
    public void getEffect(MobEffect effect, CallbackInfoReturnable<MobEffectInstance> callback) {
        ProcessTransfur.ifPlayerLatex(Util.playerOrNull(this), (variant) -> {
            if (variant.getParent().nightVision && effect.equals(MobEffects.NIGHT_VISION))
                callback.setReturnValue(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 1, false, false));
            if (variant.getParent().breatheMode.canBreatheWater() && effect.equals(MobEffects.CONDUIT_POWER))
                callback.setReturnValue(new MobEffectInstance(MobEffects.CONDUIT_POWER, 300, 1, false, false));
            if (variant.getParent().noVision && effect.equals(MobEffects.BLINDNESS))
                callback.setReturnValue(new MobEffectInstance(MobEffects.BLINDNESS, 300, 1, false, false));
        });
    }

    @Inject(method = "getEquipmentSlotForItem", at = @At("HEAD"), cancellable = true)
    private static void getEquipmentSlotForItem(ItemStack stack, CallbackInfoReturnable<EquipmentSlot> callback) {
        if (stack.getItem() instanceof BlockItem blockItem)
            if (blockItem.getBlock() instanceof WearableBlock wearableBlock)
                callback.setReturnValue(wearableBlock.getEquipmentSlot());
    }

    @Inject(method = "isVisuallySwimming", at = @At("HEAD"), cancellable = true)
    private void isVisuallySwimming(CallbackInfoReturnable<Boolean> callback) {
        if ((LivingEntity)(Object)this instanceof Player player) {
            ProcessTransfur.ifPlayerLatex(player, (variant) -> {
                if (variant.getLatexEntity().isVisuallySwimming())
                    callback.setReturnValue(true);
            });
        }
    }

    @Inject(method = "triggerItemUseEffects", at = @At("HEAD"), cancellable = true)
    protected void triggerItemUseEffects(ItemStack itemStack, int particleCount, CallbackInfo callbackInfo) {
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof SpecializedAnimations specialized) {
            if (specialized.triggerItemUseEffects((LivingEntity)(Entity)this, itemStack, particleCount))
                callbackInfo.cancel();
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo callback) {
        if (controlDisabledFor > 0) {
            if ((Entity)this instanceof Mob mob) {
                MoveControl move = mob.getMoveControl();
                move.setWantedPosition(move.getWantedX(), move.getWantedY(), move.getWantedZ(), move.getSpeedModifier());
            }

            if ((Entity)this instanceof Player player)
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> LocalUtil.mulInputImpulse(player, 0.05F));

            --controlDisabledFor;
        }
    }

    @Inject(method = "canStandOnFluid", at = @At("HEAD"), cancellable = true)
    public void canStandOnFluid(FluidState state, CallbackInfoReturnable<Boolean> callback) {
        var variant = LatexVariant.getEntityVariant((LivingEntity)(Object)this);
        if (variant == null) return;
        if (variant.getLatexType() == LatexType.NEUTRAL) return;

        if (state.getType() instanceof AbstractLatexFluid latexFluid && latexFluid.canEntityStandOn((LivingEntity)(Object)this))
            callback.setReturnValue(true);
    }

    @Shadow public abstract boolean canStandOnFluid(FluidState state);
    @Shadow public abstract boolean isEffectiveAi();
    @Shadow public abstract boolean hasEffect(MobEffect effect);
    @Shadow public abstract AttributeInstance getAttribute(Attribute attribute);
    @Shadow protected abstract boolean isAffectedByFluids();
    @Shadow public abstract Vec3 getFluidFallingAdjustedMovement(double d0, boolean flag, Vec3 movement);
    @Shadow(remap = false) @Final private static AttributeModifier SLOW_FALLING;

    @Unique private boolean isInLatex() {
        return !this.firstTick && this.fluidHeight.getDouble(ChangedTags.Fluids.LATEX) > 0.0D;
    }

    @Inject(method = "travel", at = @At("HEAD"))
    public void travel(Vec3 direction, CallbackInfo callback) {
        if (this.isEffectiveAi() || this.isControlledByLocalInstance()) {
            double d0 = 0.08D;
            AttributeInstance gravity = this.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
            boolean flag = this.getDeltaMovement().y <= 0.0D;
            if (flag && this.hasEffect(MobEffects.SLOW_FALLING)) {
                if (!gravity.hasModifier(SLOW_FALLING)) gravity.addTransientModifier(SLOW_FALLING);
                this.resetFallDistance();
            } else if (gravity.hasModifier(SLOW_FALLING)) {
                gravity.removeModifier(SLOW_FALLING);
            }
            d0 = gravity.getValue();

            FluidState fluidstate = this.level.getFluidState(this.blockPosition());
            if (this.isInLatex() && this.isAffectedByFluids() && !this.canStandOnFluid(fluidstate)) {
                double d8 = this.getY();
                this.moveRelative(0.02F, direction);
                this.move(MoverType.SELF, this.getDeltaMovement());
                if (this.getFluidHeight(ChangedTags.Fluids.LATEX) <= this.getFluidJumpThreshold()) {
                    this.setDeltaMovement(this.getDeltaMovement().multiply(0.5D, (double)0.8F, 0.5D));
                    Vec3 vec33 = this.getFluidFallingAdjustedMovement(d0, flag, this.getDeltaMovement());
                    this.setDeltaMovement(vec33);
                } else {
                    this.setDeltaMovement(this.getDeltaMovement().scale(0.5D));
                }

                if (!this.isNoGravity()) {
                    this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -d0 / 4.0D, 0.0D));
                }

                Vec3 vec34 = this.getDeltaMovement();
                if (this.horizontalCollision && this.isFree(vec34.x, vec34.y + (double)0.6F - this.getY() + d8, vec34.z)) {
                    this.setDeltaMovement(vec34.x, (double)0.3F, vec34.z);
                }
            }
        }
    }
}
