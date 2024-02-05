package net.ltxprogrammer.changed.entity;

import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.minecraft.util.Mth;

import javax.annotation.Nullable;
import java.util.Optional;

public enum TransfurCause {
    ATTACK_REPLICATE_LEFT(
            LimbCoverTransition.COVER_START, TransfurCause::secondHalfLimb, // HEAD
            LimbCoverTransition.COVER_FROM_LEFT_ARM, TransfurCause::secondLimb, // TORSO
            LimbCoverTransition.COVER_ATTACK, TransfurCause::firstLimb, // LEFT ARM
            LimbCoverTransition.COVER_START, TransfurCause::secondHalfLimb, // RIGHT ARM
            LimbCoverTransition.COVER_START, TransfurCause::thirdLimb, // LEFT LEG
            LimbCoverTransition.COVER_START, TransfurCause::thirdLimb), // RIGHT LEG
    ATTACK_REPLICATE_RIGHT(
            LimbCoverTransition.COVER_START, TransfurCause::thirdLimb, // HEAD
            LimbCoverTransition.COVER_FROM_LEFT_ARM, TransfurCause::secondLimb, // TORSO
            LimbCoverTransition.COVER_START, TransfurCause::thirdLimb, // LEFT ARM
            LimbCoverTransition.COVER_ATTACK, TransfurCause::firstLimb, // RIGHT ARM
            LimbCoverTransition.COVER_START, TransfurCause::thirdLimb, // LEFT LEG
            LimbCoverTransition.COVER_START, TransfurCause::thirdLimb), // RIGHT LEG
    GRAB_REPLICATE(
            LimbCoverTransition.COVER_START, TransfurCause::secondLimb, // HEAD
            LimbCoverTransition.COVER_ATTACK, TransfurCause::firstLimb, // TORSO
            LimbCoverTransition.COVER_START, TransfurCause::secondLimb, // LEFT ARM
            LimbCoverTransition.COVER_START, TransfurCause::secondLimb, // RIGHT ARM
            LimbCoverTransition.COVER_START, TransfurCause::secondLimb, // LEFT LEG
            LimbCoverTransition.COVER_START, TransfurCause::secondLimb), // RIGHT LEG
    FOOT_HAZARD_LEFT(
            LimbCoverTransition.COVER_START, TransfurCause::thirdLimb, // HEAD
            LimbCoverTransition.COVER_FROM_LEFT_LEG, TransfurCause::secondLimb, // TORSO
            LimbCoverTransition.COVER_START, TransfurCause::thirdLimb, // LEFT ARM
            LimbCoverTransition.COVER_START, TransfurCause::thirdLimb, // RIGHT ARM
            LimbCoverTransition.COVER_END, TransfurCause::firstLimb, // LEFT LEG
            LimbCoverTransition.COVER_START, TransfurCause::secondHalfLimb), // RIGHT LEG
    FOOT_HAZARD_RIGHT(
            LimbCoverTransition.COVER_START, TransfurCause::thirdLimb, // HEAD
            LimbCoverTransition.COVER_FROM_RIGHT_LEG, TransfurCause::secondLimb, // TORSO
            LimbCoverTransition.COVER_START, TransfurCause::thirdLimb, // LEFT ARM
            LimbCoverTransition.COVER_START, TransfurCause::thirdLimb, // RIGHT ARM
            LimbCoverTransition.COVER_START, TransfurCause::secondHalfLimb, // LEFT LEG
            LimbCoverTransition.COVER_END, TransfurCause::firstLimb), // RIGHT LEG
    WALL_HAZARD_LEFT(
            LimbCoverTransition.COVER_START, TransfurCause::secondHalfLimb, // HEAD
            LimbCoverTransition.COVER_FROM_LEFT_ARM, TransfurCause::secondLimb, // TORSO
            LimbCoverTransition.COVER_END, TransfurCause::firstLimb, // LEFT ARM
            LimbCoverTransition.COVER_START, TransfurCause::secondHalfLimb, // RIGHT ARM
            LimbCoverTransition.COVER_START, TransfurCause::thirdLimb, // LEFT LEG
            LimbCoverTransition.COVER_START, TransfurCause::thirdLimb),
    WALL_HAZARD_RIGHT(
            LimbCoverTransition.COVER_START, TransfurCause::secondHalfLimb, // HEAD
            LimbCoverTransition.COVER_FROM_RIGHT_ARM, TransfurCause::secondLimb, // TORSO
            LimbCoverTransition.COVER_START, TransfurCause::secondHalfLimb, // LEFT ARM
            LimbCoverTransition.COVER_END, TransfurCause::firstLimb, // RIGHT ARM
            LimbCoverTransition.COVER_START, TransfurCause::thirdLimb, // LEFT LEG
            LimbCoverTransition.COVER_START, TransfurCause::thirdLimb), // RIGHT LEG
    CEILING_HAZARD(
            LimbCoverTransition.COVER_END, TransfurCause::firstLimb, // HEAD
            LimbCoverTransition.COVER_FROM_HEAD, TransfurCause::secondLimb, // TORSO
            LimbCoverTransition.COVER_START, TransfurCause::secondHalfLimb, // LEFT ARM
            LimbCoverTransition.COVER_START, TransfurCause::secondHalfLimb, // RIGHT ARM
            LimbCoverTransition.COVER_START, TransfurCause::thirdLimb, // LEFT LEG
            LimbCoverTransition.COVER_START, TransfurCause::thirdLimb), // RIGHT LEG
    WAIST_HAZARD(
            LimbCoverTransition.COVER_START, TransfurCause::secondLimb, // HEAD
            LimbCoverTransition.COVER_FROM_LEGS, TransfurCause::firstLimb, // TORSO
            LimbCoverTransition.COVER_START, TransfurCause::secondLimb, // LEFT ARM
            LimbCoverTransition.COVER_START, TransfurCause::secondLimb, // RIGHT ARM
            LimbCoverTransition.COVER_START, TransfurCause::firstLimb, // LEFT LEG
            LimbCoverTransition.COVER_START, TransfurCause::firstLimb), // RIGHT LEG
    FACE_HAZARD(
            LimbCoverTransition.COVER_ATTACK, TransfurCause::firstLimb, // HEAD
            LimbCoverTransition.COVER_FROM_HEAD, TransfurCause::secondLimb, // TORSO
            LimbCoverTransition.COVER_START, TransfurCause::secondHalfLimb, // LEFT ARM
            LimbCoverTransition.COVER_START, TransfurCause::secondHalfLimb, // RIGHT ARM
            LimbCoverTransition.COVER_START, TransfurCause::thirdLimb, // LEFT LEG
            LimbCoverTransition.COVER_START, TransfurCause::thirdLimb), // RIGHT LEG

    // Specific causes that inherit from generic causes
    DARK_LATEX_CRYSTAL(FOOT_HAZARD_RIGHT),
    LATEX_PUDDLE(FOOT_HAZARD_RIGHT),
    LATEX_SYRINGE_FLOOR(FOOT_HAZARD_RIGHT),
    LATEX_WALL_SPLOTCH(WALL_HAZARD_RIGHT),
    SQUID_DOG_INKBALL(GRAB_REPLICATE),
    SYRINGE(ATTACK_REPLICATE_LEFT),
    PINK_PANTS(WAIST_HAZARD),
    WHITE_LATEX(GRAB_REPLICATE);

    private static float firstLimb(float totalProgress) {
        return Mth.clamp(Mth.map(totalProgress, 0.0f, 0.33333f, 0.0f, 1.0f), 0.0f, 1.0f);
    }

    private static float secondLimb(float totalProgress) {
        return Mth.clamp(Mth.map(totalProgress, 0.33333f, 0.66667f, 0.0f, 1.0f), 0.0f, 1.0f);
    }

    private static float secondHalfLimb(float totalProgress) {
        return Mth.clamp(Mth.map(totalProgress, 0.5f, 0.83333f, 0.0f, 1.0f), 0.0f, 1.0f);
    }

    private static float thirdLimb(float totalProgress) {
        return Mth.clamp(Mth.map(totalProgress, 0.66667f, 1.0f, 0.0f, 1.0f), 0.0f, 1.0f);
    }

    private final LimbCoverTransition headTransition, torsoTransition, leftArmTransition, rightArmTransition, leftLegTransition, rightLegTransition;
    private final Float2FloatFunction headTiming, torsoTiming, leftArmTiming, rightArmTiming, leftLegTiming, rightLegTiming;
    private final @Nullable TransfurCause inherits;
    private final float duration;

    TransfurCause(TransfurCause inherit) {
        this(inherit, 6.0f);
    }

    TransfurCause(TransfurCause inherit, float duration) {
        this.headTiming = inherit.headTiming;
        this.torsoTiming = inherit.torsoTiming;
        this.leftArmTiming = inherit.leftArmTiming;
        this.rightArmTiming = inherit.rightArmTiming;
        this.leftLegTiming = inherit.leftLegTiming;
        this.rightLegTiming = inherit.rightLegTiming;
        this.headTransition = inherit.headTransition;
        this.torsoTransition = inherit.torsoTransition;
        this.leftArmTransition = inherit.leftArmTransition;
        this.rightArmTransition = inherit.rightArmTransition;
        this.leftLegTransition = inherit.leftLegTransition;
        this.rightLegTransition = inherit.rightLegTransition;

        this.inherits = inherit;
        this.duration = duration;
    }

    TransfurCause(
            LimbCoverTransition headTransition, Float2FloatFunction headTiming,
            LimbCoverTransition torsoTransition, Float2FloatFunction torsoTiming,
            LimbCoverTransition leftArmTransition, Float2FloatFunction leftArmTiming,
            LimbCoverTransition rightArmTransition, Float2FloatFunction rightArmTiming,
            LimbCoverTransition leftLegTransition, Float2FloatFunction leftLegTiming,
            LimbCoverTransition rightLegTransition, Float2FloatFunction rightLegTiming) {
        this.headTiming = headTiming;
        this.torsoTiming = torsoTiming;
        this.leftArmTiming = leftArmTiming;
        this.rightArmTiming = rightArmTiming;
        this.leftLegTiming = leftLegTiming;
        this.rightLegTiming = rightLegTiming;
        this.headTransition = headTransition;
        this.torsoTransition = torsoTransition;
        this.leftArmTransition = leftArmTransition;
        this.rightArmTransition = rightArmTransition;
        this.leftLegTransition = leftLegTransition;
        this.rightLegTransition = rightLegTransition;

        this.inherits = null;
        this.duration = 6.0f;
    }

    TransfurCause(
            LimbCoverTransition headTransition, Float2FloatFunction headTiming,
            LimbCoverTransition torsoTransition, Float2FloatFunction torsoTiming,
            LimbCoverTransition leftArmTransition, Float2FloatFunction leftArmTiming,
            LimbCoverTransition rightArmTransition, Float2FloatFunction rightArmTiming,
            LimbCoverTransition leftLegTransition, Float2FloatFunction leftLegTiming,
            LimbCoverTransition rightLegTransition, Float2FloatFunction rightLegTiming,
            float duration) {
        this.headTiming = headTiming;
        this.torsoTiming = torsoTiming;
        this.leftArmTiming = leftArmTiming;
        this.rightArmTiming = rightArmTiming;
        this.leftLegTiming = leftLegTiming;
        this.rightLegTiming = rightLegTiming;
        this.headTransition = headTransition;
        this.torsoTransition = torsoTransition;
        this.leftArmTransition = leftArmTransition;
        this.rightArmTransition = rightArmTransition;
        this.leftLegTransition = leftLegTransition;
        this.rightLegTransition = rightLegTransition;

        this.inherits = null;
        this.duration = duration;
    }

    public Optional<TransfurCause> getParent() {
        return Optional.ofNullable(inherits);
    }

    public float getHeadProgress(float totalProgress) {
        return headTiming.get(totalProgress);
    }

    public float getTorsoProgress(float totalProgress) {
        return torsoTiming.get(totalProgress);
    }

    public float getLeftArmProgress(float totalProgress) {
        return leftArmTiming.get(totalProgress);
    }

    public float getRightArmProgress(float totalProgress) {
        return rightArmTiming.get(totalProgress);
    }

    public float getLeftLegProgress(float totalProgress) {
        return leftLegTiming.get(totalProgress);
    }

    public float getRightLegProgress(float totalProgress) {
        return rightLegTiming.get(totalProgress);
    }

    public LimbCoverTransition getHeadTransition() {
        return headTransition;
    }

    public LimbCoverTransition getTorsoTransition() {
        return torsoTransition;
    }

    public LimbCoverTransition getLeftArmTransition() {
        return leftArmTransition;
    }

    public LimbCoverTransition getRightArmTransition() {
        return rightArmTransition;
    }

    public LimbCoverTransition getLeftLegTransition() {
        return leftLegTransition;
    }

    public LimbCoverTransition getRightLegTransition() {
        return rightLegTransition;
    }

    public float getDuration() {
        return duration;
    }
}
