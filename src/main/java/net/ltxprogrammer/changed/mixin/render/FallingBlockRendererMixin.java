package net.ltxprogrammer.changed.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.block.CustomFallable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FallingBlockRenderer.class)
public abstract class FallingBlockRendererMixin {
    @Redirect(method = "render(Lnet/minecraft/world/entity/item/FallingBlockEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;getBlockModel(Lnet/minecraft/world/level/block/state/BlockState;)Lnet/minecraft/client/resources/model/BakedModel;"))
    public BakedModel overrideBlockModel(BlockRenderDispatcher instance, BlockState state) {
        BakedModel original = instance.getBlockModel(state);

        if (state.getBlock() instanceof CustomFallable customFallable) {
            return Minecraft.getInstance().getModelManager().getModel(customFallable.getModelName());
        } else {
            return original;
        }
    }

    @Inject(method = "render(Lnet/minecraft/world/entity/item/FallingBlockEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At("RETURN"))
    public void renderBlockEntity(FallingBlockEntity entity, float yRot, float partialTick, PoseStack pose, MultiBufferSource bufferSource, int light, CallbackInfo ci) {
        BlockState state = entity.getBlockState();
        if (state.getBlock() instanceof CustomFallable customFallable && state.getBlock() instanceof EntityBlock entityBlock) {
            final BlockEntity tempBlockEntity = entityBlock.newBlockEntity(entity.getStartPos(), state);
            if (tempBlockEntity == null)
                return;

            if (entity.blockData != null)
                tempBlockEntity.load(entity.blockData);

            final var blockEntityRenderer = Minecraft.getInstance().getBlockEntityRenderDispatcher().getRenderer(tempBlockEntity);
            if (blockEntityRenderer != null) {
                pose.pushPose();

                pose.translate(-0.5, 0.0, -0.5);
                blockEntityRenderer.render(tempBlockEntity, partialTick, pose, bufferSource, light, OverlayTexture.NO_OVERLAY);

                pose.popPose();
            }
        }
    }
}
