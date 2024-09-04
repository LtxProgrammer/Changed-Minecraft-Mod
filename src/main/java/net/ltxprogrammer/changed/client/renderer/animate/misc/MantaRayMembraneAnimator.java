package net.ltxprogrammer.changed.client.renderer.animate.misc;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class MantaRayMembraneAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends HumanoidAnimator.Animator<T, M> {
    protected final ModelPart torso;
    protected final ModelPart rightArm;
    protected final ModelPart leftArm;
    protected final ModelPart rightMembrane;
    protected final ModelPart leftMembrane;
    protected final ModelPart rightMembraneJoint;
    protected final ModelPart leftMembraneJoint;

    public MantaRayMembraneAnimator(ModelPart torso, ModelPart rightArm, ModelPart leftArm, ModelPart rightMembrane, ModelPart leftMembrane, ModelPart rightMembraneJoint, ModelPart leftMembraneJoint) {
        this.torso = torso;
        this.rightArm = rightArm;
        this.leftArm = leftArm;
        this.rightMembrane = rightMembrane;
        this.leftMembrane = leftMembrane;
        this.rightMembraneJoint = rightMembraneJoint;
        this.leftMembraneJoint = leftMembraneJoint;
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.FINAL;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        float torsoOffset = (this.torso.yRot * -0.5f);
        this.rightMembrane.yRot = Mth.map(this.rightArm.xRot - this.torso.xRot, 0.0f, 60.0f, 0.0f, 42.5f) + torsoOffset;
        this.leftMembrane.yRot = Mth.map(this.leftArm.xRot - this.torso.xRot, 0.0f, 60.0f, 0.0f, -42.5f) + torsoOffset;
        this.rightMembraneJoint.yRot = Mth.map(this.rightArm.xRot - this.torso.xRot, 0.0f, -60.0f, 0.0f, -30.0f) + torsoOffset;
        this.leftMembraneJoint.yRot = Mth.map(this.leftArm.xRot - this.torso.xRot, 0.0f, -60.0f, 0.0f, 30.0f) + torsoOffset;

        this.rightMembrane.yRot = Mth.clamp(this.rightMembrane.yRot, Mth.DEG_TO_RAD * -42.5f, 0.0f);
        this.leftMembrane.yRot = Mth.clamp(this.leftMembrane.yRot, 0.0f, Mth.DEG_TO_RAD * 42.5f);
        this.rightMembraneJoint.yRot = Mth.clamp(this.rightMembraneJoint.yRot, 0.0f, Mth.DEG_TO_RAD * 30.0f);
        this.leftMembraneJoint.yRot = Mth.clamp(this.leftMembraneJoint.yRot, Mth.DEG_TO_RAD * -30.0f, 0.0f);
    }
}
