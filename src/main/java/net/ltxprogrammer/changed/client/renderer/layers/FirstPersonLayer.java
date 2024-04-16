package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.entity.PlayerDataExtension;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public interface FirstPersonLayer<T extends LivingEntity> {
    float ZFIGHT_OFFSET = 1.0002f;

    default void renderFirstPersonOnFace(PoseStack stack, MultiBufferSource bufferSource, int packedLight, T entity, Camera camera) {}
    default void renderFirstPersonOnArms(PoseStack stack, MultiBufferSource bufferSource, int packedLight, T entity, HumanoidArm arm, PoseStack stackCorrector) {}

    static void renderFirstPersonLayersOnFace(PoseStack poseStack, Camera camera, float partialTicks) {
        if (!(Minecraft.getInstance().getCameraEntity() instanceof LivingEntity livingEntity))
            return;
        if (livingEntity instanceof PlayerDataExtension playerData && playerData.getLatexVariant() != null)
            livingEntity = playerData.getLatexVariant().getChangedEntity();

        final LivingEntity renderEntity = livingEntity;
        if (Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(renderEntity) instanceof LivingEntityRenderer livingEntityRenderer) {
            poseStack.pushPose();
            poseStack.scale(1.0f, -1.0f, 1.0f);
            poseStack.mulPose(camera.rotation());
            livingEntityRenderer.layers.forEach(layer -> {
                if (layer instanceof FirstPersonLayer firstPersonLayer) {
                    firstPersonLayer.renderFirstPersonOnFace(poseStack,
                            Minecraft.getInstance().renderBuffers().bufferSource(),
                            Minecraft.getInstance().level.getLightEngine().getRawBrightness(new BlockPos(renderEntity.getEyePosition()), 0),
                            renderEntity,
                            camera);
                }
            });
            poseStack.popPose();
        }
    }
}
