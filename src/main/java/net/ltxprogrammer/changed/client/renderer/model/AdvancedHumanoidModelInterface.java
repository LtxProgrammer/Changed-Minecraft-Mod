package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;

public interface AdvancedHumanoidModelInterface<T extends ChangedEntity, M extends EntityModel<T>> {
    ModelPart getArm(HumanoidArm arm);
    @Deprecated
    default PoseStack getPlacementCorrectors(CorrectorType type) {
        return new PoseStack();
    }
    void setupHand();
    HumanoidAnimator<T, M> getAnimator();
    @Deprecated
    default ChangedEntity getFirstPersonReplacementModel() {
        return null;
    }
}
