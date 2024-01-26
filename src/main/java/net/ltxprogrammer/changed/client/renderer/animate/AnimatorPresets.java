package net.ltxprogrammer.changed.client.renderer.animate;

import net.ltxprogrammer.changed.client.renderer.animate.arm.AquaticArmSwimAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.arm.ArmBobAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.arm.ArmRideAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.arm.ArmSwimAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.armsets.*;
import net.ltxprogrammer.changed.client.renderer.animate.bipedal.*;
import net.ltxprogrammer.changed.client.renderer.animate.camera.*;
import net.ltxprogrammer.changed.client.renderer.animate.ears.*;
import net.ltxprogrammer.changed.client.renderer.animate.legless.*;
import net.ltxprogrammer.changed.client.renderer.animate.misc.SquidDogTentaclesInitAnimator;
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
                    .addAnimator(new WolfBipedalSwimAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> catBipedal(ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                              ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator
                    .addAnimator(new BipedalCrouchAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new CatBipedalInitAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addAnimator(new BipedalRideAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new BipedalStandAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new CatBipedalSwimAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> dragonBipedal(ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                              ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator
                    .addAnimator(new BipedalCrouchAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new DragonBipedalInitAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addAnimator(new DragonBipedalFallFlyAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addAnimator(new BipedalRideAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new BipedalStandAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new DragonBipedalSwimAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> sharkBipedal(ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                              ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator
                    .addAnimator(new BipedalCrouchAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new SharkBipedalInitAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addAnimator(new BipedalRideAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new BipedalStandAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new SharkBipedalSwimAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> orcaBipedal(ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                              ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator
                    .addAnimator(new BipedalCrouchAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new SharkBipedalInitAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addAnimator(new BipedalRideAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new BipedalStandAnimator<>(leftLeg, rightLeg))
                    .addAnimator(new OrcaBipedalSwimAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad));
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

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> wolfUpperBody(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.setupHandsNew(1, leftArm, rightArm)
                    .addAnimator(new WolfUpperBodyInitAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new WolfUpperBodyCrouchAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new WolfUpperBodyAttackAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new WolfUpperBodyStandAnimator<>(head, torso, leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> catUpperBody(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.setupHandsNew(1, leftArm, rightArm)
                    .addAnimator(new CatUpperBodyInitAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new CatUpperBodyCrouchAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new CatUpperBodyAttackAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new CatUpperBodyStandAnimator<>(head, torso, leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> dragonUpperBody(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.setupHandsNew(1, leftArm, rightArm)
                    .addAnimator(new DragonUpperBodyInitAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new DragonUpperBodyCrouchAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new DragonUpperBodyAttackAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new DragonUpperBodyStandAnimator<>(head, torso, leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> dragonWingedUpperBody(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.setupHandsNew(1, leftArm, rightArm)
                    .addAnimator(new WingedDragonUpperBodyInitAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new DragonUpperBodyCreativeFlyAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new DragonUpperBodyCrouchAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new DragonUpperBodyAttackAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new DragonUpperBodyStandAnimator<>(head, torso, leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> sharkUpperBody(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.setupHandsNew(1, leftArm, rightArm)
                    .addAnimator(new SharkUpperBodyInitAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkUpperBodyCrouchAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkUpperBodyAttackAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkUpperBodySwimAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkUpperBodyStandAnimator<>(head, torso, leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> orcaUpperBody(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        return animator -> {
            animator.setupHandsNew(1, leftArm, rightArm)
                    .addAnimator(new SharkUpperBodyInitAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkUpperBodyCrouchAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkUpperBodyAttackAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new OrcaUpperBodySwimAnimator<>(head, torso, leftArm, rightArm))
                    .addAnimator(new SharkUpperBodyStandAnimator<>(head, torso, leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> squidDogUpperBody(ModelPart head, ModelPart torso, ModelPart upperLeftArm, ModelPart upperRightArm, ModelPart lowerLeftArm, ModelPart lowerRightArm) {
        return animator -> {
            animator.setupHandsNew(1, upperLeftArm, upperRightArm)
                    .addAnimator(new SquidDogUpperBodyInitAnimator<>(head, torso, upperLeftArm, upperRightArm, lowerLeftArm, lowerRightArm))
                    .addAnimator(new WolfUpperBodyCrouchAnimator<>(head, torso, upperLeftArm, upperRightArm))
                    .addAnimator(new WolfUpperBodyAttackAnimator<>(head, torso, upperLeftArm, upperRightArm))
                    .addAnimator(new WolfUpperBodyStandAnimator<>(head, torso, upperLeftArm, upperRightArm));
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

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> catTail(ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator
                    .addAnimator(new CatTailInitAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSwimAnimator<>(tail, tailJoints))
                    .addAnimator(new TailCrouchAnimator<>(tail, tailJoints))
                    .addAnimator(new TailRideAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSleepAnimator<>(tail, tailJoints))
                    .addAnimator(new TailFallFlyAnimator<>(tail, tailJoints));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> dragonTail(ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator
                    .addAnimator(new DragonTailInitAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSwimAnimator<>(tail, tailJoints))
                    .addAnimator(new TailCrouchAnimator<>(tail, tailJoints))
                    .addAnimator(new TailRideAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSleepAnimator<>(tail, tailJoints))
                    .addAnimator(new TailFallFlyAnimator<>(tail, tailJoints));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> sharkTail(ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator
                    .addAnimator(new SharkTailInitAnimator<>(tail, tailJoints))
                    .addAnimator(new SharkTailSwimAnimator<>(tail, tailJoints))
                    .addAnimator(new TailCrouchAnimator<>(tail, tailJoints))
                    .addAnimator(new TailRideAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSleepAnimator<>(tail, tailJoints))
                    .addAnimator(new TailFallFlyAnimator<>(tail, tailJoints));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> orcaTail(ModelPart tail, List<ModelPart> tailJoints) {
        return animator -> {
            animator
                    .addAnimator(new SharkTailInitAnimator<>(tail, tailJoints))
                    .addAnimator(new OrcaTailSwimAnimator<>(tail, tailJoints))
                    .addAnimator(new TailCrouchAnimator<>(tail, tailJoints))
                    .addAnimator(new TailRideAnimator<>(tail, tailJoints))
                    .addAnimator(new TailSleepAnimator<>(tail, tailJoints))
                    .addAnimator(new TailFallFlyAnimator<>(tail, tailJoints));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> squidDogTentacles(List<ModelPart> upperLeftTentacle, List<ModelPart> upperRightTentacle, List<ModelPart> lowerLeftTentacle, List<ModelPart> lowerRightTentacle) {
        return animator -> {
            animator.addAnimator(new SquidDogTentaclesInitAnimator<>(upperLeftTentacle, upperRightTentacle, lowerLeftTentacle, lowerRightTentacle));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> wolfEars(ModelPart leftEar, ModelPart rightEar) {
        return animator -> {
            animator
                    .addAnimator(new WolfEarsInitAnimator<>(leftEar, rightEar));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> catEars(ModelPart leftEar, ModelPart rightEar) {
        return animator -> {
            animator
                    .addAnimator(new CatEarsInitAnimator<>(leftEar, rightEar));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> wingedOld(ModelPart leftWing, ModelPart rightWing) {
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

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> dragonWinged(ModelPart leftWingRoot, ModelPart leftWingBone1, ModelPart leftWingBone2,
                                                                                                               ModelPart rightWingRoot, ModelPart rightWingBone1, ModelPart rightWingBone2) {
        return animator -> {
            animator
                    .addAnimator(new DragonWingInitAnimator<>(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2))
                    .addAnimator(new DragonWingCreativeFlyAnimator<>(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2))
                    .addAnimator(new DragonWingFallFlyAnimator<>(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2));
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

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> wolfLikeOld(ModelPart head, ModelPart torso,
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

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> wolfLikeArmor(ModelPart head,
                                                                                                                ModelPart torso, ModelPart leftArm, ModelPart rightArm,
                                                                                                                ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                                ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(wolfBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(wolfUpperBody(head, torso, leftArm, rightArm))
                    .addAnimator(new WolfHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> wolfLike(ModelPart head, ModelPart leftEar, ModelPart rightEar,
                                                                                                           ModelPart torso, ModelPart leftArm, ModelPart rightArm,
                                                                                                           ModelPart tail, List<ModelPart> tailJoints,
                                                                                                           ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                           ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(wolfBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(wolfUpperBody(head, torso, leftArm, rightArm))
                    .addPreset(wolfTail(tail, tailJoints))
                    .addPreset(wolfEars(leftEar, rightEar))
                    .addAnimator(new WolfHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> catLikeArmor(ModelPart head,
                                                                                                               ModelPart torso, ModelPart leftArm, ModelPart rightArm,
                                                                                                               ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                               ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(catBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(catUpperBody(head, torso, leftArm, rightArm))
                    .addAnimator(new CatHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> catLike(ModelPart head, ModelPart leftEar, ModelPart rightEar,
                                                                                                           ModelPart torso, ModelPart leftArm, ModelPart rightArm,
                                                                                                           ModelPart tail, List<ModelPart> tailJoints,
                                                                                                           ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                           ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(catBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(catUpperBody(head, torso, leftArm, rightArm))
                    .addPreset(catTail(tail, tailJoints))
                    .addPreset(catEars(leftEar, rightEar))
                    .addAnimator(new CatHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> dragonLike(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm,
                                                                                                             ModelPart tail, List<ModelPart> tailJoints,
                                                                                                             ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                             ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(dragonBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(dragonUpperBody(head, torso, leftArm, rightArm))
                    .addPreset(dragonTail(tail, tailJoints))
                    .addAnimator(new DragonHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> bigTailDragonLike(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm,
                                                                                                             ModelPart tail, List<ModelPart> tailJoints,
                                                                                                             ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                             ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(dragonBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(dragonUpperBody(head, torso, leftArm, rightArm))
                    .addPreset(dragonTail(tail, tailJoints))
                    .addAnimator(new DragonHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> wingedDragonLike(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm,
                                                                                                                   ModelPart tail, List<ModelPart> tailJoints,
                                                                                                                   ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                                   ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad,

                                                                                                                   ModelPart leftWingRoot, ModelPart leftWingBone1, ModelPart leftWingBone2,
                                                                                                                   ModelPart rightWingRoot, ModelPart rightWingBone1, ModelPart rightWingBone2) {
        return animator -> {
            animator.addPreset(dragonBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(dragonWingedUpperBody(head, torso, leftArm, rightArm))
                    .addPreset(dragonTail(tail, tailJoints))
                    .addPreset(dragonWinged(leftWingRoot, leftWingBone1, leftWingBone2, rightWingRoot, rightWingBone1, rightWingBone2))
                    .addAnimator(new DragonBipedalCreativeFlyAnimator<>(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addAnimator(new DragonTailCreativeFlyAnimator<>(tail, tailJoints))
                    .addAnimator(new DragonHeadCreativeFlyAnimator<>(head))
                    .addAnimator(new DragonHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm))
                    .addCameraAnimator(new DragonCameraCreativeFlyAnimator<>());
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> squidDogLike(ModelPart head, ModelPart leftEar, ModelPart rightEar,
                                                                                                           ModelPart torso, ModelPart upperLeftArm, ModelPart upperRightArm, ModelPart lowerLeftArm, ModelPart lowerRightArm,
                                                                                                           ModelPart tail, List<ModelPart> tailJoints, List<ModelPart> upperLeftTentacle, List<ModelPart> upperRightTentacle, List<ModelPart> lowerLeftTentacle, List<ModelPart> lowerRightTentacle,
                                                                                                           ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                           ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(wolfBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(squidDogUpperBody(head, torso, upperLeftArm, upperRightArm, lowerLeftArm, lowerRightArm))
                    .addPreset(wolfTail(tail, tailJoints))
                    .addPreset(wolfEars(leftEar, rightEar))
                    .addPreset(squidDogTentacles(upperLeftTentacle, upperRightTentacle, lowerLeftTentacle, lowerRightTentacle))
                    .addAnimator(new WolfHeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(upperLeftArm, upperRightArm))
                    .addAnimator(new ArmBobAnimator<>(upperLeftArm, upperRightArm))
                    .addAnimator(new ArmRideAnimator<>(upperLeftArm, upperRightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> dragonLikeOld(ModelPart head, ModelPart torso,
                                                                                                                ModelPart leftArm, ModelPart rightArm,
                                                                                                                ModelPart tail, List<ModelPart> tailJoints,
                                                                                                                ModelPart leftLeg, ModelPart rightLeg,
                                                                                                                ModelPart leftWing, ModelPart rightWing) {
        return animator -> {
            animator.addPreset(bipedal(leftLeg, rightLeg))
                    .addPreset(upperBody(head, torso, leftArm, rightArm))
                    .addPreset(standardTail(tail, tailJoints))
                    .addPreset(wingedOld(leftWing, rightWing))
                    .addAnimator(new HeadInitAnimator<>(head))
                    .addAnimator(new ArmSwimAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm));
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> dragonLikeRemodel(ModelPart head, ModelPart torso,
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

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> sharkLikeOld(ModelPart head, ModelPart torso,
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

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> sharkLike(ModelPart head, ModelPart torso,
                                                                                                            ModelPart leftArm, ModelPart rightArm,
                                                                                                            ModelPart tail, List<ModelPart> tailJoints,

                                                                                                            ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                            ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(sharkBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(sharkUpperBody(head, torso, leftArm, rightArm))
                    .addPreset(sharkTail(tail, tailJoints))
                    .addAnimator(new SharkHeadInitAnimator<>(head))
                    .addAnimator(new SharkHeadSwimAnimator<>(head))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm))
                    .addCameraAnimator(new SharkCameraSwimAnimator<>());
        };
    }

    public static <T extends LatexEntity, M extends EntityModel<T>> Consumer<LatexAnimator<T, M>> orcaLike(ModelPart head, ModelPart torso,
                                                                                                            ModelPart leftArm, ModelPart rightArm,
                                                                                                            ModelPart tail, List<ModelPart> tailJoints,

                                                                                                            ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                                                                                            ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        return animator -> {
            animator.addPreset(orcaBipedal(leftLeg, leftLegLower, leftFoot, leftPad, rightLeg, rightLegLower, rightFoot, rightPad))
                    .addPreset(orcaUpperBody(head, torso, leftArm, rightArm))
                    .addPreset(orcaTail(tail, tailJoints))
                    .addAnimator(new SharkHeadInitAnimator<>(head))
                    .addAnimator(new OrcaHeadSwimAnimator<>(head))
                    .addAnimator(new ArmBobAnimator<>(leftArm, rightArm))
                    .addAnimator(new ArmRideAnimator<>(leftArm, rightArm))
                    .addCameraAnimator(new OrcaCameraSwimAnimator<>());
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
