package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

public class UpperBodyCrouchAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public UpperBodyCrouchAnimator(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        super(head, torso, leftArm, rightArm);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.CROUCH;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        torso.xRot = 0.5F;
        rightArm.xRot += 0.4F;
        leftArm.xRot += 0.4F;

        head.y = 4.2F + core.hipOffset + (12.0f - core.legLength);;
        torso.y = 3.2F + core.hipOffset + (12.0f - core.legLength);;
        leftArm.y = 5.2F + core.hipOffset + (12.0f - core.legLength);;
        rightArm.y = 5.2F + core.hipOffset + (12.0f - core.legLength);;
    }
}
