package net.ltxprogrammer.changed.entity.robot;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.block.AbstractLargePanel;
import net.ltxprogrammer.changed.block.NineSection;
import net.ltxprogrammer.changed.data.AccessorySlots;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.item.BenignShorts;
import net.ltxprogrammer.changed.item.ExoskeletonItem;
import net.ltxprogrammer.changed.item.LaserReactiveItem;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.EntityUtil;
import net.ltxprogrammer.changed.util.ItemUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.Path;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class Exoskeleton extends AbstractRobot {
    private static final EntityDataAccessor<Integer> DATA_ID_ATTACK_TARGET = SynchedEntityData.defineId(Exoskeleton.class, EntityDataSerializers.INT);
    @Nullable
    private LivingEntity clientSideCachedAttackTarget;
    private int clientSideAttackTime;

    private ListTag savedEnchantments = new ListTag();

    public static Optional<Pair<ItemStack, ExoskeletonItem<?>>> getEntityExoskeleton(LivingEntity entity) {
        return AccessorySlots.getForEntity(entity)
                .flatMap(slots -> slots.getItem(ChangedAccessorySlots.FULL_BODY.get()))
                .map(stack -> {
                    if (stack.getItem() instanceof ExoskeletonItem<?> exo)
                        return Pair.of(stack, exo);
                    return null;
                });
    }

    public Exoskeleton(EntityType<? extends Exoskeleton> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    public ItemStack getDropItem() {
        final var stack = new ItemStack(ChangedItems.EXOSKELETON.get());
        stack.setDamageValue((int) ((1.0f - this.getCharge()) * stack.getMaxDamage()));
        if (this.hasCustomName())
            stack.setHoverName(this.getCustomName());
        stack.getOrCreateTag().put("Enchantments", savedEnchantments);
        return stack;
    }

    public void loadFromItemStack(ItemStack stack) {
        this.setCharge(1.0f - ((float) (stack.getDamageValue()) / (float) (stack.getMaxDamage())));
        if (stack.hasCustomHoverName())
            this.setCustomName(stack.getHoverName());
        savedEnchantments = stack.getEnchantmentTags();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.put("Enchantments", savedEnchantments);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        savedEnchantments = tag.getList("Enchantments", 10);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        if (this.isCharging() && pose == Pose.SLEEPING)
            return super.getDimensions(Pose.STANDING);
        return super.getDimensions(pose);
    }

    @Override
    public float getEyeHeight(Pose pose) {
        return this.isCharging() ? this.getSleepOffset() : super.getEyeHeight(pose);
    }

    protected float getSleepOffset() {
        return 2.0F / 16.0F;
    }

    protected boolean targetAlreadyHasExo(LivingEntity entity) {
        return AccessorySlots.isWearing(entity, stack -> stack.is(ChangedItems.EXOSKELETON.get()));
    }

    protected boolean targetIsBenignLatex(LivingEntity entity) {
        if (this.targetAlreadyHasExo(entity))
            return false;

        if (ProcessTransfur.getPlayerTransfurVariantSafe(EntityUtil.playerOrNull(entity)).map(TransfurVariantInstance::isTransfurring).orElse(false))
            return false;

        return EntityUtil.maybeGetOverlaying(entity).getType().is(ChangedTags.EntityTypes.BENIGN_LATEXES);
    }

    protected boolean targetHasBenignPants(LivingEntity entity) {
        if (this.targetAlreadyHasExo(entity))
            return false;

        return EntityUtil.maybeGetOverlaying(entity).getType().is(ChangedTags.EntityTypes.HUMANOIDS) &&
                AccessorySlots.isWearing(entity, stack -> stack.is(ChangedItems.BENIGN_SHORTS.get()));
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new SeekCharger(this, 0.45));
        this.goalSelector.addGoal(2, new ExoskeletonRestrainGoal(this, 0.4, false));
        this.goalSelector.addGoal(3, new ExoskeletonTransfurGoal(this, 0.4, false));
        this.goalSelector.addGoal(4, new ExoskeletonWanderGoal(this, 0.3, 120, false));

        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, this::targetIsBenignLatex));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, this::targetHasBenignPants));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_ATTACK_TARGET, 0);
    }

    public int getAttackDuration() {
        return 50;
    }

    void setActiveAttackTarget(LivingEntity entity) {
        this.entityData.set(DATA_ID_ATTACK_TARGET, entity.getId());
    }

    void setActiveAttackTarget(int entityId) {
        this.entityData.set(DATA_ID_ATTACK_TARGET, entityId);
    }

    public boolean hasActiveAttackTarget() {
        return this.entityData.get(DATA_ID_ATTACK_TARGET) != 0;
    }

    @Nullable
    public LivingEntity getActiveAttackTarget() {
        if (!this.hasActiveAttackTarget()) {
            return null;
        } else if (this.level().isClientSide) {
            if (this.clientSideCachedAttackTarget != null) {
                return this.clientSideCachedAttackTarget;
            } else {
                Entity entity = this.level().getEntity(this.entityData.get(DATA_ID_ATTACK_TARGET));
                if (entity instanceof LivingEntity) {
                    this.clientSideCachedAttackTarget = (LivingEntity)entity;
                    return this.clientSideCachedAttackTarget;
                } else {
                    return null;
                }
            }
        } else {
            return this.getTarget();
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 30.0f)
                .add(Attributes.ARMOR, 20.0f)
                .add(Attributes.MOVEMENT_SPEED, 0.65D);
    }

    @Override
    protected void playStepSound(BlockPos blockPos, BlockState state) {
        this.playSound(ChangedSounds.EXOSKELETON_STEP.get(), 0.3F, 0.75f + this.random.nextFloat() * 0.2f);
    }

    @Override
    public boolean isAffectedByWater() {
        return true;
    }

    @Override
    public SoundEvent getRunningSound() {
        return null;
    }

    @Override
    public int getRunningSoundDuration() {
        return 100;
    }

    @Override
    public float getMaxDamage() {
        return 40f;
    }

    @Override
    public ChargerType getChargerType() {
        return ChargerType.EXOSKELETON;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.getTarget() == player || this.isLowBattery())
            return super.mobInteract(player, hand);

        boolean moved = AccessorySlots.getForEntity(player).map(slots ->
                slots.moveToSlot(ChangedAccessorySlots.FULL_BODY.get(), this.getDropItem())).orElse(false);
        if (moved) {
            if (!this.level().isClientSide)
                this.discard();
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    @Override
    protected float getChargeDecay() {
        return super.getChargeDecay();
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isCharging() && this.getSleepingPos().isPresent()) {
            final var panel = level().getBlockState(this.getSleepingPos().get());
            if (panel.getBlock() instanceof AbstractLargePanel) {
                final var section = panel.getValue(AbstractLargePanel.SECTION);
                final var facing = panel.getValue(AbstractLargePanel.FACING);
                final var entityPos = section.getRelative(this.getSleepingPos().get(), facing, NineSection.BOTTOM_MIDDLE);

                this.setPos(entityPos.getX() + 0.5D, entityPos.getY(), entityPos.getZ() + 0.5D);
                this.setOldPosAndRot();
            } else {
                if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                    this.spawnAtLocation(this.getDropItem());
                }

                this.discard();
            }
        } else if (this.isCharging()) {
            if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                this.spawnAtLocation(this.getDropItem());
            }

            this.discard();
        }
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.hasActiveAttackTarget()) {
            if (this.clientSideAttackTime < this.getAttackDuration()) {
                ++this.clientSideAttackTime;
            }

        }
    }

    @Override
    public boolean isPushable() {
        return super.isPushable() && !this.isCharging();
    }

    @Override
    public boolean isNoGravity() {
        return super.isNoGravity() || this.isCharging();
    }

    @Override
    public void setCharging(boolean value) {
        super.setCharging(value);

        this.setPose(value ? Pose.SLEEPING : Pose.STANDING);
        this.refreshDimensions();
    }

    public float getAttackAnimationScale(float partialTicks) {
        return ((float)this.clientSideAttackTime + partialTicks) / (float)this.getAttackDuration();
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (DATA_ID_CHARGING.equals(accessor)) {
            this.refreshDimensions();
        }

        if (DATA_ID_ATTACK_TARGET.equals(accessor)) {
            this.clientSideAttackTime = 0;
            this.clientSideCachedAttackTarget = null;
        }
    }

    public static class ExoskeletonRestrainGoal extends MeleeAttackGoal {
        protected final Exoskeleton exoskeleton;

        public ExoskeletonRestrainGoal(Exoskeleton exoskeleton, double speedModifier, boolean visualPersistence) {
            super(exoskeleton, speedModifier, visualPersistence);

            this.exoskeleton = exoskeleton;
        }

        @Override
        protected double getAttackReachSqr(LivingEntity target) {
            if (exoskeleton.targetHasBenignPants(target))
                return super.getAttackReachSqr(target) * 1.5;
            else return super.getAttackReachSqr(target) * 0.75;
        }

        @Override
        protected void checkAndPerformAttack(LivingEntity target, double distanceSquared) {
            if (exoskeleton.targetIsBenignLatex(target)) {
                if (ProcessTransfur.getPlayerTransfurVariantSafe(EntityUtil.playerOrNull(target)).map(TransfurVariantInstance::isTransfurring).orElse(false))
                    return; // Wait for player to TF

                double reachSqr = this.getAttackReachSqr(target) * 0.9;

                if (distanceSquared <= reachSqr && this.getTicksUntilNextAttack() <= 0) {
                    this.resetAttackCooldown();

                    if (AccessorySlots.tryReplaceSlot(target, ChangedAccessorySlots.FULL_BODY.get(), exoskeleton.getDropItem()))
                        exoskeleton.discard();
                    else
                        exoskeleton.setTarget(null);
                }
            }

            else {
                // Re-evaluate nearby entities
                exoskeleton.setTarget(null);
            }
        }

        @Override
        public void start() {
            exoskeleton.detachFromCharger();
            super.start();

            exoskeleton.playSound(ChangedSounds.EXOSKELETON_CHIME.get(), 0.3F, 1f);
        }

        @Override
        public void stop() {
            super.stop();
            exoskeleton.setActiveAttackTarget(0);
        }

        @Override
        public boolean canUse() {
            LivingEntity target = this.exoskeleton.getTarget();
            return super.canUse() && (!exoskeleton.isCharging() || exoskeleton.isSufficientlyCharged()) && !exoskeleton.isLowBattery()
                    && target != null && exoskeleton.targetIsBenignLatex(target);
        }

        @Override
        public boolean canContinueToUse() {
            LivingEntity target = this.exoskeleton.getTarget();
            return super.canContinueToUse() && !exoskeleton.isCharging() && !exoskeleton.isLowBattery()
                    && target != null && exoskeleton.targetIsBenignLatex(target);
        }
    }

    public static class ExoskeletonTransfurGoal extends Goal {
        protected final Exoskeleton exoskeleton;
        protected final double speedModifier;
        protected final boolean requireVisualPersistence;
        private int attackTime;
        private int ticksUntilNextPathRecalculation;
        private Path path;
        private double pathedTargetX;
        private double pathedTargetY;
        private double pathedTargetZ;

        public ExoskeletonTransfurGoal(Exoskeleton exoskeleton, double speedModifier, boolean visualPersistence) {
            this.exoskeleton = exoskeleton;
            this.speedModifier = speedModifier;
            this.requireVisualPersistence = visualPersistence;
        }

        public boolean canUse() {
            LivingEntity livingentity = this.exoskeleton.getTarget();
            return livingentity != null && livingentity.isAlive() && exoskeleton.targetHasBenignPants(livingentity)
                    && (!exoskeleton.isCharging() || exoskeleton.isSufficientlyCharged()) && !exoskeleton.isLowBattery();
        }

        public boolean canContinueToUse() {
            return super.canContinueToUse() && this.exoskeleton.getTarget() != null && exoskeleton.targetHasBenignPants(this.exoskeleton.getTarget())
                    && !exoskeleton.isCharging() && !exoskeleton.isLowBattery();
        }

        public void start() {
            this.attackTime = -10;
            this.ticksUntilNextPathRecalculation = 0;
            this.exoskeleton.getNavigation().stop();
            this.exoskeleton.hasImpulse = true;
        }

        public void stop() {
            this.exoskeleton.setActiveAttackTarget(0);
            this.exoskeleton.setTarget((LivingEntity)null);
            this.exoskeleton.getNavigation().stop();
        }

        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity target = this.exoskeleton.getTarget();
            if (target != null) {
                this.exoskeleton.getLookControl().setLookAt(target, 30.0F, 30.0F);
                double d0 = this.exoskeleton.distanceToSqr(target.getX(), target.getY(), target.getZ());
                this.ticksUntilNextPathRecalculation = Math.max(this.ticksUntilNextPathRecalculation - 1, 0);
                if ((this.requireVisualPersistence || this.exoskeleton.getSensing().hasLineOfSight(target)) && this.ticksUntilNextPathRecalculation <= 0 && (this.pathedTargetX == 0.0D && this.pathedTargetY == 0.0D && this.pathedTargetZ == 0.0D || target.distanceToSqr(this.pathedTargetX, this.pathedTargetY, this.pathedTargetZ) >= 1.0D || this.exoskeleton.getRandom().nextFloat() < 0.05F)) {
                    this.pathedTargetX = target.getX();
                    this.pathedTargetY = target.getY();
                    this.pathedTargetZ = target.getZ();
                    this.ticksUntilNextPathRecalculation = 4 + this.exoskeleton.getRandom().nextInt(7);
                    if (d0 > 1024.0D) {
                        this.ticksUntilNextPathRecalculation += 10;
                    } else if (d0 > 256.0D) {
                        this.ticksUntilNextPathRecalculation += 5;
                    }

                    if (!this.exoskeleton.getNavigation().moveTo(target, this.speedModifier *
                            (this.attackTime > 0 ? 0.8 : 1.0))) {
                        this.ticksUntilNextPathRecalculation += 15;
                    }

                    this.ticksUntilNextPathRecalculation = this.adjustedTickDelay(this.ticksUntilNextPathRecalculation);
                }

                if (this.exoskeleton.getSensing().hasLineOfSight(target) && exoskeleton.distanceToSqr(target) < 25.0) { // ~5 blocks
                    if (exoskeleton.distanceToSqr(target) < 4.0) {
                        this.exoskeleton.getNavigation().stop();
                        this.ticksUntilNextPathRecalculation = 2;
                    }

                    ++this.attackTime;
                    if (this.attackTime == 0) {
                        this.exoskeleton.setActiveAttackTarget(target.getId());
                        if (!this.exoskeleton.isSilent()) {
                            ChangedSounds.broadcastSound(exoskeleton, ChangedSounds.LASER_FIRE, 1, 1);
                        }
                    } else if (this.attackTime >= this.exoskeleton.getAttackDuration()) {
                        float amount = ProcessTransfur.difficultyAdjustTransfurAmount(exoskeleton.level().getDifficulty(), 11.0f);

                        AtomicBoolean playedSound = new AtomicBoolean(false);
                        ItemUtil.getWearingItems(target, itemStack -> itemStack.getItem() instanceof LaserReactiveItem).forEach(slottedItem -> {
                            if (!((LaserReactiveItem)slottedItem.itemStack().getItem()).tickLaserExposure(target, slottedItem.itemStack(), amount) && !playedSound.getAndSet(true))
                                ChangedSounds.broadcastSound(target, ChangedSounds.TRANSFUR_HURT, 1, 1);
                        });

                        exoskeleton.setTarget(null);
                    }
                } else {
                    this.attackTime = -10;
                    this.exoskeleton.setActiveAttackTarget(0);
                }
            }
        }
    }

    public static class ExoskeletonWanderGoal extends RandomStrollGoal {
        protected final Exoskeleton exoskeleton;

        public ExoskeletonWanderGoal(Exoskeleton exoskeleton, double speedModifier, int interval, boolean checkNoAction) {
            super(exoskeleton, speedModifier, interval, checkNoAction);

            this.exoskeleton = exoskeleton;
        }

        @Override
        public boolean canUse() {
            return super.canUse() && !exoskeleton.isCharging() && !exoskeleton.isLowBattery();
        }

        @Override
        public boolean canContinueToUse() {
            return super.canContinueToUse() && !exoskeleton.isCharging() && !exoskeleton.isLowBattery();
        }
    }
}
