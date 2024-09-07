package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class TripleArmUpperBodyStandAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends AbstractUpperBodyAnimator<T, M> {
    public final ModelPart upperLeftArm;
    public final ModelPart upperRightArm;
    public final ModelPart middleLeftArm;
    public final ModelPart middleRightArm;
    public final ModelPart lowerLeftArm;
    public final ModelPart lowerRightArm;

    public TripleArmUpperBodyStandAnimator(ModelPart head, ModelPart torso, ModelPart upperLeftArm, ModelPart upperRightArm, ModelPart middleLeftArm, ModelPart middleRightArm, ModelPart lowerLeftArm, ModelPart lowerRightArm) {
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
        return HumanoidAnimator.AnimateStage.STAND;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        torso.z = Mth.lerp(core.ageLerp, -0.2f, -0.8f);
        torso.z = Mth.lerp(limbSwingAmount, torso.z, 0.0f);
        head.z = torso.z;
        upperLeftArm.z = torso.z;
        upperRightArm.z = torso.z;
        middleLeftArm.z = torso.z;
        middleRightArm.z = torso.z;
        lowerLeftArm.z = torso.z;
        lowerRightArm.z = torso.z;

        torso.xRot = Mth.lerp(core.ageLerp, (float)Math.PI / 50.0f, (float)Math.PI / 42.0f);
        torso.xRot = Mth.lerp(limbSwingAmount, torso.xRot, 0.0f);

        torso.y = Mth.lerp(core.ageLerp, 0.0f, Mth.lerp(limbSwingAmount, 1.0f, 0.25f)) + core.calculateTorsoPositionY();
        head.y = torso.y + Mth.lerp(limbSwingAmount, 0.15f, 0.025f);
        upperLeftArm.y = torso.y + 2.0f;
        upperRightArm.y = torso.y + 2.0f;
        middleLeftArm.y = torso.y + 6.0f;
        middleRightArm.y = torso.y + 6.0f;
        lowerLeftArm.y = torso.y + 10.0f;
        lowerRightArm.y = torso.y + 10.0f;
    }
}