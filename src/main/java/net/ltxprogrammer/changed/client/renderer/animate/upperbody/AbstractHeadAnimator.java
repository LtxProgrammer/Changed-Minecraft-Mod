package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

public abstract class AbstractHeadAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends HumanoidAnimator.Animator<T, M> {
    public final ModelPart head;

    public AbstractHeadAnimator(ModelPart head) {
        this.head = head;
    }

    @Override
    public void copyTo(HumanoidModel<?> humanoidModel) {
        super.copyTo(humanoidModel);
        humanoidModel.head.copyFrom(this.head);
        humanoidModel.hat.copyFrom(this.head);
    }

    @Override
    public void copyFrom(HumanoidModel<?> humanoidModel) {
        super.copyFrom(humanoidModel);
        this.head.copyFrom(humanoidModel.head);
    }
}
