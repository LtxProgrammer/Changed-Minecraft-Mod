package net.ltxprogrammer.changed.client.renderer.animate.tail;

import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TailSleepAnimator<T extends ChangedEntity, M extends EntityModel<T>> extends AbstractTailAnimator<T, M> {
    public TailSleepAnimator(ModelPart tail, List<ModelPart> tailJoints) {
        super(tail, tailJoints);
    }

    @Override
    public HumanoidAnimator.AnimateStage preferredStage() {
        return HumanoidAnimator.AnimateStage.SLEEP;
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        tail.xRot = -1.4137167F;
    }
}
