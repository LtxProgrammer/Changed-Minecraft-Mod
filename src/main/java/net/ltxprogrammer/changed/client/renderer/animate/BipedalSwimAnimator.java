package net.ltxprogrammer.changed.client.renderer.animate;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class BipedalSwimAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractBipedalAnimator<T, M> {
    public BipedalSwimAnimator(ModelPart leftLeg, ModelPart rightLeg) {
        super(leftLeg, rightLeg);
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.SWIM;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        leftLeg.xRot = Mth.lerp(core.swimAmount, leftLeg.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F + (float)Math.PI));
        rightLeg.xRot = Mth.lerp(core.swimAmount, rightLeg.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F));
    }
}
