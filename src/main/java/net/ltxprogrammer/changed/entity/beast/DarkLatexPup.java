package net.ltxprogrammer.changed.entity.beast;

import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedCriteriaTriggers;
import net.ltxprogrammer.changed.init.ChangedItems;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.scores.Team;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DarkLatexPup extends AbstractDarkLatexEntity {
    protected static final int MAX_AGE = 72000;
    protected int age = 0;

    public DarkLatexPup(EntityType<? extends DarkLatexPup> type, Level level) {
        super(type, level);
    }

    @Override
    public ChangedParticles.Color3 getDripColor() {
        return ChangedParticles.Color3.DARK;
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

    @Override
    protected boolean targetSelectorTest(LivingEntity livingEntity) {
        return false; // TODO remove when pup puddle is implemented.
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
