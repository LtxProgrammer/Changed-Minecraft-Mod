package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;

public interface LatexHumanoidModelInterface<T extends LatexEntity, M extends EntityModel<T>> {
    ModelPart getArm(HumanoidArm arm);
    @Deprecated
    default PoseStack getPlacementCorrectors(CorrectorType type) {
        return new PoseStack();
    }
    void setupHand();
    LatexAnimator<T, M> getAnimator();
    @Deprecated
    default LatexEntity getFirstPersonReplacementModel() {
        return null;
    }
}
