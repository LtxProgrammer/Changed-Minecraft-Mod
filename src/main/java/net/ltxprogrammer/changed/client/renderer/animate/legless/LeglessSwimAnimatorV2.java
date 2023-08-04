package net.ltxprogrammer.changed.client.renderer.animate.legless;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LeglessSwimAnimatorV2<T extends LatexEntity, M extends EntityModel<T>> extends AbstractLeglessAnimator<T, M> {
    public LeglessSwimAnimatorV2(ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail, List<ModelPart> tailJoints) {
        super(abdomen, lowerAbdomen, tail, tailJoints);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.SWIM;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        abdomen.xRot = Mth.lerp(core.swimAmount, abdomen.xRot, 0.0f);
        abdomen.yRot = Mth.lerp(core.swimAmount, abdomen.yRot, 0.0f);
        abdomen.zRot = Mth.lerp(core.swimAmount, abdomen.zRot, 0.35F * Mth.cos(limbSwing * 0.33333334F));
        lowerAbdomen.xRot = Mth.lerp(core.swimAmount, lowerAbdomen.xRot, 0.0f);
        lowerAbdomen.yRot = Mth.lerp(core.swimAmount, lowerAbdomen.yRot, 0.0f);
        lowerAbdomen.zRot = Mth.lerp(core.swimAmount, lowerAbdomen.zRot, 0.35F * Mth.cos(limbSwing * 0.33333334F - ((float)Math.PI / 3.0F)));
        tail.xRot = Mth.lerp(core.swimAmount, tail.xRot, 0.0f);
        tail.yRot = Mth.lerp(core.swimAmount, tail.yRot, 0.0f);
        tail.zRot = Mth.lerp(core.swimAmount, tail.zRot, 0.35F * Mth.cos(limbSwing * 0.33333334F - ((float)Math.PI / 1.5F)));


        float offset = 0.0F;
        for (ModelPart joint : tailJoints) {
            joint.zRot = Mth.lerp(core.swimAmount, joint.zRot, -0.35F * Mth.cos(limbSwing * 0.33333334F -
                    (((float)Math.PI / 3.0F) * offset)));
            joint.xRot = Mth.lerp(core.swimAmount, joint.xRot, 0.0f);
            joint.yRot = Mth.lerp(core.swimAmount, joint.yRot, 0.0f);
            offset += 0.75F;
        }
    }
}
