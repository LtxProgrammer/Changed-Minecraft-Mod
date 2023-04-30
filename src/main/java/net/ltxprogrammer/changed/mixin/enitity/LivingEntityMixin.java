package net.ltxprogrammer.changed.mixin.enitity;

import net.ltxprogrammer.changed.block.WearableBlock;
import net.ltxprogrammer.changed.entity.LivingEntityDataExtension;
import net.ltxprogrammer.changed.item.SpecializedAnimations;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.LocalUtil;
import net.ltxprogrammer.changed.util.Util;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.spongepowered.asm.mixin.Mixin;
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
            if (variant.getParent().restrained && effect.equals(MobEffects.DIG_SLOWDOWN))
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
            if (variant.getParent().restrained && effect.equals(MobEffects.DIG_SLOWDOWN))
                callback.setReturnValue(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 300, 1, false, false));
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
}
