package net.ltxprogrammer.changed.client.renderer.animate.tail;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

import java.util.List;

public abstract class AbstractTailAnimator<T extends LatexEntity, M extends EntityModel<T>> extends LatexAnimator.Animator<T, M> {
    public final ModelPart tail;
    public final List<ModelPart> tailJoints;

    public AbstractTailAnimator(ModelPart tail, List<ModelPart> tailJoints) {
        this.tail = tail;
        this.tailJoints = tailJoints;
    }
}
