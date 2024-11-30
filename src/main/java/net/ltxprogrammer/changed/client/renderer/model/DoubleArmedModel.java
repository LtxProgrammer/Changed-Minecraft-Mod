package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;

public interface DoubleArmedModel<T extends ChangedEntity> extends AdvancedArmedModel<T> {
    void translateToUpperHand(T entity, HumanoidArm arm, PoseStack poseStack);
    void translateToLowerHand(T entity, HumanoidArm arm, PoseStack poseStack);

    ModelPart getOtherArm(HumanoidArm arm);
    default void translateToHand(T entity, HumanoidArm arm, PoseStack poseStack) {
        translateToUpperHand(entity, arm, poseStack);
    }
}
