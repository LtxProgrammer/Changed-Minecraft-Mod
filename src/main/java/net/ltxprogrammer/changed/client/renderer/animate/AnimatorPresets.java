package net.ltxprogrammer.changed.client.renderer.animate;

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

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> quadrupedal(ModelPart torso, ModelPart frontLeftLeg, ModelPart frontRightLeg, ModelPart backLeftLeg, ModelPart backRightLeg) {
        return animator -> {
            animator
                    .addAnimator(new QuadrupedalCrouchAnimator<>(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg))
                    .addAnimator(new QuadrupedalInitAnimator<>(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg))
                    .addAnimator(new QuadrupedalRideAnimator<>(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg))
                    .addAnimator(new QuadrupedalStandAnimator<>(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg));
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

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> upperBody(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.setupHands(1, leftArm, rightArm)
                    .addAnimator(new UpperBodyInitAnimator<>(torso, leftArm, rightArm))
                    .addAnimator(new UpperBodyCrouchAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new UpperBodyAttackAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new UpperBodyStandAnimator<>(head, torso, leftArm, rightArm));
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

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> standardTail(ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator
                    .addAnimator(new TailInitAnimator<>(tail, tailJoints))
                    .addAnimator(new TailCrouchAnimator<>(tail, tailJoints))
                    .addAnimator(new TailRideAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSleepAnimator<>(tail, tailJoints))
                    .addAnimator(new TailFallFlyAnimator<>(tail, tailJoints));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> winged(ModelPart leftWing, ModelPart rightWing) {
        return animator -> {
            animator
                    .addAnimator(new WingInitAnimator<>(leftWing, rightWing))
                    .addAnimator(new WingFallFlyAnimator<>(leftWing, rightWing));
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

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> taurLegs(ModelPart tail, List<ModelPart> tailJoints,
                                                                                                           ModelPart frontLeftLeg, ModelPart frontRightLeg,
                                                                                                           ModelPart lowerTorso, ModelPart backLeftLeg, ModelPart backRightLeg) {
        return animator -> {
            animator.addPreset(quadrupedal(lowerTorso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg))
                    .addPreset(standardTail(tail, tailJoints));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> taurLike(ModelPart head, ModelPart torso,
                                                                                                           ModelPart leftArm, ModelPart rightArm,
                                                                                                           ModelPart tail, List<ModelPart> tailJoints,
                                                                                                           ModelPart frontLeftLeg, ModelPart frontRightLeg,
                                                                                                           ModelPart lowerTorso, ModelPart backLeftLeg, ModelPart backRightLeg) {
        return animator -> {
            animator.addPreset(taurLegs(tail, tailJoints, frontLeftLeg, frontRightLeg, lowerTorso, backLeftLeg, backRightLeg))
                    .addPreset(upperBody(head, torso, leftArm, rightArm))
                    .addAnimator(new HeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }
}
