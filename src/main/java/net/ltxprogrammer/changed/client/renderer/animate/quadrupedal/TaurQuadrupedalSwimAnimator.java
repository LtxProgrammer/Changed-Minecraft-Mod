package net.ltxprogrammer.changed.client.renderer.animate.quadrupedal;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class TaurQuadrupedalSwimAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends AbstractQuadrupedalAnimator<T, M> {
    public final ModelPart frontLeftLegLower, frontLeftFoot;
    public final ModelPart frontRightLegLower, frontRightFoot;
    public final ModelPart backLeftLegLower, backLeftFoot, backLeftPad;
    public final ModelPart backRightLegLower, backRightFoot, backRightPad;

    public TaurQuadrupedalSwimAnimator(ModelPart torso, ModelPart frontLeftLeg, ModelPart frontLeftLegLower, ModelPart frontLeftFoot,
                                       ModelPart frontRightLeg, ModelPart frontRightLegLower, ModelPart frontRightFoot,
                                       ModelPart backLeftLeg, ModelPart backLeftLegLower, ModelPart backLeftFoot, ModelPart backLeftPad,
                                       ModelPart backRightLeg, ModelPart backRightLegLower, ModelPart backRightFoot, ModelPart backRightPad) {
        super(torso, frontLeftLeg, frontRightLeg, backLeftLeg, backRightLeg);
        this.frontLeftLegLower = frontLeftLegLower;
        this.frontLeftFoot = frontLeftFoot;
        this.frontRightLegLower = frontRightLegLower;
        this.frontRightFoot = frontRightFoot;
        this.backLeftLegLower = backLeftLegLower;
        this.backLeftFoot = backLeftFoot;
        this.backLeftPad = backLeftPad;
        this.backRightLegLower = backRightLegLower;
        this.backRightFoot = backRightFoot;
        this.backRightPad = backRightPad;
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.SWIM;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        //float adjust = core.swimAmount * (float)Math.PI * 0.5F;
        torso.xRot = Mth.lerp(core.swimAmount, torso.xRot, -(float)Math.PI * 0.5f);
        frontLeftLeg.xRot = Mth.lerp(core.swimAmount, frontLeftLeg.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F + (float)Math.PI) + 0.2181662F);
        frontRightLeg.xRot = Mth.lerp(core.swimAmount, frontRightLeg.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F) + 0.2181662F);
        backLeftLeg.xRot = Mth.lerp(core.swimAmount, backLeftLeg.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F) + 0.2181662F);
        backRightLeg.xRot = Mth.lerp(core.swimAmount, backRightLeg.xRot, 0.3F * Mth.cos(limbSwing * 0.33333334F + (float)Math.PI) + 0.2181662F);

        frontRightLegLower.xRot = Mth.lerp(core.swimAmount, frontRightLegLower.xRot, -0.6544985F);
        frontLeftLegLower.xRot = Mth.lerp(core.swimAmount, frontLeftLegLower.xRot, -0.6544985F);
        frontRightFoot.xRot = Mth.lerp(core.swimAmount, frontRightFoot.xRot, 0.3926991F);
        frontLeftFoot.xRot = Mth.lerp(core.swimAmount, frontLeftFoot.xRot, 0.3926991F);
        //frontRightFoot.xRot = Mth.lerp(core.swimAmount, rightPad.xRot, 0.3490659F);
        //frontLeftFoot.xRot = Mth.lerp(core.swimAmount, leftPad.xRot, 0.3490659F);

        frontLeftLeg.xRot += Mth.lerp(core.swimAmount, 0.0F, Mth.DEG_TO_RAD * 50);
        frontRightLeg.xRot += Mth.lerp(core.swimAmount, 0.0F, Mth.DEG_TO_RAD * 50);
        backRightLeg.xRot += Mth.lerp(core.swimAmount, 0.0F, Mth.DEG_TO_RAD * 80);
        backLeftLeg.xRot += Mth.lerp(core.swimAmount, 0.0F, Mth.DEG_TO_RAD * 80);
    }
}
