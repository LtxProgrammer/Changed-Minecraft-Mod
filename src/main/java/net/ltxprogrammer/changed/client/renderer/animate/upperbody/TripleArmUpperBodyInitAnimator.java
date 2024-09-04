package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.UseItemMode;
import net.ltxprogrammer.changed.item.SpecializedAnimations;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

public class TripleArmUpperBodyInitAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public final ModelPart upperLeftArm;
    public final ModelPart upperRightArm;
    public final ModelPart middleLeftArm;
    public final ModelPart middleRightArm;
    public final ModelPart lowerLeftArm;
    public final ModelPart lowerRightArm;

    public TripleArmUpperBodyInitAnimator(ModelPart head, ModelPart torso, ModelPart upperLeftArm, ModelPart upperRightArm, ModelPart middleLeftArm, ModelPart middleRightArm, ModelPart lowerLeftArm, ModelPart lowerRightArm) {
        super(head, torso, upperLeftArm, upperRightArm);
        this.upperLeftArm = upperLeftArm;
        this.upperRightArm = upperRightArm;
        this.middleLeftArm = middleLeftArm;
        this.middleRightArm = middleRightArm;
        this.lowerLeftArm = lowerLeftArm;
        this.lowerRightArm = lowerRightArm;
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.INIT;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean fallFlying = entity.getFallFlyingTicks() > 4;
        torso.yRot = 0.0F;
        torso.y = core.calculateTorsoPositionY();
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
        lowerLeftArm.y = torso.y + 10.0f;
        lowerLeftArm.z = torso.z;
        lowerRightArm.x = torso.x - core.torsoWidth;
        lowerRightArm.y = torso.y + 10.0f;
        lowerRightArm.z = torso.z;
        middleLeftArm.x = torso.x + core.torsoWidth;
        middleLeftArm.y = torso.y + 6.0f;
        middleLeftArm.z = torso.z;
        middleRightArm.x = torso.x - core.torsoWidth;
        middleRightArm.y = torso.y + 6.0f;
        middleRightArm.z = torso.z;
        upperLeftArm.x = torso.x + core.torsoWidth;
        upperLeftArm.y = torso.y + 2.0f;
        upperLeftArm.z = torso.z;
        upperRightArm.x = torso.x - core.torsoWidth;
        upperRightArm.y = torso.y + 2.0f;
        upperRightArm.z = torso.z;

        lowerRightArm.zRot = 0.0F;
        lowerLeftArm.zRot = 0.0F;
        middleRightArm.zRot = 0.0F;
        middleLeftArm.zRot = 0.0F;

        middleLeftArm.y += Mth.lerp(limbSwingAmount, 1.0f, 0.0f);
        middleLeftArm.z += Mth.lerp(limbSwingAmount, 1.0f, 0.0f);
        middleLeftArm.x -= Mth.lerp(limbSwingAmount, 0.5f, 0.0f);
        middleLeftArm.xRot = -Mth.lerp(limbSwingAmount, 50.0f, 20.0f) * Mth.DEG_TO_RAD;
        middleLeftArm.yRot = Mth.lerp(limbSwingAmount, 22.5f, 15.0f) * Mth.DEG_TO_RAD;

        middleRightArm.y += Mth.lerp(limbSwingAmount, 1.0f, 0.0f);
        middleRightArm.z += Mth.lerp(limbSwingAmount, 1.0f, 0.0f);
        middleRightArm.x += Mth.lerp(limbSwingAmount, 0.5f, 0.0f);
        middleRightArm.xRot = -Mth.lerp(limbSwingAmount, 50.0f, 20.0f) * Mth.DEG_TO_RAD;
        middleRightArm.yRot = -Mth.lerp(limbSwingAmount, 22.5f, 15.0f) * Mth.DEG_TO_RAD;

        lowerLeftArm.xRot = -25.0f * Mth.DEG_TO_RAD;
        lowerLeftArm.yRot = -20.0f * Mth.DEG_TO_RAD;
        lowerRightArm.xRot = -25.0f * Mth.DEG_TO_RAD;
        lowerRightArm.yRot = 20.0f * Mth.DEG_TO_RAD;

        upperLeftArm.xRot = -20.0f * Mth.DEG_TO_RAD;
        upperLeftArm.zRot = -20.0f * Mth.DEG_TO_RAD;
        upperRightArm.xRot = -20.0f * Mth.DEG_TO_RAD;
        upperRightArm.zRot = 20.0f * Mth.DEG_TO_RAD;
    }
}