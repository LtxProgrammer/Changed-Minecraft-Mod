package net.ltxprogrammer.changed.client.renderer.animate.legless;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.ltxprogrammer.changed.client.renderer.animate.upperbody.SharkUpperBodySwimAnimator.*;

public class LeglessSwimAnimatorV2<T extends ChangedEntity, M extends EntityModel<T>> extends AbstractLeglessAnimator<T, M> {
    public static final float SWIM_AMOUNT = 0.2f;

    public LeglessSwimAnimatorV2(ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail, List<ModelPart> tailJoints) {
        super(abdomen, lowerAbdomen, tail, tailJoints);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.SWIM;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float midSegmentSway = -TORSO_SWAY_SCALE * Mth.cos(limbSwing * SWIM_RATE -
                (((float)Math.PI / 3.0F) * 0.75f));
        float legSway = -SWIM_AMOUNT * Mth.cos(limbSwing * SWIM_RATE -
                (((float)Math.PI / 3.0F) * -0.0f));
        float legSway2 = -SWIM_AMOUNT * Mth.cos(limbSwing * SWIM_RATE -
                (((float)Math.PI / 3.0F) * 0.5f));
        float legSway3 = -SWIM_AMOUNT * Mth.cos(limbSwing * SWIM_RATE -
                (((float)Math.PI / 3.0F) * 1f));
        float midSegmentLegOffset = Mth.map(midSegmentSway, -0.35f, 0.35f, 2.0f, -2.0f);

        abdomen.x = Mth.lerp(core.swimAmount, 0.0F, midSegmentLegOffset * SWIM_SCALE * 2.0f);
        abdomen.xRot = Mth.lerp(core.swimAmount, abdomen.xRot, 0.0f);
        abdomen.yRot = Mth.lerp(core.swimAmount, abdomen.yRot, 0.0f);
        abdomen.zRot = Mth.lerp(core.swimAmount, abdomen.zRot, legSway);
        lowerAbdomen.xRot = Mth.lerp(core.swimAmount, lowerAbdomen.xRot, 0.0f);
        lowerAbdomen.yRot = Mth.lerp(core.swimAmount, lowerAbdomen.yRot, 0.0f);
        lowerAbdomen.zRot = Mth.lerp(core.swimAmount, lowerAbdomen.zRot, legSway2);
        tail.xRot = Mth.lerp(core.swimAmount, tail.xRot, 0.0f);
        tail.yRot = Mth.lerp(core.swimAmount, tail.yRot, 0.0f);
        tail.zRot = Mth.lerp(core.swimAmount, tail.zRot, legSway3);


        float offset = 1F;
        for (ModelPart joint : tailJoints) {
            joint.zRot = Mth.lerp(core.swimAmount, joint.zRot, -SWIM_AMOUNT * Mth.cos(limbSwing * SWIM_RATE -
                    (((float)Math.PI / 3.0F) * offset)));
            joint.xRot = Mth.lerp(core.swimAmount, joint.xRot, 0.0f);
            joint.yRot = Mth.lerp(core.swimAmount, joint.yRot, 0.0f);
            offset += 0.5F;
        }
    }
}
