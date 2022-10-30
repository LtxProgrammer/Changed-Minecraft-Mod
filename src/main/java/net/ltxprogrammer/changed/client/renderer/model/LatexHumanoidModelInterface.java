package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;

public interface LatexHumanoidModelInterface {
    ModelPart getArm(HumanoidArm arm);
    default PoseStack getPlacementCorrectors(HumanoidArm arm) {
        return new PoseStack();
    }
    void setupHand();
    LatexHumanoidModelController getController();
    default LatexEntity getFirstPersonReplacementModel() {
        return null;
    }
}
