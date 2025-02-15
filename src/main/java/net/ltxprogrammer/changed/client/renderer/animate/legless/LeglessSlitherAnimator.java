package net.ltxprogrammer.changed.client.renderer.animate.legless;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.SpringType;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.ltxprogrammer.changed.client.renderer.animate.upperbody.SharkUpperBodySwimAnimator.*;

public class LeglessSlitherAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractLeglessAnimator<T, M> {
    public static float SLITHER_AMOUNT = 0.9f;
    public static float SLITHER_CURVE = 0.6f;
    public static float SEGMENT_SIZE = 1.7f;
    public static float SLITHER_RATE = 0.5f;

    public LeglessSlitherAnimator(ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail, List<ModelPart> tailJoints) {
        super(abdomen, lowerAbdomen, tail, tailJoints);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.SWIM;
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
        float tailDrag = entity.getTailDragAmount(this.core.partialTicks) * 0.5f;
        float verticalDrag = entity.getSimulatedSpring(SpringType.HEAVY_STRONG, SpringType.Direction.VERTICAL, this.core.partialTicks) * 0.4f;
        if (entity.isOnGround())
            verticalDrag = Math.min(0f, verticalDrag);

        float overallAngle = 0f;
        float segmentPosition = SEGMENT_SIZE;
        abdomen.xRot = Mth.lerp(core.swimAmount, abdomen.xRot, 0.0f);
        abdomen.yRot = Mth.lerp(core.swimAmount, abdomen.yRot, 0.0f);
        abdomen.zRot = Mth.lerp(core.swimAmount, abdomen.zRot, slitherAnim(segmentPosition, limbSwing * SLITHER_RATE) * 0.5f);
        overallAngle += abdomen.zRot;
        segmentPosition += SEGMENT_SIZE;
        lowerAbdomen.xRot = Mth.lerp(core.swimAmount, lowerAbdomen.xRot, -verticalDrag);
        lowerAbdomen.yRot = Mth.lerp(core.swimAmount, lowerAbdomen.yRot, 0.0f);
        lowerAbdomen.zRot = Mth.lerp(core.swimAmount, lowerAbdomen.zRot, slitherAngle(segmentPosition, limbSwing * SLITHER_RATE) - overallAngle);
        overallAngle += lowerAbdomen.zRot;
        segmentPosition += SEGMENT_SIZE;
        tail.xRot = Mth.lerp(core.swimAmount, tail.xRot, -verticalDrag);
        tail.yRot = Mth.lerp(core.swimAmount, tail.yRot, 0.0f);
        tail.zRot = Mth.lerp(core.swimAmount, tail.zRot, slitherAngle(segmentPosition, limbSwing * SLITHER_RATE) - overallAngle);
        overallAngle += tail.zRot;
        tail.zRot -= tailDrag * 1.5F;
        segmentPosition += SEGMENT_SIZE;

        for (ModelPart joint : tailJoints) {
            joint.zRot = Mth.lerp(core.swimAmount, joint.zRot, slitherAngle(segmentPosition, limbSwing * SLITHER_RATE) - overallAngle);
            joint.xRot = Mth.lerp(core.swimAmount, joint.xRot, -verticalDrag);
            joint.yRot = Mth.lerp(core.swimAmount, joint.yRot, 0.0f);
            overallAngle += joint.zRot;
            joint.zRot -= Mth.lerp(core.swimAmount, 0f, tailDrag * 1.5F);
            segmentPosition += SEGMENT_SIZE;
        }
    }
}
