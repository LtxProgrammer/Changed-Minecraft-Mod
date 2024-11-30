package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.world.entity.HumanoidArm;

public interface AdvancedArmedModel<T extends ChangedEntity> {
    void translateToHand(T entity, HumanoidArm arm, PoseStack poseStack);
}
