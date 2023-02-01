package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.block.WhiteLatexTransportInterface;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.CameraUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    @Shadow public abstract boolean isSwimming();

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    protected void tryToStartFallFlying(CallbackInfoReturnable<Boolean> ci) {
        Player player = (Player)(Object)this;
        if (latexVariant != null && latexVariant.canGlide) {
            if (!player.isOnGround() && !player.isFallFlying() && !player.isInWater() && !player.hasEffect(MobEffects.LEVITATION)) {
                player.startFallFlying();
                ci.setReturnValue(true);
                ci.cancel();

                //player.respawn();
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        ProcessTransfur.tickPlayerTransfurProgress((Player)(Object)this);
    }

    @Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
    protected void getHurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> ci) {
        if (source == ChangedDamageSources.TRANSFUR)
            ci.setReturnValue(ChangedSounds.BLOW1);
    }

    @Inject(method = "getSwimSound", at = @At("HEAD"), cancellable = true)
    protected void getSwimSound(CallbackInfoReturnable<SoundEvent> ci) {
        if (WhiteLatexTransportInterface.isEntityInWhiteLatex(this)) {
            ci.setReturnValue(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.slime_block.step")));
            ci.cancel();
        }
    }

    // ADDITIONAL DATA
    public LatexVariant<?> latexVariant = null;
    public ProcessTransfur.TransfurProgress transfurProgress = new ProcessTransfur.TransfurProgress(0, LatexVariant.FALLBACK_VARIANT.getFormId());
    public CameraUtil.TugData wantToLookAt;
    public int paleExposure;

    @Inject(method = "makeStuckInBlock", at = @At("HEAD"), cancellable = true)
    public void makeStuckInBlock(BlockState state, Vec3 v3, CallbackInfo ci) {
        if (latexVariant != null)
            if (latexVariant.canClimb && state.is(Blocks.COBWEB))
                ci.cancel();
    }

    private float getFoodMul(LatexVariant<?> variant) {
        if (isSwimming() || this.isEyeInFluid(FluidTags.WATER) || this.isInWater()) {
            return 1.0f - (variant.swimMultiplier() - 1.0f) * 0.85f;
        }

        else if (onGround && isSprinting()) {
            return 1.0f - (variant.landMultiplier() - 1.0f) * 0.85f;
        }

        return 1.0f;
    }

    @Inject(method = "causeFoodExhaustion", at = @At("HEAD"), cancellable = true)
    public void causeFoodExhaustion(float amount, CallbackInfo ci) {
        Player player = (Player)(Object)this;
        if (latexVariant != null && !player.getAbilities().invulnerable && !this.level.isClientSide) {
            ci.cancel();
            player.getFoodData().addExhaustion(amount * getFoodMul(latexVariant));
        }
    }
}
