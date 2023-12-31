package net.ltxprogrammer.changed.client.renderer.animate.bipedal;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class DragonBipedalFallFlyAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractBipedalAnimator<T, M> {
    public final ModelPart leftLegLower, leftFoot, leftPad;
    public final ModelPart rightLegLower, rightFoot, rightPad;

    public DragonBipedalFallFlyAnimator(ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
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
        return LatexAnimator.AnimateStage.FALL_FLY;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        leftLeg.xRot += Mth.lerp(core.fallFlyingAmount, 0.0f, Mth.DEG_TO_RAD * -10.0f);
        rightLeg.xRot += Mth.lerp(core.fallFlyingAmount, 0.0f, Mth.DEG_TO_RAD * -10.0f);

        rightLegLower.xRot = Mth.lerp(core.fallFlyingAmount, rightLegLower.xRot, -0.6544985F);
        leftLegLower.xRot = Mth.lerp(core.fallFlyingAmount, leftLegLower.xRot, -0.6544985F);
        rightFoot.xRot = Mth.lerp(core.fallFlyingAmount, rightFoot.xRot, 0.3926991F);
        leftFoot.xRot = Mth.lerp(core.fallFlyingAmount, leftFoot.xRot, 0.3926991F);
        rightPad.xRot = Mth.lerp(core.fallFlyingAmount, rightPad.xRot, 0.3490659F);
        leftPad.xRot = Mth.lerp(core.fallFlyingAmount, leftPad.xRot, 0.3490659F);
    }
}
