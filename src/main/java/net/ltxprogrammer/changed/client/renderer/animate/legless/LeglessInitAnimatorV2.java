package net.ltxprogrammer.changed.client.renderer.animate.legless;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Animator that handles a slithering entity upright on land
 * @param <T>
 */
public class LeglessInitAnimatorV2<T extends LatexEntity, M extends EntityModel<T>> extends AbstractLeglessAnimator<T, M> {
    public LeglessInitAnimatorV2(ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail, List<ModelPart> tailJoints) {
        super(abdomen, lowerAbdomen, tail, tailJoints);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.INIT;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float slitherAmount = Math.min(0.5F, limbSwingAmount);
        abdomen.xRot = (float) Math.toRadians(-10);
        abdomen.yRot = Mth.cos(limbSwing * 0.33333334F + ((float)Math.PI / 2.0F)) * 0.6F * slitherAmount;
        abdomen.zRot = Mth.cos(limbSwing * 0.33333334F) * 0.6F * slitherAmount;
        lowerAbdomen.xRot = (float) Math.toRadians(80);
        lowerAbdomen.yRot = abdomen.yRot;
        lowerAbdomen.zRot = abdomen.zRot;
        tail.xRot = (float) Math.toRadians(20);
        tail.yRot = 0.0F;
        tail.zRot = 0.0F;

        float offset = 0.0F;
        for (ModelPart joint : tailJoints) {
            joint.zRot = -0.35F * Mth.cos(limbSwing * 0.33333334F -
                    (((float)Math.PI / 3.0F) * offset)) * slitherAmount;
            joint.xRot = 0.0F;
            joint.yRot = 0.0F;
            offset += 0.75F;
        }
    }
}
