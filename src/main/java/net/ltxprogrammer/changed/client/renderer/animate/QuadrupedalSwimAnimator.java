package net.ltxprogrammer.changed.client.renderer.animate;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class QuadrupedalSwimAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractQuadrupedalAnimator<T, M> {
    public QuadrupedalSwimAnimator(ModelPart torso, ModelPart frontLeftLeg, ModelPart frontRightLeg, ModelPart backLeftLeg, ModelPart backRightLeg) {
        super(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.SWIM;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float adjust = core.swimAmount * (float)Math.PI * 0.5F;
        torso.xRot = Mth.lerp(core.swimAmount, torso.xRot, -(float)Math.PI * 0.5f);
        frontLeftLeg.xRot = Mth.lerp(core.swimAmount, frontLeftLeg.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F + (float)Math.PI));
        frontRightLeg.xRot = Mth.lerp(core.swimAmount, frontRightLeg.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F));
        backLeftLeg.xRot = Mth.lerp(core.swimAmount, backLeftLeg.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F));
        backRightLeg.xRot = Mth.lerp(core.swimAmount, backRightLeg.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F + (float)Math.PI));

        frontRightLeg.xRot += adjust * 0.7f;
        frontLeftLeg.xRot += adjust * 0.7f;
        backRightLeg.xRot += adjust;
        backLeftLeg.xRot += adjust;
    }
}
