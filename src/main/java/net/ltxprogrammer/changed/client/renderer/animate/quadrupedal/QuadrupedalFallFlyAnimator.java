package net.ltxprogrammer.changed.client.renderer.animate.quadrupedal;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class QuadrupedalFallFlyAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractQuadrupedalAnimator<T, M> {
    public QuadrupedalFallFlyAnimator(ModelPart torso, ModelPart frontLeftLeg, ModelPart frontRightLeg, ModelPart backLeftLeg, ModelPart backRightLeg) {
        super(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.FALL_FLY;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float fallFlyingTicks = (float)entity.getFallFlyingTicks();
        float fallFlyingAmount = Mth.clamp(fallFlyingTicks * fallFlyingTicks / 100.0F, 0.0F, 1.0F);
        float adjust = fallFlyingAmount * (float)Math.PI * 0.5F;
        torso.xRot = Mth.lerp(fallFlyingAmount, torso.xRot, -(float)Math.PI * 0.5f);
        frontLeftLeg.xRot = Mth.lerp(fallFlyingAmount, frontLeftLeg.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F + (float)Math.PI));
        frontRightLeg.xRot = Mth.lerp(fallFlyingAmount, frontRightLeg.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F));
        backLeftLeg.xRot = Mth.lerp(fallFlyingAmount, backLeftLeg.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F));
        backRightLeg.xRot = Mth.lerp(fallFlyingAmount, backRightLeg.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F + (float)Math.PI));

        frontRightLeg.xRot += adjust * 0.7f;
        frontLeftLeg.xRot += adjust * 0.7f;
        backRightLeg.xRot += adjust;
        backLeftLeg.xRot += adjust;
    }
}
