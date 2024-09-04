package net.ltxprogrammer.changed.client.renderer.animate.legless;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static net.ltxprogrammer.changed.client.renderer.animate.upperbody.OrcaUpperBodySwimAnimator.SWIM_SCALE;
import static net.ltxprogrammer.changed.client.renderer.animate.upperbody.SharkUpperBodySwimAnimator.*;

public class LeglessSwimAnimatorV2Vertical<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractLeglessAnimator<T, M> {
    public static final float SWIM_AMOUNT = 0.2f;

    public LeglessSwimAnimatorV2Vertical(ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail, List<ModelPart> tailJoints) {
        super(abdomen, lowerAbdomen, tail, tailJoints);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.SWIM;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float midSegmentSway = -TORSO_SWAY_SCALE * Mth.cos(limbSwing * SWIM_RATE -
                (((float) Math.PI / 3.0F) * 0.75f));
        float legSway = -SWIM_AMOUNT * Mth.cos(limbSwing * SWIM_RATE -
                (((float) Math.PI / 3.0F) * -0.0f));
        float legSway2 = -SWIM_AMOUNT * Mth.cos(limbSwing * SWIM_RATE -
                (((float) Math.PI / 3.0F) * 0.5f));
        float legSway3 = -SWIM_AMOUNT * Mth.cos(limbSwing * SWIM_RATE -
                (((float) Math.PI / 3.0F) * 1f));

        abdomen.yRot = Mth.lerp(core.swimAmount, abdomen.yRot, 0.0f);
        abdomen.zRot = Mth.lerp(core.swimAmount, abdomen.zRot, 0.0f);
        abdomen.xRot = Mth.lerp(core.swimAmount, abdomen.xRot, legSway);
        lowerAbdomen.yRot = Mth.lerp(core.swimAmount, lowerAbdomen.yRot, 0.0f);
        lowerAbdomen.zRot = Mth.lerp(core.swimAmount, lowerAbdomen.zRot, 0.0f);
        lowerAbdomen.xRot = Mth.lerp(core.swimAmount, lowerAbdomen.xRot, legSway2);
        tail.yRot = Mth.lerp(core.swimAmount, tail.yRot, 0.0f);
        tail.zRot = Mth.lerp(core.swimAmount, tail.zRot, 0.0f);
        tail.xRot = Mth.lerp(core.swimAmount, tail.xRot, legSway3);

        float offset = 1F;
        for (ModelPart joint : tailJoints) {
            joint.xRot = Mth.lerp(core.swimAmount, joint.xRot, -0.35F * Mth.cos(limbSwing * SWIM_RATE -
                    (((float) Math.PI / 3.0F) * offset)));
            joint.yRot = Mth.lerp(core.swimAmount, joint.yRot, 0.0f);
            joint.zRot = Mth.lerp(core.swimAmount, joint.zRot, 0.0f);
            offset += 0.75F;
        }
    }
}
