package net.ltxprogrammer.changed.client.renderer.animate.ears;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

public abstract class AbstractEarsAnimator<T extends LatexEntity, M extends EntityModel<T>> extends LatexAnimator.Animator<T, M> {
    public final ModelPart leftEar;
    public final ModelPart rightEar;

    public AbstractEarsAnimator(ModelPart leftEar, ModelPart rightEar) {
        this.leftEar = leftEar;
        this.rightEar = rightEar;
    }
}
