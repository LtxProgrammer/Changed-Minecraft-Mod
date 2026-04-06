package net.ltxprogrammer.changed.client.renderer.animate.legless;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.beast.NagaEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import java.util.List;

public abstract class AbstractNagaAnimator<T extends ChangedEntity & NagaEntity, M extends AdvancedHumanoidModel<T>> extends HumanoidAnimator.Animator<T, M> {
    public final ModelPart waist;
    public final ModelPart upperAbdomen;
    public final ModelPart lowerAbdomen;
    public final ModelPart tailStart;
    public final List<ModelPart> tailJoints;

    public AbstractNagaAnimator(ModelPart waist, ModelPart upperAbdomen, ModelPart lowerAbdomen, ModelPart tailStart, List<ModelPart> tailJoints) {
        this.waist = waist;
        this.upperAbdomen = upperAbdomen;
        this.lowerAbdomen = lowerAbdomen;
        this.tailStart = tailStart;
        this.tailJoints = tailJoints;
    }

    @Override
    public void copyTo(HumanoidModel<?> humanoidModel) {
        super.copyTo(humanoidModel);
        humanoidModel.leftLeg.copyFrom(this.waist);
        humanoidModel.leftLeg.x += 1.9f;
        humanoidModel.leftLeg.y -= core.calculateLegPositionY() - 12.0f;
        humanoidModel.rightLeg.copyFrom(this.waist);
        humanoidModel.rightLeg.x += -1.9f;
        humanoidModel.rightLeg.y -= core.calculateLegPositionY() - 12.0f;

        humanoidModel.leftLeg.visible = this.waist.visible;
        humanoidModel.rightLeg.visible = this.waist.visible;
    }

    @Override
    public void copyFrom(HumanoidModel<?> humanoidModel) {
        super.copyFrom(humanoidModel);
        waist.xRot = Mth.rotLerp(0.5f, humanoidModel.leftLeg.xRot, humanoidModel.rightLeg.xRot);
        waist.yRot = Mth.rotLerp(0.5f, humanoidModel.leftLeg.yRot, humanoidModel.rightLeg.yRot);
        waist.zRot = Mth.rotLerp(0.5f, humanoidModel.leftLeg.zRot, humanoidModel.rightLeg.zRot);
        waist.x = Mth.lerp(0.5f, humanoidModel.leftLeg.x, humanoidModel.rightLeg.x);
        waist.y = Mth.lerp(0.5f, humanoidModel.leftLeg.y, humanoidModel.rightLeg.y);
        waist.z = Mth.lerp(0.5f, humanoidModel.leftLeg.z, humanoidModel.rightLeg.z);

        waist.y += core.calculateLegPositionY() - 12.0f;
    }
}
