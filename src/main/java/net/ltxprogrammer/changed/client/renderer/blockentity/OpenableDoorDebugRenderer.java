package net.ltxprogrammer.changed.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.block.entity.OpenableDoor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

public class OpenableDoorDebugRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    protected EntityRenderDispatcher entityRenderDispatcher;

    public OpenableDoorDebugRenderer(BlockEntityRendererProvider.Context context) {
        this.entityRenderDispatcher = context.getEntityRenderer();
    }

    protected void renderHitbox(PoseStack pose, VertexConsumer vertexConsumer, T blockEntity, float partialTicks) {
        if (!(blockEntity.getBlockState().getBlock() instanceof OpenableDoor door))
            return;

        var blockPos = blockEntity.getBlockPos();
        AABB aabb = door.getDetectionSize(blockEntity.getBlockState(), blockEntity.getLevel(), blockPos)
                .move(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());
        LevelRenderer.renderLineBox(pose, vertexConsumer, aabb, 1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void render(T blockEntity, float partialTicks, PoseStack pose, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        if (entityRenderDispatcher.shouldRenderHitBoxes() && !Minecraft.getInstance().showOnlyReducedInfo()) {
            renderHitbox(pose, buffers.getBuffer(RenderType.lines()), blockEntity, partialTicks);
        }
    }
}
