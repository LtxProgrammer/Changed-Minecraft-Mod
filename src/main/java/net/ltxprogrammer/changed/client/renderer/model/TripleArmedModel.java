package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ArmedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;

public interface TripleArmedModel extends DoubleArmedModel {
    void translateToMiddleHand(HumanoidArm arm, PoseStack poseStack);

    ModelPart getMiddleArm(HumanoidArm arm);
}
