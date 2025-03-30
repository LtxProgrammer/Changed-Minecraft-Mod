package net.ltxprogrammer.changed.mixin.entity;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.ability.GrabEntityAbility;
import net.ltxprogrammer.changed.block.StasisChamber;
import net.ltxprogrammer.changed.block.ThreeXThreeSection;
import net.ltxprogrammer.changed.block.WearableBlock;
import net.ltxprogrammer.changed.block.WhiteLatexTransportInterface;
import net.ltxprogrammer.changed.block.entity.StasisChamberBlockEntity;
import net.ltxprogrammer.changed.data.AccessorySlotType;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.fluid.AbstractLatexFluid;
import net.ltxprogrammer.changed.fluid.Gas;
import net.ltxprogrammer.changed.fluid.TransfurGas;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.item.ExtendedItemProperties;
import net.ltxprogrammer.changed.item.SpecializedAnimations;
import net.ltxprogrammer.changed.network.packet.AccessorySyncPacket;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityDataExtension {
    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Unique
    public int controlDisabledFor = 0;
    @Unique @Nullable
    public LivingEntity grabbedBy = null;
    @Unique
    public AccessorySlots accessorySlots = Util.make(new AccessorySlots((LivingEntity)(Object)this), slots -> {
        slots.initialize(
                AccessoryEntities.INSTANCE.canEntityTypeUseSlot(AccessoryEntities.getApparentEntityType(slots.owner)),
                AccessorySlots.defaultInvalidHandler(slots.owner));
    });

    @Override
    public int getNoControlTicks() {
        return controlDisabledFor;
    }

    @Override
    public void setNoControlTicks(int ticks) {
        this.controlDisabledFor = ticks;
    }

    @Nullable
    @Override
    public LivingEntity getGrabbedBy() {
        return grabbedBy;
    }

    @Override
    public void setGrabbedBy(@Nullable LivingEntity grabbedBy) {
        this.grabbedBy = grabbedBy;
    }

    @Override
    public Optional<AccessorySlots> getAccessorySlots() {
        return Optional.of(accessorySlots);
    }

    @Inject(method = "updateFallFlying", at = @At("HEAD"), cancellable = true)
    private void updateFallFlying(CallbackInfo callback) {
        if (this.level.isClientSide) return;
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(this), (player, variant) -> {
            if (variant.getParent().canGlide) {
                this.setSharedFlag(7, player.isFallFlying() && !player.isOnGround() && !player.isPassenger() && !player.hasEffect(MobEffects.LEVITATION));
                callback.cancel();
            }
        });
    }

    @Inject(method = "onClimbable", at = @At("HEAD"), cancellable = true)
    public void onClimbable(CallbackInfoReturnable<Boolean> callback) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(this), (variant) -> {
            if (variant.getParent().canClimb && this.horizontalCollision)
                callback.setReturnValue(true);
        });
    }

    @Inject(method = "getJumpPower", at = @At("RETURN"), cancellable = true)
    public void getJumpPower(CallbackInfoReturnable<Float> callback) {
        ProcessTransfur.getEntityVariant((LivingEntity)(Object)this).map(variant -> callback.getReturnValue() * variant.jumpStrength).ifPresent(callback::setReturnValue);
    }

    @Inject(method = "hasEffect", at = @At("HEAD"), cancellable = true)
    public void hasEffect(MobEffect effect, CallbackInfoReturnable<Boolean> callback) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(this), (player, variant) -> {
            if (variant.visionType.test(effect))
                callback.setReturnValue(true);

            if (variant.miningStrength.test(effect))
                callback.setReturnValue(true);

            if (effect.equals(MobEffects.NIGHT_VISION)) {
                if (variant.getChangedEntity().getLatexType() == LatexType.WHITE_LATEX && WhiteLatexTransportInterface.isEntityInWhiteLatex(player))
                    callback.setReturnValue(true);
            }
            if (variant.getParent().breatheMode.canBreatheWater() && effect.equals(MobEffects.CONDUIT_POWER) && isEyeInFluid(FluidTags.WATER))
                callback.setReturnValue(true);
        });
    }

    @Inject(method = "getEffect", at = @At("HEAD"), cancellable = true)
    public void getEffect(MobEffect effect, CallbackInfoReturnable<MobEffectInstance> callback) {
        ProcessTransfur.ifPlayerTransfurred(EntityUtil.playerOrNull(this), (player, variant) -> {
            if (variant.visionType.test(effect))
                callback.setReturnValue(new MobEffectInstance(effect, 300, 1, false, false));

            if (variant.miningStrength.test(effect))
                callback.setReturnValue(new MobEffectInstance(effect, 300, 1, false, false));

            if (effect.equals(MobEffects.NIGHT_VISION)) {
                if (variant.getChangedEntity().getLatexType() == LatexType.WHITE_LATEX && WhiteLatexTransportInterface.isEntityInWhiteLatex(player))
                    callback.setReturnValue(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 1, false, false));
            }
            if (variant.getParent().breatheMode.canBreatheWater() && effect.equals(MobEffects.CONDUIT_POWER) && isEyeInFluid(FluidTags.WATER))
                callback.setReturnValue(new MobEffectInstance(MobEffects.CONDUIT_POWER, 300, 1, false, false));
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
            ProcessTransfur.ifPlayerTransfurred(player, (variant) -> {
                if (variant.getChangedEntity().isVisuallySwimming())
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

    @Unique
    private Map<AccessorySlotType, ItemStack> collectAccessoryChanges() {
        Map<AccessorySlotType, ItemStack> map = null;

        for (AccessorySlotType slotType : ChangedRegistry.ACCESSORY_SLOTS.get().getValues()) {
            ItemStack lastStack = accessorySlots.getLastItem(slotType);
            ItemStack newStack = accessorySlots.getItem(slotType).orElse(ItemStack.EMPTY);
            if (!ItemStack.matches(newStack, lastStack)) {
                //net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent(this, equipmentslot, lastStack, newStack));
                if (map == null) {
                    map = new HashMap<>();
                }

                map.put(slotType, newStack);
                if (!lastStack.isEmpty()) {
                    this.getAttributes().removeAttributeModifiers(lastStack.getAttributeModifiers(slotType.getEquivalentSlot()));
                }

                if (!newStack.isEmpty()) {
                    this.getAttributes().addTransientAttributeModifiers(newStack.getAttributeModifiers(slotType.getEquivalentSlot()));
                }
            }
        }

        return map;
    }

    @Unique
    private void handleAccessoryChanges(Map<AccessorySlotType, ItemStack> map) {
        map.forEach((slotType, stack) -> {
            accessorySlots.setLastItem(slotType, stack.copy());
        });
        Changed.PACKET_HANDLER.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this), new AccessorySyncPacket(this.getId(), map));
    }

    @Inject(method = "detectEquipmentUpdates", at = @At("RETURN"))
    private void detectAccessoryUpdates(CallbackInfo ci) {
        Map<AccessorySlotType, ItemStack> map = this.collectAccessoryChanges();
        if (map != null && !map.isEmpty())
            this.handleAccessoryChanges(map);
    }

    @Unique @Nullable private Gas eyeInGas = null;

    private void checkForGas() {
        eyeInGas = null;
        // Code from Entity.updateFluidOnEyes()
        double yCheck = this.getEyeY() - 0.11111111;

        BlockPos blockpos = new BlockPos(this.getX(), yCheck, this.getZ());
        FluidState fluidstate = this.level.getFluidState(blockpos);
        double yFluid = ((float)blockpos.getY() + fluidstate.getHeight(this.level, blockpos));
        if (yFluid > yCheck && fluidstate.getType() instanceof Gas transfurGas)
            eyeInGas = transfurGas;

        var blockstate = this.level.getBlockState(blockpos);
        if (blockstate.is(ChangedBlocks.STASIS_CHAMBER.get())) {
            this.level.getBlockEntity(
                    blockstate.getValue(StasisChamber.SECTION).getRelative(blockpos, blockstate.getValue(HorizontalDirectionalBlock.FACING), ThreeXThreeSection.CENTER),
                    ChangedBlockEntities.STASIS_CHAMBER.get()
            ).filter(chamber -> chamber.getFluidYHeight() > yCheck).flatMap(StasisChamberBlockEntity::getFluidType).ifPresent(fluid -> {
                fluid.defaultFluidState().getTags().forEach(this.fluidOnEyes::add);
                if (fluid instanceof Gas gas)
                    eyeInGas = gas;
            });
        }
    }

    @Override
    public <T extends Gas> Optional<T> isEyeInGas(Class<T> clazz) {
        return Optional.ofNullable(eyeInGas).flatMap(gas -> {
            if (clazz.isAssignableFrom(gas.getClass()))
                return Optional.of((T)gas);
            else
                return Optional.empty();
        });
    }

    @Override
    public void do_hurtCurrentlyUsedShield(float blocked) {
        this.hurtCurrentlyUsedShield(blocked);
    }

    @Override
    public void do_blockUsingShield(LivingEntity attacker) {
        this.blockUsingShield(attacker);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo callback) {
        if (controlDisabledFor > 0) {
            if ((Entity)this instanceof Mob mob) {
                MoveControl move = mob.getMoveControl();
                move.setWantedPosition(move.getWantedX(), move.getWantedY(), move.getWantedZ(), move.getSpeedModifier());
            }

            /*if ((Entity)this instanceof Player player)
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> LocalUtil.mulInputImpulse(player, 0.05F));*/

            --controlDisabledFor;
        }

        if (grabbedBy != null) {
            if (grabbedBy.isDeadOrDying() || grabbedBy.isRemoved())
                grabbedBy = null;
            if (grabbedBy instanceof Player player && player.isSpectator())
                grabbedBy = null;
        }

        this.checkForGas();
    }

    @Inject(method = "canStandOnFluid", at = @At("HEAD"), cancellable = true)
    public void canStandOnFluid(FluidState state, CallbackInfoReturnable<Boolean> callback) {
        var variant = TransfurVariant.getEntityVariant((LivingEntity)(Object)this);
        if (variant == null) return;
        if (variant.getLatexType() == LatexType.NEUTRAL) return;

        if (state.getType() instanceof AbstractLatexFluid latexFluid && latexFluid.canEntityStandOn((LivingEntity)(Object)this))
            callback.setReturnValue(true);
    }

    @Inject(method = "breakItem", at = @At("HEAD"), cancellable = true)
    public void useDifferentBreakSound(ItemStack itemStack, CallbackInfo ci) {
        if (!(itemStack.getItem() instanceof ExtendedItemProperties extended) || itemStack.isEmpty())
            return;

        if (!this.isSilent()) {
            this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), extended.getBreakSound(itemStack), this.getSoundSource(), 0.8F, 0.8F + this.level.random.nextFloat() * 0.4F, false);
        }

        this.spawnItemParticles(itemStack, 5);

        ci.cancel();
    }

    @Shadow public abstract boolean canStandOnFluid(FluidState state);
    @Shadow public abstract boolean isEffectiveAi();
    @Shadow public abstract boolean hasEffect(MobEffect effect);
    @Shadow public abstract AttributeInstance getAttribute(Attribute attribute);
    @Shadow protected abstract boolean isAffectedByFluids();
    @Shadow public abstract Vec3 getFluidFallingAdjustedMovement(double d0, boolean flag, Vec3 movement);
    @Shadow(remap = false) @Final private static AttributeModifier SLOW_FALLING;

    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot p_21127_);

    @Shadow protected abstract void spawnItemParticles(ItemStack p_21061_, int p_21062_);

    @Shadow protected abstract void hurtCurrentlyUsedShield(float p_21316_);

    @Shadow protected abstract void blockUsingShield(LivingEntity p_21200_);

    @Shadow public abstract AttributeMap getAttributes();

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

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getFriction(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F", remap = false))
    public float getFrictionForEntity(BlockState instance, LevelReader levelReader, BlockPos pos, Entity entity) {
        return EntityUtil.getFrictionOnBlock(instance, levelReader, pos, entity);
    }

    @Inject(method = "push", at = @At("HEAD"), cancellable = true)
    public void pushEntityIfNotGrabbed(Entity entity, CallbackInfo callback) {
        var ability = AbstractAbility.getAbilityInstance((LivingEntity)(Object)this, ChangedAbilities.GRAB_ENTITY_ABILITY.get());
        if (ability != null && ability.grabbedEntity == entity) { // Grabbed entity is called to push
            callback.cancel();
            return;
        }

        if (this.grabbedBy == entity) {
            callback.cancel();
            return;
        }
    }

    @Inject(method = "isBlocking", at = @At("RETURN"), cancellable = true)
    public void isControllerBlocking(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue())
            return;

        LivingEntity controlling = GrabEntityAbility.getControllingEntity((LivingEntity)(Object)this);
        if (controlling == (Object)this)
            return;

        cir.setReturnValue(controlling.isBlocking());
    }

    @Inject(method = "createLivingAttributes", at = @At("RETURN"))
    private static void addChangedAttributes(CallbackInfoReturnable<AttributeSupplier.Builder> cir) {
        cir.getReturnValue().add(ChangedAttributes.TRANSFUR_TOLERANCE.get());
    }

    @Inject(method = "increaseAirSupply", at = @At("HEAD"), cancellable = true)
    private void maybeAddAir(int current, CallbackInfoReturnable<Integer> cir) {
        TransfurGas.validEntityInGas((LivingEntity)(Object)this).ifPresent(gas -> cir.setReturnValue(current));
    }

    @Inject(method = "checkBedExists", at = @At("HEAD"), cancellable = true)
    private void bedExistsOrIsStabilized(CallbackInfoReturnable<Boolean> cir) {
        if (StasisChamber.isEntityStabilized((LivingEntity)(Object)this))
            cir.setReturnValue(true);
    }

    @Inject(method = "isSleeping", at = @At("HEAD"), cancellable = true)
    public void isSleepingOrStabilized(CallbackInfoReturnable<Boolean> cir) {
        if (StasisChamber.isEntityStabilized((LivingEntity)(Object)this))
            cir.setReturnValue(true);
    }

    @Inject(method = "stopSleeping", at = @At("HEAD"), cancellable = true)
    public void unlessIsStabilizedAndMultiplayer(CallbackInfo ci) {
        if (level instanceof ServerLevel serverLevel) {
            if (serverLevel.players().stream().filter(player -> !player.isSpectator()).count() == 1) {
                // Singleplayer, just skip stasis time
                if (this.vehicle instanceof SeatEntity seatEntity) {
                    this.level.getBlockEntity(seatEntity.getAttachedBlockPos(), ChangedBlockEntities.STASIS_CHAMBER.get())
                            .ifPresent(StasisChamberBlockEntity::trimSchedule);
                }
                return;
            }
        }
        if (StasisChamber.isEntityStabilized((LivingEntity)(Object)this))
            ci.cancel();
    }

    @Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
    public void addExtendedData(CompoundTag tag, CallbackInfo ci) {
        tag.put("ChangedAccessorySlots", accessorySlots.save());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
    public void readExtendedData(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("ChangedAccessorySlots"))
            accessorySlots.load(tag.getCompound("ChangedAccessorySlots"));
    }
}
