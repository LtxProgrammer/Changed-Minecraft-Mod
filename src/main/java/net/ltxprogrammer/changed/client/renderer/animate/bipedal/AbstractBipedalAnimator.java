package net.ltxprogrammer.changed.client.renderer.animate.bipedal;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

public abstract class AbstractBipedalAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends HumanoidAnimator.Animator<T, M> {
    public final ModelPart leftLeg;
    public final ModelPart rightLeg;

    public AbstractBipedalAnimator(ModelPart leftLeg, ModelPart rightLeg) {
        this.leftLeg = leftLeg;
        this.rightLeg = rightLeg;
    }

    @Override
    public void copyTo(HumanoidModel<?> humanoidModel) {
        super.copyTo(humanoidModel);
        humanoidModel.leftLeg.copyFrom(this.leftLeg);
        humanoidModel.rightLeg.copyFrom(this.rightLeg);

        humanoidModel.leftLeg.visible = this.leftLeg.visible;
        humanoidModel.rightLeg.visible = this.rightLeg.visible;
    }

    @Override
    public void copyFrom(HumanoidModel<?> humanoidModel) {
        super.copyFrom(humanoidModel);
        this.leftLeg.copyFrom(humanoidModel.leftLeg);
        this.rightLeg.copyFrom(humanoidModel.rightLeg);
    }
}
