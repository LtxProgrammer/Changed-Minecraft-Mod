package net.ltxprogrammer.changed.client.renderer.animate.quadrupedal;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class TaurQuadrupedalInitAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends AbstractQuadrupedalAnimator<T, M> {
    public final ModelPart frontLeftLegLower, frontLeftFoot;
    public final ModelPart frontRightLegLower, frontRightFoot;
    public final ModelPart backLeftLegLower, backLeftFoot, backLeftPad;
    public final ModelPart backRightLegLower, backRightFoot, backRightPad;

    public TaurQuadrupedalInitAnimator(ModelPart torso, ModelPart frontLeftLeg, ModelPart frontLeftLegLower, ModelPart frontLeftFoot,
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
        return HumanoidAnimator.AnimateStage.INIT;
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
        torso.y = Mth.lerp(core.ageLerp, 0.0f, Mth.lerp(limbSwingAmount, 1.0f, 0.25f)) + core.hipOffset + (12.0f - core.legLength) + 12.5f;

        frontRightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / swingSpeed;
        frontLeftLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount / swingSpeed;
        frontRightLeg.yRot = 0.0F;
        frontLeftLeg.yRot = 0.0F;

        frontRightLeg.x = -3.2f;
        frontLeftLeg.x = 3.2f;
        frontRightLeg.zRot = Mth.PI * 0.015f;
        frontLeftLeg.zRot = Mth.PI * -0.015f;

        frontRightLeg.xRot += Mth.lerp(limbSwingAmount, Mth.PI * 0.03f, 0.0F);
        frontLeftLeg.xRot += Mth.lerp(limbSwingAmount, Mth.PI * -0.03f, 0.0F);

        frontRightLegLower.xRot = Mth.map(frontRightLeg.xRot, -1.1F, 1.1F, -0.08726646F, 0.08726646F);
        frontLeftLegLower.xRot = Mth.map(frontLeftLeg.xRot, -1.1F, 1.1F, -0.08726646F, 0.08726646F);

        frontRightFoot.xRot = Mth.clamp(-frontRightLeg.xRot, -0.2617994f, 0.2617994f);
        frontRightFoot.zRot = Mth.clamp(-frontRightLeg.zRot, -0.2617994f, 0.2617994f);
        frontLeftFoot.xRot = Mth.clamp(-frontLeftLeg.xRot, -0.2617994f, 0.2617994f);
        frontLeftFoot.zRot = Mth.clamp(-frontLeftLeg.zRot, -0.2617994f, 0.2617994f);


        backRightLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount / swingSpeed;
        backLeftLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / swingSpeed;
        backRightLeg.yRot = 0.0F;
        backLeftLeg.yRot = 0.0F;

        backRightLeg.x = -3.2f;
        backLeftLeg.x = 3.2f;
        backRightLeg.zRot = Mth.PI * 0.015f;
        backLeftLeg.zRot = Mth.PI * -0.015f;

        backRightLeg.xRot += Mth.lerp(limbSwingAmount, Mth.PI * 0.03f, 0.0F);
        backLeftLeg.xRot += Mth.lerp(limbSwingAmount, Mth.PI * -0.03f, 0.0F);

        backRightLegLower.xRot = Mth.map(backRightLeg.xRot, -1.1F, 1.1F, -0.08726646F, 0.08726646F);
        backLeftLegLower.xRot = Mth.map(backLeftLeg.xRot, -1.1F, 1.1F, -0.08726646F, 0.08726646F);
        backRightFoot.xRot = Mth.map(-backRightLeg.xRot, -1.1F, 1.1F, -0.1745329F, 0.1745329F);
        backLeftFoot.xRot = Mth.map(-backLeftLeg.xRot, -1.1F, 1.1F, -0.1745329F, 0.1745329F);

        backRightPad.xRot = Mth.clamp(-backRightLeg.xRot, -0.2617994f, 0.2617994f);
        backRightPad.zRot = Mth.clamp(-backRightLeg.zRot, -0.2617994f, 0.2617994f);
        backLeftPad.xRot = Mth.clamp(-backLeftLeg.xRot, -0.2617994f, 0.2617994f);
        backLeftPad.zRot = Mth.clamp(-backLeftLeg.zRot, -0.2617994f, 0.2617994f);
    }
}
