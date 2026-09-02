package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.ability.IAbstractChangedEntity;
import net.ltxprogrammer.changed.entity.*;
import net.ltxprogrammer.changed.entity.ai.TamedEntityFavor;
import net.ltxprogrammer.changed.entity.variant.EntityShape;
import net.ltxprogrammer.changed.init.*;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.NotNull;

public class DarkLatexWolfPup extends AbstractDarkLatexEntity {
    protected static final int MAX_AGE = 72000;
    protected int age = 0;
    protected int ticksLeftAsPuddle = 0;
    private static final EntityDataAccessor<Boolean> DATA_PUDDLE_ID = SynchedEntityData.defineId(DarkLatexWolfPup.class, EntityDataSerializers.BOOLEAN);

    public DarkLatexWolfPup(EntityType<? extends DarkLatexWolfPup> type, Level level) {
        super(type, level);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        final DarkLatexWolfPup self = this;
        return new GroundPathNavigation(this, level) {
            @Override
            protected boolean canUpdatePath() {
                return super.canUpdatePath() && !self.isPuddle();
            }
        };
    }

    @Override
    public boolean tryTransfurTarget(Entity entity) {
        return false;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            if (!this.isPuddle())
                this.playSound(ChangedSounds.DARK_LATEX_PUP_FORM_PUDDLE.get(), 1.0f, 1.0f);
            this.setPuddle(true);
            ticksLeftAsPuddle = 120;
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2, false, false, false)); // Slowness 2 for 5 seconds
        }
        return true;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_PUDDLE_ID, false);
    }

    public void setPuddle(boolean isPuddle) {
        this.goalSelector.setControlFlag(Goal.Flag.MOVE, !isPuddle);
        this.entityData.set(DATA_PUDDLE_ID, isPuddle);
    }

    public boolean isPuddle() {
        return this.entityData.get(DATA_PUDDLE_ID);
    }

    @Override
    public void tick() {
        super.tick();
        if (ticksLeftAsPuddle > 0) {
            this.navigation.stop();
            this.setDeltaMovement(0, Math.min(this.getDeltaMovement().y, 0), 0);
            ticksLeftAsPuddle--;
            if (ticksLeftAsPuddle <= 0)
                setPuddle(false);
        }
        this.refreshDimensions();
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.NONE;
    }

    @Override
    public Color3 getTransfurColor(TransfurCause cause) {
        return Color3.DARK;
    }

    @Override
    public void setSharedFlag(int p_20116_, boolean p_20117_) {
        super.setSharedFlag(p_20116_, p_20117_);
    }

    @Override
    public double getMyRidingOffset() {
        return 0.2;
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        age = tag.getInt("age");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("age", age);
    }

    @Override
    protected float getEyeHeightMul(Pose pose) {
        if (pose == Pose.CROUCHING)
            return 0.65F;
        if (this.isPuddle())
            return 0.9F;
        else
            return 0.8F;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        EntityDimensions core = super.getDimensions(pose);
        if (this.isPuddle())
            return EntityDimensions.scalable(core.width + 0.4f, core.height - 0.5f);
        else
            return core;
    }

    @Override
    public void variantTick(Level level) {
        super.variantTick(level);

        age++;

        final int checkAge = ProcessTransfur.ifPlayerTransfurred(getUnderlyingPlayer(), variant -> variant.ageAsVariant, () -> age);
        if (!level.isClientSide && checkAge > MAX_AGE) {
            IAbstractChangedEntity conversionEntity = IAbstractChangedEntity.forEntity(this);
            var newVariant = ChangedTransfurVariants.Gendered.DARK_LATEX_WOLVES.getRandomVariant(level().random);

            conversionEntity.replaceVariant(newVariant);
            ChangedSounds.broadcastSound(conversionEntity.getEntity(), newVariant.sound, 1.0f, 1.0f);
            conversionEntity.getEntity().heal(12.0f);
        }
    }

    public boolean canBeLeashed(Player player) {
        return !this.isLeashed();
    }

    @Override
    protected boolean isTamableBy(Player player) {
        return true;
    }

    @Override
    protected float getTameChance(Player player, ItemStack itemStack) {
        var variant = ProcessTransfur.getPlayerTransfurVariant(player);
        if (variant == null)
            return 0.1f; // 10%
        if (ChangedLatexTypes.DARK_LATEX.get().isFriendlyTo(variant.getLatexType()))
            return 0.3333f; // 33%
        if (!ChangedLatexTypes.DARK_LATEX.get().isHostileTo(variant.getLatexType()))
            return 0.1f; // 10%
        return 0.0f; // No chance
    }

    /*@Override
    protected InteractionResult tamedInteract(Player player, InteractionHand hand) {
        boolean shouldFollow = !this.isFollowingOwner();
        this.setFollowOwner(shouldFollow);

        player.displayClientMessage(Component.translatable(shouldFollow ? "text.changed.tamed.follow" : "text.changed.tamed.wander", this.getDisplayName()), true);
        this.jumping = false;
        this.navigation.stop();
        this.setTarget((LivingEntity) null);
        return InteractionResult.SUCCESS;
    }*/

    @Override
    public boolean canDoFavor(TamedEntityFavor favor) {
        return favor == ChangedTamedEntityFavors.NONE.get();
    }

    @Override
    protected void setAttributes(AttributeMap attributes) {
        super.setAttributes(attributes);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.25);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.975);
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(12.0);
        attributes.getInstance(ChangedAttributes.FALL_RESISTANCE.get()).setBaseValue(2.5);
        attributes.getInstance(ChangedAttributes.MINING_SPEED.get()).setBaseValue(AttributePresets.MINING_FATIGUE_1);
    }

    @Override
    public @NotNull EntityShape getEntityShape() {
        return EntityShape.FERAL;
    }

    @Override
    public boolean isItemAllowedInSlot(ItemStack stack, EquipmentSlot slot) {
        if (slot.getType() == EquipmentSlot.Type.ARMOR)
            return false;
        return super.isItemAllowedInSlot(stack, slot);
    }
}
