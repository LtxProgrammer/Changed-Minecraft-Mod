package net.ltxprogrammer.changed.mixin;

import net.ltxprogrammer.changed.block.RetinalScanner;
import net.ltxprogrammer.changed.block.WhiteLatexTransportInterface;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    protected void tryToStartFallFlying(CallbackInfoReturnable<Boolean> ci) {
        Player player = (Player)(Object)this;
        if (ProcessTransfur.isPlayerLatex(player) && ProcessTransfur.getPlayerLatexVariant(player).canGlide()) {
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

    @Inject(method = "causeFallDamage", at = @At("HEAD"), cancellable = true)
    public void causeFallDamage(float p_148859_, float p_148860_, DamageSource p_148861_, CallbackInfoReturnable<Boolean> ci) {
        Player player = (Player)(Object)this;
        if (ProcessTransfur.isPlayerLatex(player) && ProcessTransfur.getPlayerLatexVariant(player).isFallImmune()) {
            ci.setReturnValue(false);
            ci.cancel();
        }
    }

    // ADDITIONAL DATA
    public LatexVariant<?> latexVariant = null;
    public ProcessTransfur.TransfurProgress transfurProgress = new ProcessTransfur.TransfurProgress(0, LatexVariant.LIGHT_LATEX_WOLF.male().getFormId());

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    protected void readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("TransfurProgress") && tag.contains("TransfurProgressType"))
            ProcessTransfur.setPlayerTransfurProgress((Player)(LivingEntity)this,
                    new ProcessTransfur.TransfurProgress(tag.getInt("TransfurProgress"),
                    new ResourceLocation(tag.getString("TransfurProgressType"))));
        if (tag.contains("LatexVariant")) {
            if (latexVariant == null || latexVariant.getFormId().equals(TagUtil.getResourceLocation(tag, "LatexVariant"))) {
                latexVariant = LatexVariant.ALL_LATEX_FORMS.get(TagUtil.getResourceLocation(tag, "LatexVariant")).clone();
                latexVariant.generateForm((Player)(LivingEntity)this, level);
            }
        }
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    protected void addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        tag.putInt("TransfurProgress", ProcessTransfur.getPlayerTransfurProgress((Player)(LivingEntity)this).ticks());
        tag.putString("TransfurProgressType", ProcessTransfur.getPlayerTransfurProgress((Player)(LivingEntity)this).type().toString());
        if (latexVariant != null)
            TagUtil.putResourceLocation(tag, "LatexVariant", latexVariant.getFormId());
    }

    @Inject(method = "makeStuckInBlock", at = @At("HEAD"), cancellable = true)
    public void makeStuckInBlock(BlockState state, Vec3 v3, CallbackInfo ci) {
        if (ProcessTransfur.isPlayerLatex((Player)(Object)this))
            if (ProcessTransfur.getPlayerLatexVariant((Player)(Object)this).canClimb() && state.is(Blocks.COBWEB))
                ci.cancel();
    }
}
