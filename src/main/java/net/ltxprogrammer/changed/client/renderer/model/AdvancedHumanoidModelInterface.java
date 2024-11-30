package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;

public interface AdvancedHumanoidModelInterface<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> {
    @Deprecated
    default PoseStack getPlacementCorrectors(CorrectorType type) {
        return new PoseStack();
    }
    void setupHand(T entity);
    HumanoidAnimator<T, M> getAnimator(T entity);
    @Deprecated
    default ChangedEntity getFirstPersonReplacementModel() {
        return null;
    }

    default void scaleForBody(PoseStack poseStack) {}
    default void scaleForHead(PoseStack poseStack) {}
}
