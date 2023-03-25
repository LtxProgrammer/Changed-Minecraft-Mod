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
        frontRightLeg.xRot += adjust;
        frontLeftLeg.xRot += adjust;
        backRightLeg.xRot += adjust;
        backLeftLeg.xRot += adjust;
    }
}
