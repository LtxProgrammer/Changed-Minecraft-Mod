package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class DoubleArmUpperBodyInitAnimator<T extends LatexEntity, M extends EntityModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public final ModelPart upperLeftArm;
    public final ModelPart upperRightArm;
    public final ModelPart lowerLeftArm;
    public final ModelPart lowerRightArm;

    public DoubleArmUpperBodyInitAnimator(ModelPart head, ModelPart torso, ModelPart upperLeftArm, ModelPart upperRightArm, ModelPart lowerLeftArm, ModelPart lowerRightArm) {
        super(head, torso, upperLeftArm, upperRightArm);
        this.upperLeftArm = upperLeftArm;
        this.upperRightArm = upperRightArm;
        this.lowerLeftArm = lowerLeftArm;
        this.lowerRightArm = lowerRightArm;
    }

    @Override
    public LatexAnimator.AnimateStage preferredStage() {
        return LatexAnimator.AnimateStage.INIT;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean fallFlying = entity.getFallFlyingTicks() > 4;
        torso.yRot = 0.0F;
        torso.y = core.hipOffset + (12.0f - core.legLength);
        float f = 1.0F;
        if (fallFlying) {
            f = (float)entity.getDeltaMovement().lengthSqr();
            f /= 0.2F;
            f *= f * f;
        }

        if (f < 1.0F) {
            f = 1.0F;
        }

        lowerLeftArm.x = torso.x + core.torsoWidth;
        lowerLeftArm.y = torso.y + 6.0f;
        lowerLeftArm.z = torso.z + core.forwardOffset;
        lowerRightArm.x = torso.x - core.torsoWidth;
        lowerRightArm.y = torso.y + 6.0f;
        lowerRightArm.z = torso.z + core.forwardOffset;
        upperLeftArm.x = torso.x + core.torsoWidth;
        upperLeftArm.y = torso.y + 2.0f;
        upperLeftArm.z = torso.z + core.forwardOffset;
        upperRightArm.x = torso.x - core.torsoWidth;
        upperRightArm.y = torso.y + 2.0f;
        upperRightArm.z = torso.z + core.forwardOffset;

        lowerRightArm.zRot = 0.0F;
        lowerLeftArm.zRot = 0.0F;
        upperRightArm.zRot = 0.0F;
        upperLeftArm.zRot = 0.0F;

        lowerLeftArm.y += Mth.lerp(limbSwingAmount, 1.0f, 0.0f);
        lowerLeftArm.z += Mth.lerp(limbSwingAmount, 1.0f, 0.0f);
        lowerLeftArm.x -= Mth.lerp(limbSwingAmount, 0.5f, 0.0f);
        lowerLeftArm.xRot = -Mth.lerp(limbSwingAmount, 50.0f, 20.0f) * Mth.DEG_TO_RAD;
        lowerLeftArm.yRot = Mth.lerp(limbSwingAmount, 22.5f, 15.0f) * Mth.DEG_TO_RAD;

        lowerRightArm.y += Mth.lerp(limbSwingAmount, 1.0f, 0.0f);
        lowerRightArm.z += Mth.lerp(limbSwingAmount, 1.0f, 0.0f);
        lowerRightArm.x += Mth.lerp(limbSwingAmount, 0.5f, 0.0f);
        lowerRightArm.xRot = -Mth.lerp(limbSwingAmount, 50.0f, 20.0f) * Mth.DEG_TO_RAD;
        lowerRightArm.yRot = -Mth.lerp(limbSwingAmount, 22.5f, 15.0f) * Mth.DEG_TO_RAD;

        upperLeftArm.xRot = -20.0f * Mth.DEG_TO_RAD;
        upperLeftArm.zRot = -20.0f * Mth.DEG_TO_RAD;
        upperRightArm.xRot = -20.0f * Mth.DEG_TO_RAD;
        upperRightArm.zRot = 20.0f * Mth.DEG_TO_RAD;

        /*rightArm.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 2.0F * limbSwingAmount * 0.5F / f;
        leftArm.xRot = Mth.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;

        rightArm.zRot += Mth.lerp(core.reachOut, 0.0F, 0.1745329f); // 10 Degrees
        leftArm.zRot += Mth.lerp(core.reachOut, 0.0F, -0.1745329f); // 10 Degrees
        rightArm.xRot = Mth.lerp(core.reachOut, rightArm.xRot, -0.5235988f); // 30 Degrees
        leftArm.xRot = Mth.lerp(core.reachOut, leftArm.xRot, -0.5235988f);   // 30 Degrees*/
    }
}