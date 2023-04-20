package net.ltxprogrammer.changed.client.renderer.animate;

import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;

public abstract class AbstractWingAnimatorV2<T extends LatexEntity, M extends EntityModel<T>> extends LatexAnimator.Animator<T, M> {
    public final ModelPart leftWingRoot;
    public final ModelPart leftWingBone1;
    public final ModelPart leftWingBone2;
    public final ModelPart rightWingRoot;
    public final ModelPart rightWingBone1;
    public final ModelPart rightWingBone2;

    public AbstractWingAnimatorV2(ModelPart leftWingRoot, ModelPart leftWingBone1, ModelPart leftWingBone2,
                                  ModelPart rightWingRoot, ModelPart rightWingBone1, ModelPart rightWingBone2) {
        this.leftWingRoot = leftWingRoot;
        this.leftWingBone1 = leftWingBone1;
        this.leftWingBone2 = leftWingBone2;
        this.rightWingRoot = rightWingRoot;
        this.rightWingBone1 = rightWingBone1;
        this.rightWingBone2 = rightWingBone2;
    }
}
