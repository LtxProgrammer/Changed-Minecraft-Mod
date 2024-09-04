package net.ltxprogrammer.changed.client.renderer.animate.tail;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.geom.ModelPart;

import java.util.List;

public abstract class AbstractTailAnimator<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends HumanoidAnimator.Animator<T, M> {
    public final ModelPart tail;
    public final List<ModelPart> tailJoints;

    public AbstractTailAnimator(ModelPart tail, List<ModelPart> tailJoints) {
        this.tail = tail;
        this.tailJoints = tailJoints;
    }
}
