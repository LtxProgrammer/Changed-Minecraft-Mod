package net.ltxprogrammer.changed.client.renderer.animate.bipedal;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class WolfBipedalSwimAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractBipedalAnimator<T, M> {
    public final ModelPart leftLegLower, leftFoot, leftPad;
    public final ModelPart rightLegLower, rightFoot, rightPad;

    public WolfBipedalSwimAnimator(ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
                                   ModelPart rightLeg, ModelPart rightLegLower, ModelPart rightFoot, ModelPart rightPad) {
        super(leftLeg, rightLeg);
        this.leftLegLower = leftLegLower;
        this.leftFoot = leftFoot;
        this.leftPad = leftPad;
        this.rightLegLower = rightLegLower;
        this.rightFoot = rightFoot;
        this.rightPad = rightPad;
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.SWIM;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        leftLeg.xRot = Mth.lerp(core.swimAmount, leftLeg.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F + (float)Math.PI) + 0.2181662F);
        rightLeg.xRot = Mth.lerp(core.swimAmount, rightLeg.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F) + 0.2181662F);

        rightLegLower.xRot = Mth.lerp(core.swimAmount, rightLegLower.xRot, -0.6544985F);
        leftLegLower.xRot = Mth.lerp(core.swimAmount, leftLegLower.xRot, -0.6544985F);
        rightFoot.xRot = Mth.lerp(core.swimAmount, rightFoot.xRot, 0.3926991F);
        leftFoot.xRot = Mth.lerp(core.swimAmount, leftFoot.xRot, 0.3926991F);
        rightPad.xRot = Mth.lerp(core.swimAmount, rightPad.xRot, 0.3490659F);
        leftPad.xRot = Mth.lerp(core.swimAmount, leftPad.xRot, 0.3490659F);
    }
}
