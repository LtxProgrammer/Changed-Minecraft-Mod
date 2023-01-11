package net.ltxprogrammer.changed.entity.beast.boss;

import net.ltxprogrammer.changed.init.ChangedSounds;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

public abstract class BehemothHand extends Behemoth {
    public BehemothHead head;

    public BehemothHand(EntityType<? extends BehemothHand> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    public BehemothHand setHead(BehemothHead head) {
        this.head = head;
        return this;
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        head = null;
    }

    @Override
    public void tick() {
        super.tick();
        if (tickCount > 10 && (head == null || head.isDeadOrDying()))
            this.discard();
    }

    protected void setAttributes(AttributeMap attributes) {
        attributes.getInstance(Attributes.MAX_HEALTH).setBaseValue(100.0);
        attributes.getInstance(Attributes.FOLLOW_RANGE).setBaseValue(24.0);
        attributes.getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(1.0);
        attributes.getInstance(ForgeMod.SWIM_SPEED.get()).setBaseValue(1.0);
        attributes.getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(3.0);
        attributes.getInstance(Attributes.ARMOR).setBaseValue(2.0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(0, new StayByHeadGoal(this));
    }

    public static class StayByHeadGoal extends Goal {
        final BehemothHand hand;
        static final float MAX_DISTANCE_ALLOWED_SQR = 12 * 12;
        static final int RETURN_LENGTH = 5 * 20;
        int ticksLeft = 0;

        public StayByHeadGoal(BehemothHand hand) {
            this.hand = hand;
        }

        @Override
        public boolean canUse() {
            if (hand.head == null)
                return false;
            return hand.distanceToSqr(hand.head) > MAX_DISTANCE_ALLOWED_SQR;
        }

        @Override
        public boolean canContinueToUse() {
            return ticksLeft > 0;
        }

        @Override
        public void start() {
            ChangedSounds.broadcastSound(hand, ChangedSounds.POISON, 1.0f, 1.0f);
            hand.setInvisible(true);
            hand.setInvulnerable(true);
            hand.noPhysics = true;
            ticksLeft = RETURN_LENGTH;

            if (hand.head != null) {
                hand.moveTo(hand.head.position());
            }
        }

        @Override
        public void tick() {
            --ticksLeft;
            hand.setDeltaMovement(0, 0, 0);

            if (hand.head != null) {
                hand.moveTo(hand.head.position());
            }
        }

        @Override
        public void stop() {
            ChangedSounds.broadcastSound(hand, ChangedSounds.POISON, 1.0f, 1.0f);
            hand.setInvisible(false);
            hand.setInvulnerable(false);
            hand.noPhysics = false;
            hand.fallDistance = 0.0f;

            if (hand.head != null) {
                hand.moveTo(hand.head.position().add(0.0, 1.2, 0.0));
            }
        }
    }
}
