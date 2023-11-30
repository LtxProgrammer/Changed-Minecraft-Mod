package net.ltxprogrammer.changed.client.renderer.animate;

import net.ltxprogrammer.changed.client.renderer.animate.arm.AquaticArmSwimAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.arm.ArmBobAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.arm.ArmRideAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.arm.ArmSwimAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.armsets.*;
import net.ltxprogrammer.changed.client.renderer.animate.bipedal.*;
import net.ltxprogrammer.changed.client.renderer.animate.ears.WolfEarsInitAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.legless.*;
import net.ltxprogrammer.changed.client.renderer.animate.quadrupedal.*;
import net.ltxprogrammer.changed.client.renderer.animate.tail.*;
import net.ltxprogrammer.changed.client.renderer.animate.upperbody.*;
import net.ltxprogrammer.changed.client.renderer.animate.wing.*;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

import java.util.List;
import java.util.function.Consumer;

public class AnimatorPresets {
    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> bipedal(ModelPart leftLeg, ModelPart rightLeg) {
        return animator -> {
            animator
                    .addAnimator(new BipedalCrouchAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new BipedalInitAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new BipedalRideAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new BipedalStandAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new BipedalSwimAnimator<>(leftLeg, rightLeg));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> wolfBipedal(ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                              ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator
                    .addAnimator(new BipedalCrouchAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new WolfBipedalInitAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addAnimator(new BipedalRideAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new BipedalStandAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new BipedalSwimAnimator<>(leftLeg, rightLeg));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> quadrupedal(ModelPart torso, ModelPart frontLeftLeg, ModelPart frontRightLeg, ModelPart backLeftLeg, ModelPart backRightLeg) {
        return animator -> {
            animator
                    .addAnimator(new QuadrupedalCrouchAnimator<>(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg))
                    .addAnimator(new QuadrupedalInitAnimator<>(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg))
                    .addAnimator(new QuadrupedalRideAnimator<>(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg))
                    .addAnimator(new QuadrupedalSwimAnimator<>(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg))
                    .addAnimator(new QuadrupedalStandAnimator<>(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg))
                    .addAnimator(new QuadrupedalFallFlyAnimator<>(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg))
                    .addAnimator(new QuadrupedalSleepAnimator<>(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> legless(ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator.addAnimator(new LeglessInitAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessCrouchAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessFallFlyAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessStandAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessSwimAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessSleepAnimator<>(abdomen, lowerAbdomen, tail, tailJoints));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> leglessV2(ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator.addAnimator(new LeglessInitAnimatorV2<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessCrouchAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessFallFlyAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessStandAnimator<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessSwimAnimatorV2<>(abdomen, lowerAbdomen, tail, tailJoints))
                    .addAnimator(new LeglessSleepAnimator<>(abdomen, lowerAbdomen, tail, tailJoints));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> taurUpperBody(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.setupHands(1, leftArm, rightArm)
                    .addAnimator(new UpperBodyInitAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new UpperBodyAttackAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new UpperBodyStandAnimator<>(head, torso, leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> upperBody(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.setupHands(1, leftArm, rightArm)
                    .addAnimator(new UpperBodyInitAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new UpperBodyCrouchAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new UpperBodyAttackAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new UpperBodyStandAnimator<>(head, torso, leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> wolfUpperBody(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart leftForearm, ModelPart rightArm, ModelPart rightForearm) {
        return animator -> {
            animator.setupHands(1, leftArm, rightArm)
                    .addAnimator(new WolfUpperBodyInitAnimator<>(head, torso, leftArm, leftForearm, rightArm, rightForearm))
                    .addAnimator(new WolfUpperBodyCrouchAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new UpperBodyAttackAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new WolfUpperBodyStandAnimator<>(head, torso, leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> aquaticUpperBody(ModelPart head, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator
                    .addAnimator(new AquaticArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new AquaticHeadInitAnimator<>(head));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> aquaticTail(ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator
                    .addAnimator(new AquaticTailInitAnimator<>(tail, tailJoints))
                    .addAnimator(new AquaticTailSwimAnimator<>(tail, tailJoints))
                    .addAnimator(new TailCrouchAnimator<>(tail, tailJoints))
                    .addAnimator(new TailRideAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSleepAnimator<>(tail, tailJoints))
                    .addAnimator(new TailFallFlyAnimator<>(tail, tailJoints));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> noSwimOrSleepTail(ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator
                    .addAnimator(new TailInitAnimator<>(tail, tailJoints))
                    .addAnimator(new TailCrouchAnimator<>(tail, tailJoints))
                    .addAnimator(new TailRideAnimator<>(tail, tailJoints));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> standardTail(ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator
                    .addAnimator(new TailInitAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSwimAnimator<>(tail, tailJoints))
                    .addAnimator(new TailCrouchAnimator<>(tail, tailJoints))
                    .addAnimator(new TailRideAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSleepAnimator<>(tail, tailJoints))
                    .addAnimator(new TailFallFlyAnimator<>(tail, tailJoints));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> wolfTail(ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator
                    .addAnimator(new WolfTailInitAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSwimAnimator<>(tail, tailJoints))
                    .addAnimator(new TailCrouchAnimator<>(tail, tailJoints))
                    .addAnimator(new TailRideAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSleepAnimator<>(tail, tailJoints))
                    .addAnimator(new TailFallFlyAnimator<>(tail, tailJoints));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> wolfEars(ModelPart leftEar, ModelPart rightEar) {
        return animator -> {
            animator
                    .addAnimator(new WolfEarsInitAnimator<>(leftEar, rightEar));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> winged(ModelPart leftWing, ModelPart rightWing) {
        return animator -> {
            animator
                    .addAnimator(new WingInitAnimator<>(leftWing, rightWing))
                    .addAnimator(new WingFallFlyAnimator<>(leftWing, rightWing));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> wingedV2(ModelPart leftWingRoot, ModelPart leftWingBone1, ModelPart leftWingBone2,
                                                                                                           ModelPart rightWingRoot, ModelPart rightWingBone1, ModelPart rightWingBone2) {
        return animator -> {
            animator
                    .addAnimator(new WingInitAnimatorV2<>(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2))
                    .addAnimator(new WingFallFlyAnimatorV2<>(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> legacyWinged(ModelPart leftWingRoot, ModelPart leftWingBone1, ModelPart leftWingBone2,
                                                                                                           ModelPart rightWingRoot, ModelPart rightWingBone1, ModelPart rightWingBone2) {
        return animator -> {
            animator
                    .addAnimator(new LegacyWingInitAnimator<>(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2))
                    .addAnimator(new LegacyWingFallFlyAnimator<>(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> armSetTwo(ModelPart leftArm, ModelPart rightArm,
                                                                                                            ModelPart leftArm2, ModelPart rightArm2) {
        return animator -> {
            animator.setupHands(2, leftArm2, rightArm2)
                    .addAnimator(new ArmSetTwoBobAnimator<>(leftArm, rightArm, leftArm2, rightArm2))
                    .addAnimator(new ArmSetTwoCrouchAnimator<>(leftArm, rightArm, leftArm2, rightArm2))
                    .addAnimator(new ArmSetTwoFinalAnimator<>(leftArm, rightArm, leftArm2, rightArm2))
                    .addAnimator(new ArmSetTwoStandAnimator<>(leftArm, rightArm, leftArm2, rightArm2));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> armSetThree(ModelPart leftArm, ModelPart rightArm,
                                                                                                              ModelPart leftArm3, ModelPart rightArm3) {
        return animator -> {
            animator.setupHands(3, leftArm3, rightArm3)
                    .addAnimator(new ArmSetThreeBobAnimator<>(leftArm, rightArm, leftArm3, rightArm3))
                    .addAnimator(new ArmSetThreeFinalAnimator<>(leftArm, rightArm, leftArm3, rightArm3));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> wolfLike(ModelPart head, ModelPart torso,
                                                                                                           ModelPart leftArm, ModelPart rightArm,
                                                                                                           ModelPart tail, List<ModelPart> tailJoints,
                                                                                                           ModelPart leftLeg, ModelPart rightLeg) {
        return animator -> {
            animator.addPreset(bipedal(leftLeg, rightLeg))
                    .addPreset(upperBody(head, torso, leftArm, rightArm))
                    .addPreset(standardTail(tail, tailJoints))
                    .addAnimator(new HeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> wolfLikeV2(ModelPart head, ModelPart leftEar, ModelPart rightEar,
                                                                                                             ModelPart torso,
                                                                                                             ModelPart leftArm, ModelPart leftForearm,
                                                                                                             ModelPart rightArm, ModelPart rightForearm,
                                                                                                             ModelPart tail, List<ModelPart> tailJoints,
                                                                                                             ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                             ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(wolfBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(wolfUpperBody(head, torso, leftArm, leftForearm, rightArm, rightForearm))
                    .addPreset(wolfTail(tail, tailJoints))
                    .addPreset(wolfEars(leftEar, rightEar))
                    .addAnimator(new WolfHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> dragonLike(ModelPart head, ModelPart torso,
                                                                                                             ModelPart leftArm, ModelPart rightArm,
                                                                                                             ModelPart tail, List<ModelPart> tailJoints,
                                                                                                             ModelPart leftLeg, ModelPart rightLeg,
                                                                                                             ModelPart leftWing, ModelPart rightWing) {
        return animator -> {
            animator.addPreset(bipedal(leftLeg, rightLeg))
                    .addPreset(upperBody(head, torso, leftArm, rightArm))
                    .addPreset(standardTail(tail, tailJoints))
                    .addPreset(winged(leftWing, rightWing))
                    .addAnimator(new HeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> dragonLikeV2(ModelPart head, ModelPart torso,
                                                                                                             ModelPart leftArm, ModelPart rightArm,
                                                                                                             ModelPart tail, List<ModelPart> tailJoints,
                                                                                                             ModelPart leftLeg, ModelPart rightLeg,

                                                                                                               ModelPart leftWingRoot, ModelPart leftWingBone1, ModelPart leftWingBone2,
                                                                                                               ModelPart rightWingRoot, ModelPart rightWingBone1, ModelPart rightWingBone2) {
        return animator -> {
            animator.addPreset(bipedal(leftLeg, rightLeg))
                    .addPreset(upperBody(head, torso, leftArm, rightArm))
                    .addPreset(standardTail(tail, tailJoints))
                    .addPreset(wingedV2(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2))
                    .addAnimator(new HeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> legacyDragonLike(ModelPart head, ModelPart torso,
                                                                                                               ModelPart leftArm, ModelPart rightArm,
                                                                                                               ModelPart tail, List<ModelPart> tailJoints,
                                                                                                               ModelPart leftLeg, ModelPart rightLeg,

                                                                                                               ModelPart leftWingRoot, ModelPart leftWingBone1, ModelPart leftWingBone2,
                                                                                                               ModelPart rightWingRoot, ModelPart rightWingBone1, ModelPart rightWingBone2) {
        return animator -> {
            animator.addPreset(bipedal(leftLeg, rightLeg))
                    .addPreset(upperBody(head, torso, leftArm, rightArm))
                    .addPreset(standardTail(tail, tailJoints))
                    .addPreset(legacyWinged(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2))
                    .addAnimator(new HeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> sharkLike(ModelPart head, ModelPart torso,
                                                                                                            ModelPart leftArm, ModelPart rightArm,
                                                                                                            ModelPart tail, List<ModelPart> tailJoints,
                                                                                                            ModelPart leftLeg, ModelPart rightLeg) {
        return animator -> {
            animator.addPreset(bipedal(leftLeg, rightLeg))
                    .addPreset(upperBody(head, torso, leftArm, rightArm))
                    .addPreset(aquaticUpperBody(head, leftArm, rightArm))
                    .addPreset(aquaticTail(tail, tailJoints))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> snakeLike(ModelPart head, ModelPart torso,
                                                                                                            ModelPart leftArm, ModelPart rightArm,
                                                                                                            ModelPart abdomen, ModelPart lowerAbdomen,
                                                                                                            ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator.addPreset(legless(abdomen, lowerAbdomen, tail, tailJoints))
                    .addPreset(upperBody(head, torso, leftArm, rightArm))
                    .addAnimator(new AquaticHeadInitAnimator<>(head))
                    .addAnimator(new AquaticArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> snakeLikeV2(ModelPart head, ModelPart torso,
                                                                                                            ModelPart leftArm, ModelPart rightArm,
                                                                                                            ModelPart abdomen, ModelPart lowerAbdomen,
                                                                                                            ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator.addPreset(leglessV2(abdomen, lowerAbdomen, tail, tailJoints))
                    .addPreset(upperBody(head, torso, leftArm, rightArm))
                    .addAnimator(new AquaticHeadInitAnimator<>(head))
                    .addAnimator(new AquaticArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> taurLegs(ModelPart tail, List<ModelPart> tailJoints,
                                                                                                           ModelPart frontLeftLeg, ModelPart frontRightLeg,
                                                                                                           ModelPart lowerTorso, ModelPart backLeftLeg, ModelPart backRightLeg) {
        return animator -> {
            animator.addPreset(quadrupedal(lowerTorso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg))
                    .addPreset(noSwimOrSleepTail(tail, tailJoints));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> taurLike(ModelPart head, ModelPart torso,
                                                                                                           ModelPart leftArm, ModelPart rightArm,
                                                                                                           ModelPart tail, List<ModelPart> tailJoints,
                                                                                                           ModelPart frontLeftLeg, ModelPart frontRightLeg,
                                                                                                           ModelPart lowerTorso, ModelPart backLeftLeg, ModelPart backRightLeg) {
        return animator -> {
            animator.addPreset(taurLegs(tail, tailJoints, frontLeftLeg, frontRightLeg, lowerTorso, backLeftLeg, backRightLeg))
                    .addPreset(taurUpperBody(head, torso, leftArm, rightArm))
                    .addAnimator(new HeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }
}
