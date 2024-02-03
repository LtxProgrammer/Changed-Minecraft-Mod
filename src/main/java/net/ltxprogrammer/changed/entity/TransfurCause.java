package net.ltxprogrammer.changed.entity;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.util.Mth;

public enum TransfurCause {
    ATTACK_REPLICATE_LEFT(TransfurCause::thirdLimb, TransfurCause::secondLimb, TransfurCause::firstLimb, TransfurCause::thirdLimb, TransfurCause::thirdLimb, TransfurCause::thirdLimb),
    ATTACK_REPLICATE_RIGHT(TransfurCause::thirdLimb, TransfurCause::secondLimb, TransfurCause::thirdLimb, TransfurCause::firstLimb, TransfurCause::thirdLimb, TransfurCause::thirdLimb),
    GRAB_REPLICATE(TransfurCause::secondLimb, TransfurCause::firstLimb, TransfurCause::secondLimb, TransfurCause::secondLimb, TransfurCause::secondLimb, TransfurCause::secondLimb),
    FOOT_HAZARD_LEFT(TransfurCause::thirdLimb, TransfurCause::secondLimb, TransfurCause::thirdLimb, TransfurCause::thirdLimb, TransfurCause::firstLimb, TransfurCause::thirdLimb),
    FOOT_HAZARD_RIGHT(TransfurCause::thirdLimb, TransfurCause::secondLimb, TransfurCause::thirdLimb, TransfurCause::thirdLimb, TransfurCause::thirdLimb, TransfurCause::firstLimb),
    WALL_HAZARD_LEFT(TransfurCause::thirdLimb, TransfurCause::secondLimb, TransfurCause::firstLimb, TransfurCause::thirdLimb, TransfurCause::thirdLimb, TransfurCause::thirdLimb),
    WALL_HAZARD_RIGHT(TransfurCause::thirdLimb, TransfurCause::secondLimb, TransfurCause::thirdLimb, TransfurCause::firstLimb, TransfurCause::thirdLimb, TransfurCause::thirdLimb),
    CEILING_HAZARD(TransfurCause::firstLimb, TransfurCause::secondLimb, TransfurCause::thirdLimb, TransfurCause::thirdLimb, TransfurCause::thirdLimb, TransfurCause::thirdLimb),
    IV_RACK(TransfurCause::thirdLimb, TransfurCause::secondLimb, TransfurCause::firstLimb, TransfurCause::thirdLimb, TransfurCause::thirdLimb, TransfurCause::thirdLimb);

    private static float firstLimb(float totalProgress) {
        return Mth.clamp(Mth.map(totalProgress, 0.0f, 0.33333f, 0.0f, 1.0f), 0.0f, 1.0f);
    }

    private static float secondLimb(float totalProgress) {
        return Mth.clamp(Mth.map(totalProgress, 0.33333f, 0.66667f, 0.0f, 1.0f), 0.0f, 1.0f);
    }

    private static float thirdLimb(float totalProgress) {
        return Mth.clamp(Mth.map(totalProgress, 0.66667f, 1.0f, 0.0f, 1.0f), 0.0f, 1.0f);
    }

    private final Float2FloatFunction head, torso, leftArm, rightArm, leftLeg, rightLeg;

    TransfurCause(Float2FloatFunction head, Float2FloatFunction torso, Float2FloatFunction leftArm, Float2FloatFunction rightArm, Float2FloatFunction leftLeg, Float2FloatFunction rightLeg) {
        this.head = head;
        this.torso = torso;
        this.leftArm = leftArm;
        this.rightArm = rightArm;
        this.leftLeg = leftLeg;
        this.rightLeg = rightLeg;
    }

    public float getHeadProgress(float totalProgress) {
        return head.get(totalProgress);
    }

    public float getTorsoProgress(float totalProgress) {
        return torso.get(totalProgress);
    }

    public float getLeftArmProgress(float totalProgress) {
        return leftArm.get(totalProgress);
    }

    public float getRightArmProgress(float totalProgress) {
        return rightArm.get(totalProgress);
    }

    public float getLeftLegProgress(float totalProgress) {
        return leftLeg.get(totalProgress);
    }

    public float getRightLegProgress(float totalProgress) {
        return rightLeg.get(totalProgress);
    }
}
