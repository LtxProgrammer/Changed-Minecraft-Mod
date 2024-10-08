package net.ltxprogrammer.changed.mixin.entity;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.block.WhiteLatexTransportInterface;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedAbilities;
import net.ltxprogrammer.changed.init.ChangedAttributes;
import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.network.packet.SyncMoversPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.CameraUtil;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements PlayerDataExtension {
    @Shadow public abstract boolean isSwimming();

    @Shadow public abstract boolean isSpectator();

    @Nullable
    @Unique public PlayerMoverInstance<?> playerMover = null;

    protected PlayerMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "getMyRidingOffset", at = @At("RETURN"), cancellable = true)
    public void getMyRidingOffset(CallbackInfoReturnable<Double> callback) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(this), variant -> {
            callback.setReturnValue(
                Mth.lerp(variant.getMorphProgression(), callback.getReturnValue(), variant.getChangedEntity().getMyRidingOffset())
            );
        });
    }

    @Inject(method = "tryToStartFallFlying", at = @At("HEAD"), cancellable = true)
    protected void tryToStartFallFlying(CallbackInfoReturnable<Boolean> ci) {
        Player player = (Player)(Object)this;
        if (latexVariant != null && latexVariant.getParent().canGlide) {
            if (!player.isOnGround() && !player.isFallFlying() && !player.isInWater() && !player.hasEffect(MobEffects.LEVITATION)) {
                player.startFallFlying();
                ci.setReturnValue(true);
                ci.cancel();

                //player.respawn();
            }
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tickPre(CallbackInfo ci) {
        ProcessTransfur.tickPlayerTransfurProgress((Player)(Object)this);
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void tickPost(CallbackInfo ci) {
        var tug = CameraUtil.getTugData((Player)(Object)this);
        if (tug != null) {
            if (tug.shouldExpire(this)) {
                CameraUtil.resetTugData((Player)(Object)this);
                return;
            }

            tug.ticksExpire--;
        }
    }

    @Inject(method = "getHurtSound", at = @At("HEAD"), cancellable = true)
    protected void getHurtSound(DamageSource source, CallbackInfoReturnable<SoundEvent> ci) {
        if (source instanceof ChangedDamageSources.TransfurDamageSource)
            ci.setReturnValue(ChangedSounds.BLOW1);
    }

    @Inject(method = "getSwimSound", at = @At("HEAD"), cancellable = true)
    protected void getSwimSound(CallbackInfoReturnable<SoundEvent> ci) {
        if (WhiteLatexTransportInterface.isEntityInWhiteLatex(this)) {
            ci.setReturnValue(ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation("block.slime_block.step")));
            ci.cancel();
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void orNotHurt(DamageSource p_36154_, float p_36155_, CallbackInfoReturnable<Boolean> cir) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(this), variant -> {
            if (variant.ageAsVariant < 30)
                cir.cancel();
        });
    }

    @Inject(method = "createAttributes", at = @At("RETURN"))
    private static void addChangedAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        cir.getReturnValue().add(ChangedAttributes.TRANSFUR_DAMAGE.get(), 3.0D);
    }

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    public void transfurAttack(Entity target, CallbackInfo ci) {
        if (!(target instanceof LivingEntity livingEntity))
            return;

        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(this), (player, variant) -> {
            ItemStack attackingItem = this.getItemInHand(InteractionHand.MAIN_HAND);

            // Check if item contributes to transfur damage
            boolean weaponContributes = attackingItem.isEmpty() ||
                    attackingItem.getAttributeModifiers(EquipmentSlot.MAINHAND).containsKey(ChangedAttributes.TRANSFUR_DAMAGE.get());

            if (weaponContributes && variant.getChangedEntity().tryTransfurTarget(target)) {
                attackingItem.hurtEnemy(livingEntity, player);

                ci.cancel();
            }
        });
    }

    @Inject(method = "setItemSlot", at = @At("HEAD"), cancellable = true)
    public void denyInvalidArmor(EquipmentSlot slot, ItemStack item, CallbackInfo ci) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(this), (player, variant) -> {
            if (!variant.canWear(player, item, slot)) {
                ci.cancel();
                this.setItemSlot(EquipmentSlot.MAINHAND, item);
            }
        });
    }

    // ADDITIONAL DATA
    @Unique
    public TransfurVariantInstance<?> latexVariant = null;
    @Unique
    public float transfurProgress = 0.0f;
    @Unique
    public CameraUtil.TugData wantToLookAt;
    @Unique
    public int paleExposure;
    @Unique
    public BasicPlayerInfo basicPlayerInfo = new BasicPlayerInfo();

    @Override
    public TransfurVariantInstance<?> getTransfurVariant() {
        return latexVariant;
    }

    @Override
    public void setTransfurVariant(TransfurVariantInstance<?> latexVariant) {
        this.latexVariant = latexVariant;
    }

    @NotNull
    @Override
    public float getTransfurProgress() {
        return transfurProgress;
    }

    @Override
    public void setTransfurProgress(@NotNull float transfurProgress) {
        this.transfurProgress = transfurProgress;
    }

    @Override
    public CameraUtil.TugData getTugData() {
        return wantToLookAt;
    }

    @Override
    public void setTugData(CameraUtil.TugData data) {
        this.wantToLookAt = data;
    }

    @Override
    public int getPaleExposure() {
        return paleExposure;
    }

    @Override
    public void setPaleExposure(int paleExposure) {
        this.paleExposure = paleExposure;
    }

    @Inject(method = "makeStuckInBlock", at = @At("HEAD"), cancellable = true)
    public void makeStuckInBlock(BlockState state, Vec3 v3, CallbackInfo ci) {
        if (latexVariant != null)
            if (latexVariant.getParent().canClimb && state.is(Blocks.COBWEB))
                ci.cancel();
    }

    @ModifyArg(method = "checkMovementStatistics", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V", ordinal = 0))
    public float foodEfficiencySwimming(float distance) {
        return latexVariant != null ? latexVariant.getSwimEfficiency() * distance : distance;
    }

    @ModifyArg(method = "checkMovementStatistics", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V", ordinal = 1))
    public float foodEfficiencyWalkUnderWater(float distance) {
        return latexVariant != null ? latexVariant.getSwimEfficiency() * distance : distance;
    }

    @ModifyArg(method = "checkMovementStatistics", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V", ordinal = 2))
    public float foodEfficiencyWalkOnWater(float distance) {
        return latexVariant != null ? latexVariant.getSwimEfficiency() * distance : distance;
    }

    @ModifyArg(method = "checkMovementStatistics", at =
    @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V", ordinal = 3))
    public float foodEfficiencySprinting(float distance) {
        return latexVariant != null ? latexVariant.getSprintEfficiency() * distance : distance;
    }

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    public void getDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> callback) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(this), variant -> {
            final float morphProgress = variant.getMorphProgression();

            if (morphProgress < 1f) {
                ChangedEntity ChangedEntity = variant.getChangedEntity();

                final var playerDim = callback.getReturnValue();
                final var latexDim = ChangedEntity.getDimensions(pose);
                float width = Mth.lerp(morphProgress, playerDim.width, latexDim.width);
                float height = Mth.lerp(morphProgress, playerDim.height, latexDim.height);

                callback.setReturnValue(new EntityDimensions(width, height, latexDim.fixed));
            } else {
                callback.setReturnValue(variant.getChangedEntity().getDimensions(pose));
            }
        });
    }

    @Inject(method = "tick", at = @At("RETURN"))
    public void checkGrabbed(CallbackInfo ci) {
        if (this instanceof LivingEntityDataExtension ext) {
            var grabbedBy = ext.getGrabbedBy();
            var ability = AbstractAbility.getAbilityInstance(grabbedBy, ChangedAbilities.GRAB_ENTITY_ABILITY.get());
            if (ability != null && !ability.grabbedHasControl) {
                this.noPhysics = true;
                this.onGround = false;
            }
        }
    }

    @Nullable
    @Override
    public PlayerMoverInstance<?> getPlayerMover() {
        return playerMover;
    }

    @Override
    public void setPlayerMover(@Nullable PlayerMoverInstance<?> playerMover) {
        if (this.playerMover != null)
            this.playerMover.onRemove((Player)(Object)this);

        this.playerMover = playerMover;
        if (!level.isClientSide)
            Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this), SyncMoversPacket.Builder.of((Player)(Object)this));
    }

    @Override
    public BasicPlayerInfo getBasicPlayerInfo() {
        return basicPlayerInfo;
    }

    @Override
    public void setBasicPlayerInfo(BasicPlayerInfo basicPlayerInfo) {
        this.basicPlayerInfo = basicPlayerInfo;
    }
}
