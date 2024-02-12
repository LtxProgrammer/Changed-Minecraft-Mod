package net.ltxprogrammer.changed.client.renderer.animate.ears;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

public abstract class AbstractEarsAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends HumanoidAnimator.Animator<T, M> {
    public final ModelPart leftEar;
    public final ModelPart rightEar;

    public AbstractEarsAnimator(ModelPart leftEar, ModelPart rightEar) {
        this.leftEar = leftEar;
        this.rightEar = rightEar;
    }
}
