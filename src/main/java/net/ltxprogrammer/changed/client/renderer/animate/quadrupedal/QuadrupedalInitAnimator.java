package net.ltxprogrammer.changed.client.renderer.animate.quadrupedal;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class QuadrupedalInitAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractQuadrupedalAnimator<T, M> {
    public QuadrupedalInitAnimator(ModelPart torso, ModelPart frontLeftLeg, ModelPart frontRightLeg, ModelPart backLeftLeg, ModelPart backRightLeg) {
        super(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg);
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

        torso.xRot = 0.0F;
        frontRightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / swingSpeed;
        frontLeftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount / swingSpeed;
        frontRightLeg.yRot = 0.0F;
        frontLeftLeg.yRot = 0.0F;
        frontRightLeg.zRot = 0.0F;
        frontLeftLeg.zRot = 0.0F;
        backRightLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount / swingSpeed;
        backLeftLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / swingSpeed;
        backRightLeg.yRot = 0.0F;
        backLeftLeg.yRot = 0.0F;
        backRightLeg.zRot = 0.0F;
        backLeftLeg.zRot = 0.0F;
    }
}
