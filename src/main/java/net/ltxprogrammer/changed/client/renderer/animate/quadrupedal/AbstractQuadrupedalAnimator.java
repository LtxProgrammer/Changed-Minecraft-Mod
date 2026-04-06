package net.ltxprogrammer.changed.client.renderer.animate.quadrupedal;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public abstract class AbstractQuadrupedalAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends HumanoidAnimator.Animator<T, M> {
    public final ModelPart torso;
    public final ModelPart frontLeftLeg;
    public final ModelPart frontRightLeg;
    public final ModelPart backLeftLeg;
    public final ModelPart backRightLeg;

    public AbstractQuadrupedalAnimator(ModelPart torso, ModelPart frontLeftLeg, ModelPart frontRightLeg, ModelPart backLeftLeg, ModelPart backRightLeg) {
        this.torso = torso;
        this.frontLeftLeg = frontLeftLeg;
        this.frontRightLeg = frontRightLeg;
        this.backLeftLeg = backLeftLeg;
        this.backRightLeg = backRightLeg;
    }

    @Override
    public void copyTo(HumanoidModel<?> humanoidModel) {
        super.copyTo(humanoidModel);
        humanoidModel.leftLeg.copyFrom(this.torso);
        humanoidModel.leftLeg.x += 1.9f;
        humanoidModel.rightLeg.copyFrom(this.torso);
        humanoidModel.rightLeg.x += -1.9f;

        humanoidModel.leftLeg.visible = this.torso.visible;
        humanoidModel.rightLeg.visible = this.torso.visible;
    }

    @Override
    public void copyFrom(HumanoidModel<?> humanoidModel) {
        super.copyFrom(humanoidModel);
        torso.xRot = Mth.rotLerp(0.5f, humanoidModel.leftLeg.xRot, humanoidModel.rightLeg.xRot);
        torso.yRot = Mth.rotLerp(0.5f, humanoidModel.leftLeg.yRot, humanoidModel.rightLeg.yRot);
        torso.zRot = Mth.rotLerp(0.5f, humanoidModel.leftLeg.zRot, humanoidModel.rightLeg.zRot);
        torso.x = Mth.lerp(0.5f, humanoidModel.leftLeg.x, humanoidModel.rightLeg.x);
        torso.y = Mth.lerp(0.5f, humanoidModel.leftLeg.y, humanoidModel.rightLeg.y);
        torso.z = Mth.lerp(0.5f, humanoidModel.leftLeg.z, humanoidModel.rightLeg.z);
    }
}
