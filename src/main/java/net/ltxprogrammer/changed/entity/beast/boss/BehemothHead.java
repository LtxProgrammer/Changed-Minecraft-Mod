package net.ltxprogrammer.changed.entity.beast.boss;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.init.ChangedEntities;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeMod;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class BehemothHead extends Behemoth {
    private final ServerBossEvent bossEvent = (ServerBossEvent)(new ServerBossEvent(this.getDisplayName(), BossEvent.BossBarColor.WHITE, BossEvent.BossBarOverlay.PROGRESS));

    public BehemothHandLeft leftHand;
    public BehemothHandRight rightHand;

    // If the UUID is null, create hand, if the UUID is equal the head UUID, don't find hand, else find hand
    private UUID loadedLeftHand = null;
    private UUID loadedRightHand = null;

    public BehemothHead(EntityType<? extends BehemothHead> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
        this.xpReward = 50;
    }
    @Override
    public ChangedParticles.Color3 getHairColor(int layer) {
        return ChangedParticles.Color3.WHITE;
    }

    public @Nullable List<HairStyle> getValidHairStyles() {
        return HairStyle.Collection.EMPTY;
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

        if (tickCount > 8)
            return;

        AABB checkBB = new AABB(this.blockPosition()).inflate(16);
        if (leftHand == null) {
            if (loadedLeftHand == null) {
                leftHand = ChangedEntities.BEHEMOTH_HAND_LEFT.get().create(level);
                leftHand.moveTo(this.position());
                leftHand.setHead(this);
                level.addFreshEntity(leftHand);
                loadedLeftHand = this.getUUID();
            }

            else if (!loadedLeftHand.equals(this.getUUID())) {
                var list = level.getEntitiesOfClass(BehemothHandLeft.class, checkBB,
                        foundEntity -> foundEntity.handUUID.equals(loadedLeftHand));
                if (!list.isEmpty()) {
                    leftHand = list.get(0);
                    leftHand.setHead(this);
                }
            }
        }

        if (rightHand == null) {
            if (loadedRightHand == null) {
                rightHand = ChangedEntities.BEHEMOTH_HAND_RIGHT.get().create(level);
                rightHand.moveTo(this.position());
                rightHand.setHead(this);
                level.addFreshEntity(rightHand);
                loadedRightHand = this.getUUID();
            }

            else if (!loadedRightHand.equals(this.getUUID())) {
                var list = level.getEntitiesOfClass(BehemothHandRight.class, checkBB,
                        foundEntity -> foundEntity.handUUID.equals(loadedRightHand));
                if (!list.isEmpty()) {
                    rightHand = list.get(0);
                    rightHand.setHead(this);
                }
            }
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        this.bossEvent.removeAllPlayers();
        if (leftHand != null)
            leftHand.remove(reason);
        leftHand = null;
        if (rightHand != null)
            rightHand.remove(reason);
        rightHand = null;
    }

    private static final String LEFT_HAND_ID = Changed.modResourceStr("leftHandUUID");
    private static final String RIGHT_HAND_ID = Changed.modResourceStr("rightHandUUID");
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (leftHand != null)
            tag.putUUID(LEFT_HAND_ID, leftHand.handUUID);
        else
            tag.putUUID(LEFT_HAND_ID, this.getUUID());
        if (rightHand != null)
            tag.putUUID(RIGHT_HAND_ID, rightHand.handUUID);
        else
            tag.putUUID(RIGHT_HAND_ID, this.getUUID());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        if (tag.contains(LEFT_HAND_ID))
            loadedLeftHand = tag.getUUID(LEFT_HAND_ID);
        if (tag.contains(RIGHT_HAND_ID))
            loadedRightHand = tag.getUUID(RIGHT_HAND_ID);
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
    }

    protected void customServerAiStep() {
        this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
    }

    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }
}
