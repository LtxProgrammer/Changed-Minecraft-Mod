package net.ltxprogrammer.changed.client.renderer.animate.legless;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Animator that handles a slithering entity upright on land
 * @param <T>
 */
public class LeglessInitAnimatorV2<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractLeglessAnimator<T, M> {
    public static float SLITHER_AMOUNT = 0.9f;
    public static float SLITHER_CURVE = 0.6f;
    public static float SEGMENT_SIZE = 1.7f;
    public static float SLITHER_RATE = 0.5f;

    public LeglessInitAnimatorV2(ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail, List<ModelPart> tailJoints) {
        super(abdomen, lowerAbdomen, tail, tailJoints);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.INIT;
    }

    public float slitherAnim(float segmentPosition, float time) {
        return SLITHER_AMOUNT * Mth.sin(SLITHER_CURVE * segmentPosition - time) * (Math.min(segmentPosition, 2 * SEGMENT_SIZE) / (2 * SEGMENT_SIZE));
    }

    public float slitherAngle(float segmentPosition, float time, float lastPosition) {
        return (slitherAnim(segmentPosition, time) - lastPosition) / SEGMENT_SIZE;
    }

    public float slitherAngle(float segmentPosition, float time) {
        return slitherAngle(segmentPosition, time, slitherAnim(segmentPosition - SEGMENT_SIZE, time));
    }

    public float slitherDeltaAngle(float segmentPosition, float time) {
        var x0 = slitherAnim(segmentPosition, time);
        var x1 = slitherAnim(segmentPosition - SEGMENT_SIZE, time);
        var x2 = slitherAnim(segmentPosition - 2 * SEGMENT_SIZE, time);

        return ((x0 - x1) - (x1 - x2)) / SEGMENT_SIZE;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float overallAngle = 0f;
        float segmentPosition = SEGMENT_SIZE;
        abdomen.xRot = (float) Math.toRadians(-12.5);
        abdomen.yRot = slitherAnim(segmentPosition, limbSwing * SLITHER_RATE) * 0.5f;
        abdomen.zRot = 0f;
        overallAngle += -abdomen.yRot;
        segmentPosition += SEGMENT_SIZE * 2f;
        lowerAbdomen.xRot = (float) Math.toRadians(42.5);
        lowerAbdomen.yRot = 0f;//abdomen.yRot;
        lowerAbdomen.zRot = 0f;
        tail.xRot = (float) Math.toRadians(60);
        tail.zRot = 0f;
        tail.yRot = 0f;

        for (ModelPart joint : tailJoints) {
            joint.zRot = slitherAngle(segmentPosition, limbSwing * SLITHER_RATE) - overallAngle;
            joint.yRot = 0f;
            joint.xRot = 0f;
            overallAngle += joint.zRot;
            segmentPosition += SEGMENT_SIZE;
        }
    }
}
