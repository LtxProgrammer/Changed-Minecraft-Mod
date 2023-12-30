package net.ltxprogrammer.changed.client.renderer.animate.bipedal;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static net.ltxprogrammer.changed.client.renderer.animate.wing.DragonWingCreativeFlyAnimator.BODY_FLY_SCALE;
import static net.ltxprogrammer.changed.client.renderer.animate.wing.DragonWingCreativeFlyAnimator.WING_FLAP_RATE;

public class DragonBipedalCreativeFlyAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractBipedalAnimator<T, M> {
    public final ModelPart leftLegLower, leftFoot, leftPad;
    public final ModelPart rightLegLower, rightFoot, rightPad;

    public DragonBipedalCreativeFlyAnimator(ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
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
        return LatexAnimator.AnimateStage.CREATIVE_FLY;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float flapAmount = Mth.cos(ageInTicks * WING_FLAP_RATE);
        flapAmount = Mth.map(flapAmount * flapAmount, 0.0f, 1.0f, -BODY_FLY_SCALE, BODY_FLY_SCALE);
        leftLeg.y += Mth.lerp(core.flyAmount, 0.0f, flapAmount - 4.0f);
        rightLeg.y += Mth.lerp(core.flyAmount, 0.0f, flapAmount - 4.0f);
        leftLeg.z += Mth.lerp(core.flyAmount, 0.0f, 7.0f);
        rightLeg.z += Mth.lerp(core.flyAmount, 0.0f, 7.0f);

        leftLeg.xRot = Mth.lerp(core.flyAmount, 0.0f,  Mth.DEG_TO_RAD * (15.0f + Mth.lerp(core.ageLerp, -3.5f, 3.5f)));
        rightLeg.xRot = Mth.lerp(core.flyAmount, 0.0f, Mth.DEG_TO_RAD * (15.0f + Mth.lerp(core.ageLerp, 3.5f, -3.5f)));

        rightLegLower.xRot = Mth.lerp(core.flyAmount, rightLegLower.xRot, -0.6544985F * 0.25f);
        leftLegLower.xRot = Mth.lerp(core.flyAmount, leftLegLower.xRot, -0.6544985F * 0.25f);
        rightFoot.xRot = Mth.lerp(core.flyAmount, rightFoot.xRot, 0.3926991F);
        leftFoot.xRot = Mth.lerp(core.flyAmount, leftFoot.xRot, 0.3926991F);
        rightPad.xRot = Mth.lerp(core.flyAmount, rightPad.xRot, 0.3490659F);
        leftPad.xRot = Mth.lerp(core.flyAmount, leftPad.xRot, 0.3490659F);
    }
}
