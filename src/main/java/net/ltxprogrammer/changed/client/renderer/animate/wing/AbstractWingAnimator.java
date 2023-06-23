package net.ltxprogrammer.changed.client.renderer.animate.wing;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

public abstract class AbstractWingAnimator<T extends LatexEntity, M extends EntityModel<T>> extends LatexAnimator.Animator<T, M> {
    public final ModelPart leftWing;
    public final ModelPart rightWing;

    public AbstractWingAnimator(ModelPart leftWing, ModelPart rightWing) {
        this.leftWing = leftWing;
        this.rightWing = rightWing;
    }
}
