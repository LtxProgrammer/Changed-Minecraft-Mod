package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class TripleArmUpperBodyCrouchAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public final ModelPart upperLeftArm;
    public final ModelPart upperRightArm;
    public final ModelPart middleLeftArm;
    public final ModelPart middleRightArm;
    public final ModelPart lowerLeftArm;
    public final ModelPart lowerRightArm;

    public TripleArmUpperBodyCrouchAnimator(ModelPart head, ModelPart torso, ModelPart upperLeftArm, ModelPart upperRightArm, ModelPart middleLeftArm, ModelPart middleRightArm, ModelPart lowerLeftArm, ModelPart lowerRightArm) {
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
        return HumanoidAnimator.AnimateStage.CROUCH;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        torso.z = Mth.lerp(core.ageLerp, -1.0f, -1.5f);
        head.z = torso.z;

        torso.xRot = Mth.lerp(core.ageLerp, 0.5f, 0.6f);
        upperRightArm.xRot += 0.3F;
        upperLeftArm.xRot += 0.3F;
        middleRightArm.xRot += 0.55F;
        middleLeftArm.xRot += 0.55F;
        lowerRightArm.xRot += 0.55F;
        lowerLeftArm.xRot += 0.55F;

        torso.y = Mth.lerp(core.ageLerp, 3.2f, 4.0f) + core.calculateTorsoPositionY();
        head.y = torso.y + 0.5f;
        upperLeftArm.y = torso.y + 2.25f;
        upperRightArm.y = torso.y + 2.25f;
        middleLeftArm.y = torso.y + 6.25f;
        middleRightArm.y = torso.y + 6.25f;
        lowerLeftArm.y = torso.y + 10.25f;
        lowerRightArm.y = torso.y + 10.25f;
        lowerLeftArm.z = middleLeftArm.z + 4.25f;
        lowerRightArm.z = middleRightArm.z + 4.25f;
    }
}