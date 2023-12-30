package net.ltxprogrammer.changed.client.renderer.animate.bipedal;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import static net.ltxprogrammer.changed.client.renderer.animate.upperbody.SharkUpperBodySwimAnimator.*;

public class SharkBipedalSwimAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractBipedalAnimator<T, M> {
    public final ModelPart leftLegLower, leftFoot, leftPad;
    public final ModelPart rightLegLower, rightFoot, rightPad;

    public SharkBipedalSwimAnimator(ModelPart leftLeg, ModelPart leftLegLower, ModelPart leftFoot, ModelPart leftPad,
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
        float midSegmentSway = -TORSO_SWAY_SCALE * Mth.cos(limbSwing * SWIM_RATE -
                (((float)Math.PI / 3.0F) * 0.75f));
        float legSway = 0.3F * Mth.cos(limbSwing * SWIM_RATE -
                (((float)Math.PI / 3.0F) * -0.0f));
        float midSegmentLegOffset = Mth.map(midSegmentSway, -0.35f, 0.35f, 2.0f, -2.0f);

        leftLeg.x += Mth.lerp(core.swimAmount, 0.0F, midSegmentLegOffset * (SWIM_SCALE + 2.0f));
        rightLeg.x += Mth.lerp(core.swimAmount, 0.0F, midSegmentLegOffset * (SWIM_SCALE + 2.0f));

        float rightSwaySwim = Mth.cos((limbSwing * 0.6662F * 0.1f) + Mth.PI) * 0.05f;
        float leftSwaySwim = Mth.cos(limbSwing * 0.6662F * 0.1f) * 0.05f;

        leftLeg.xRot = Mth.lerp(core.swimAmount, leftLeg.xRot, Mth.DEG_TO_RAD * -6.0f + leftSwaySwim);
        leftLeg.zRot = Mth.lerp(core.swimAmount, leftLeg.zRot, legSway - 0.075f);
        rightLeg.xRot = Mth.lerp(core.swimAmount, rightLeg.xRot, Mth.DEG_TO_RAD * -6.0f + rightSwaySwim);
        rightLeg.zRot = Mth.lerp(core.swimAmount, rightLeg.zRot, legSway + 0.075f);

        rightLegLower.xRot = Mth.lerp(core.swimAmount, rightLegLower.xRot, -0.6544985F);
        leftLegLower.xRot = Mth.lerp(core.swimAmount, leftLegLower.xRot, -0.6544985F);
        rightFoot.xRot = Mth.lerp(core.swimAmount, rightFoot.xRot, 0.3926991F);
        leftFoot.xRot = Mth.lerp(core.swimAmount, leftFoot.xRot, 0.3926991F);
        rightPad.xRot = Mth.lerp(core.swimAmount, rightPad.xRot, 0.3490659F);
        leftPad.xRot = Mth.lerp(core.swimAmount, leftPad.xRot, 0.3490659F);
    }
}
