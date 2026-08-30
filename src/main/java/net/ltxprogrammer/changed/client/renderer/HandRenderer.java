package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

/// Allows renderer overrides to handle rendering arms (if applicable)
public interface HandRenderer<T extends LivingEntity> {
    void renderHand(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, HumanoidArm hand, PartPose armPose);
}
