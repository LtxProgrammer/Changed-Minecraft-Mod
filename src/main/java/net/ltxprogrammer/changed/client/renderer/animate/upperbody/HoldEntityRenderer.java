package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class HoldEntityRenderer<T extends LatexEntity, M extends EntityModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public HoldEntityRenderer(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        super(head, torso, leftArm, rightArm);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.FINAL;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (core.grabState == LatexHumanoidModel.GrabState.HOLD) {
            rightArm.xRot = Mth.PI * -0.5f;
            rightArm.yRot = Mth.PI * -0.3f;
            leftArm.xRot = Mth.PI * -0.5f;
            leftArm.yRot = Mth.PI * 0.3f;

            rightArm.z -= 2.5f;
            rightArm.y += 6.0f;
            leftArm.z -= 2.5f;
            leftArm.y += 2.0f;
        }
    }
}
