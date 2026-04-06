package net.ltxprogrammer.changed.client.renderer.animate.legless;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import java.util.List;

public abstract class AbstractLeglessAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends HumanoidAnimator.Animator<T, M> {
    public final ModelPart abdomen;
    public final ModelPart lowerAbdomen;
    public final ModelPart tail;
    public final List<ModelPart> tailJoints;

    public AbstractLeglessAnimator(ModelPart abdomen, ModelPart lowerAbdomen, ModelPart tail, List<ModelPart> tailJoints) {
        this.abdomen = abdomen;
        this.lowerAbdomen = lowerAbdomen;
        this.tail = tail;
        this.tailJoints = tailJoints;
    }

    @Override
    public void copyTo(HumanoidModel<?> humanoidModel) {
        super.copyTo(humanoidModel);
        humanoidModel.leftLeg.copyFrom(this.abdomen);
        humanoidModel.leftLeg.x += 1.9f;
        humanoidModel.leftLeg.y -= core.calculateLegPositionY() - 12.0f;
        humanoidModel.rightLeg.copyFrom(this.abdomen);
        humanoidModel.rightLeg.x += -1.9f;
        humanoidModel.rightLeg.y -= core.calculateLegPositionY() - 12.0f;

        humanoidModel.leftLeg.visible = this.abdomen.visible;
        humanoidModel.rightLeg.visible = this.abdomen.visible;
    }

    @Override
    public void copyFrom(HumanoidModel<?> humanoidModel) {
        super.copyFrom(humanoidModel);
        abdomen.xRot = Mth.rotLerp(0.5f, humanoidModel.leftLeg.xRot, humanoidModel.rightLeg.xRot);
        abdomen.yRot = Mth.rotLerp(0.5f, humanoidModel.leftLeg.yRot, humanoidModel.rightLeg.yRot);
        abdomen.zRot = Mth.rotLerp(0.5f, humanoidModel.leftLeg.zRot, humanoidModel.rightLeg.zRot);
        abdomen.x = Mth.lerp(0.5f, humanoidModel.leftLeg.x, humanoidModel.rightLeg.x);
        abdomen.y = Mth.lerp(0.5f, humanoidModel.leftLeg.y, humanoidModel.rightLeg.y);
        abdomen.z = Mth.lerp(0.5f, humanoidModel.leftLeg.z, humanoidModel.rightLeg.z);

        abdomen.y += core.calculateLegPositionY() - 12.0f;
    }
}
