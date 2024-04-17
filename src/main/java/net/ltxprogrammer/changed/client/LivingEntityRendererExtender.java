package net.ltxprogrammer.changed.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;

public interface LivingEntityRendererExtender<T extends LivingEntity, M extends EntityModel<T>> {
    void directRender(T entity, float yRot, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight);
}
