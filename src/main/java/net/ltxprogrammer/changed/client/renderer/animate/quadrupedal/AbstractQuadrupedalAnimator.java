package net.ltxprogrammer.changed.client.renderer.animate.quadrupedal;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

public abstract class AbstractQuadrupedalAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends HumanoidAnimator.Animator<T, M> {
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
        humanoidModel.leftLeg.copyFrom(this.frontLeftLeg);
        humanoidModel.rightLeg.copyFrom(this.frontRightLeg);
    }

    @Override
    public void copyFrom(HumanoidModel<?> humanoidModel) {
        super.copyFrom(humanoidModel);
        /*this.frontLeftLeg.xRot = humanoidModel.leftLeg.xRot;
        this.frontLeftLeg.yRot = humanoidModel.leftLeg.yRot;
        this.frontLeftLeg.zRot = humanoidModel.leftLeg.zRot;
        this.frontRightLeg.xRot = humanoidModel.rightLeg.xRot;
        this.frontRightLeg.yRot = humanoidModel.rightLeg.yRot;
        this.frontRightLeg.zRot = humanoidModel.rightLeg.zRot;*/
    }
}
