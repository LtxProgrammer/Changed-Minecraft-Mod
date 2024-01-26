package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.world.entity.HumanoidArm;

public interface DoubleArmedModel extends ArmedModel {
    void translateToUpperHand(HumanoidArm arm, PoseStack poseStack);
    void translateToLowerHand(HumanoidArm arm, PoseStack poseStack);

    default void translateToHand(HumanoidArm arm, PoseStack poseStack) {
        translateToUpperHand(arm, poseStack);
    }
}
