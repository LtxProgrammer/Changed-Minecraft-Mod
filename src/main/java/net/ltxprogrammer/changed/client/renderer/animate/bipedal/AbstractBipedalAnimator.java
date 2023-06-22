package net.ltxprogrammer.changed.client.renderer.animate.bipedal;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

public abstract class AbstractBipedalAnimator<T extends LatexEntity, M extends EntityModel<T>> extends LatexAnimator.Animator<T, M> {
    public final ModelPart leftLeg;
    public final ModelPart rightLeg;

    public AbstractBipedalAnimator(ModelPart leftLeg, ModelPart rightLeg) {
        this.leftLeg = leftLeg;
        this.rightLeg = rightLeg;
    }
}
