package net.ltxprogrammer.changed.entity.beast.boss;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.TransfurMode;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

public class BehemothHead extends Behemoth {
    private BehemothHandLeft leftHand;
    private BehemothHandRight rightHand;

    public BehemothHead(EntityType<? extends BehemothHead> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.xpReward = 50;
    }

    protected void setAttributes(AttributeMap attributes) {
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(200.0);
        attributes.getInstance(Attributes.FOLLOW_RANGE).setBaseValue(24.0);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(0.0);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(0.0);
        attributes.getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(3.0);
        attributes.getInstance(Attributes.ARMOR).setBaseValue(2.0);
    }

    @Override
    public void knockback(double p_147241_, double p_147242_, double p_147243_) {}

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        this.setDeltaMovement(this.getDeltaMovement().multiply(0, 1, 0));
    }

    private void addHands() {
        if (leftHand == null) {
            leftHand = ChangedEntities.BEHEMOTH_HAND_LEFT.get().create(level);
            leftHand.moveTo(this.position());
            leftHand.setHead(this);
            level.addFreshEntity(leftHand);
        }

        if (rightHand == null) {
            rightHand = ChangedEntities.BEHEMOTH_HAND_RIGHT.get().create(level);
            rightHand.moveTo(this.position());
            rightHand.setHead(this);
            level.addFreshEntity(rightHand);
        }
    }
    public void onAddedToWorld() {
        super.onAddedToWorld();
        addHands();
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        if (leftHand != null)
            leftHand.remove(reason);
        leftHand = null;
        if (rightHand != null)
            rightHand.remove(reason);
        rightHand = null;
    }

    private static final String LEFT_HAND_HEALTH = "leftHandHealth";
    private static final String RIGHT_HAND_HEALTH = "rightHandHealth";
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (leftHand != null)
            tag.putFloat(LEFT_HAND_HEALTH, leftHand.getHealth());
        if (rightHand != null)
            tag.putFloat(RIGHT_HAND_HEALTH, rightHand.getHealth());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        addHands();
        if (leftHand != null) {
            if (tag.contains(LEFT_HAND_HEALTH))
                leftHand.setHealth(tag.getFloat(LEFT_HAND_HEALTH));
            else {
                leftHand.discard();
                leftHand = null;
            }
        }
        if (rightHand != null) {
            if (tag.contains(RIGHT_HAND_HEALTH))
                rightHand.setHealth(tag.getFloat(RIGHT_HAND_HEALTH));
            else {
                rightHand.discard();
                rightHand = null;
            }
        }
    }
}
