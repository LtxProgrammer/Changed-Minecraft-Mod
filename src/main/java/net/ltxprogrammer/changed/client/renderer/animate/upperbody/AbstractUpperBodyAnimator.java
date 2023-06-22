package net.ltxprogrammer.changed.client.renderer.animate.upperbody;

import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.item.SpecializedAnimations;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;

public abstract class AbstractUpperBodyAnimator<T extends LatexEntity, M extends EntityModel<T>> extends LatexAnimator.Animator<T, M> {
    public final ModelPart head;
    public final ModelPart torso;
    public final ModelPart leftArm;
    public final ModelPart rightArm;

    protected SpecializedAnimations.AnimationHandler.UpperModelContext upperModelContext() {
        AbstractUpperBodyAnimator<T, M> tmp = this;
        return new SpecializedAnimations.AnimationHandler.UpperModelContext(leftArm, rightArm, torso, head) {
            final AbstractUpperBodyAnimator<T, M> controller = tmp;

            @Override
            public ModelPart getArm(HumanoidArm humanoidArm) {
                return controller.getArm(humanoidArm);
            }
        };
    }

    protected ModelPart getArm(HumanoidArm humanoidArm) {
        return humanoidArm == HumanoidArm.LEFT ? leftArm : rightArm;
    }

    public AbstractUpperBodyAnimator(ModelPart head, ModelPart torso, ModelPart leftArm, ModelPart rightArm) {
        this.head = head;
        this.torso = torso;
        this.leftArm = leftArm;
        this.rightArm = rightArm;
    }
}
