package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.ai.DudNavigator;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DarkLatexPup extends AbstractDarkLatexEntity {
    protected static final int MAX_AGE = 72000;
    protected int age = 0;
    protected int ticksLeftAsPuddle = 0;
    private static final EntityDataAccessor<Boolean> DATA_PUDDLE_ID = SynchedEntityData.defineId(DarkLatexPup.class, EntityDataSerializers.BOOLEAN);

    private final PathNavigation goodNavigator;
    private final DudNavigator dudNavigator;

    public DarkLatexPup(EntityType<? extends DarkLatexPup> type, Level level) {
        super(type, level);
        this.goodNavigator = this.navigation;
        this.dudNavigator = new DudNavigator(this, level);
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            if (!this.isPuddle())
                this.playSound(ChangedSounds.POISON, 1.0f, 1.0f);
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
        this.entityData.set(DATA_PUDDLE_ID, isPuddle);
    }

    public boolean isPuddle() {
        return this.entityData.get(DATA_PUDDLE_ID);
    }

    @Override
    public void tick() {
        super.tick();
        if (ticksLeftAsPuddle > 0) {
            if (this.navigation != this.dudNavigator) {
                this.navigation.stop();
                this.navigation = this.dudNavigator;
            }
            ticksLeftAsPuddle--;
            if (ticksLeftAsPuddle <= 0)
                setPuddle(false);
        } else if (this.navigation == this.dudNavigator) {
            this.navigation = this.goodNavigator;
            if (this.getTarget() != null)
                this.navigation.moveTo(this.getTarget(), 1);
        }
    }

    @Override
    public Color3 getDripColor() {
        return Color3.DARK;
    }

    @Override
    public TransfurMode getTransfurMode() {
        return TransfurMode.NONE;
    }

    @Override
    public HairStyle getDefaultHairStyle() {
        return HairStyle.BALD.get();
    }

    @Override
    public @Nullable List<HairStyle> getValidHairStyles() {
        return List.of();
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
    public float getEyeHeightMul() {
        if (this.isCrouching())
            return 0.55F;
        else
            return 0.8F;
    }

    @Override
    public void visualTick(Level level) {
        super.visualTick(level);

        age++;

        var underlyingPlayer = getUnderlyingPlayer();
        if (ProcessTransfur.ifPlayerLatex(underlyingPlayer, variant -> {
            if (variant.ageAsVariant > MAX_AGE || age > MAX_AGE) {
                var newVariant = LatexVariant.DARK_LATEX_WOLF.randomGender(level.random);
                ProcessTransfur.setPlayerLatexVariant(underlyingPlayer, newVariant);
                ChangedSounds.broadcastSound(this, newVariant.sound, 1.0f, 1.0f);
                underlyingPlayer.heal(12.0f);
            }
        })) return;

        if (age > MAX_AGE) {
            var newVariant = LatexVariant.DARK_LATEX_WOLF.randomGender(level.random);
            var wolf = newVariant.getEntityType().create(level);
            if (wolf != null) {
                wolf.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
                level.addFreshEntity(wolf);
                ChangedSounds.broadcastSound(this, newVariant.sound, 1.0f, 1.0f);
                applyCustomizeToAged((AbstractDarkLatexEntity)wolf);
            }
            this.discard();
        }
    }

    protected void applyCustomizeToAged(AbstractDarkLatexEntity aged) {
        aged.setOwnerUUID(this.getOwnerUUID());
        aged.setCustomName(this.getCustomName());
        aged.setUnderlyingPlayer(this.getUnderlyingPlayer());
    }

    public boolean canBeLeashed(Player player) {
        return !this.isLeashed();
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (this.level.isClientSide) {
            boolean flag = this.isOwnedBy(player) || this.isTame() || this.isTameItem(itemstack) && !this.isTame();
            return flag ? InteractionResult.CONSUME : InteractionResult.PASS;
        } else {
            if (!this.isTame() && this.isTameItem(itemstack)) {
                if (!player.getAbilities().instabuild) {
                    itemstack.shrink(1);
                }

                if (this.random.nextInt(3) == 0) {
                    this.tame(player);
                    this.navigation.stop();
                    this.setTarget(null);
                    this.level.broadcastEntityEvent(this, (byte)7);
                } else {
                    this.level.broadcastEntityEvent(this, (byte)6);
                }

                return InteractionResult.SUCCESS;
            }

            return super.mobInteract(player, hand);
        }
    }
}
