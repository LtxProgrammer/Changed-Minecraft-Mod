package net.ltxprogrammer.changed.client.renderer.animate.bipedal;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class WolfBipedalInitAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractBipedalAnimator<T, M> {
    public WolfBipedalInitAnimator(ModelPart leftLeg, ModelPart rightLeg) {
        super(leftLeg, rightLeg);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.INIT;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean fallFlying = entity.getFallFlyingTicks() > 4;
        float swingSpeed = 1.0F;
        if (fallFlying) {
            swingSpeed = (float)entity.getDeltaMovement().lengthSqr();
            swingSpeed /= 0.2F;
            swingSpeed *= swingSpeed * swingSpeed;
        }

        if (swingSpeed < 1.0F) {
            swingSpeed = 1.0F;
        }

        rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / swingSpeed;
        leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount / swingSpeed;
        rightLeg.yRot = 0.0F;
        leftLeg.yRot = 0.0F;

        rightLeg.x = -2.2f;
        leftLeg.x = 2.2f;
        rightLeg.zRot = Mth.PI * 0.015f;
        leftLeg.zRot = Mth.PI * -0.015f;

        rightLeg.xRot += Mth.lerp(limbSwingAmount, Mth.PI * 0.03f, 0.0F);
        leftLeg.xRot += Mth.lerp(limbSwingAmount, Mth.PI * -0.03f, 0.0F);
    }
}
