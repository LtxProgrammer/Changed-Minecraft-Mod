package net.ltxprogrammer.changed.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.ltxprogrammer.changed.client.LatexCoveredBlocks;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {
    @Shadow protected abstract void renderChunkLayer(RenderType p_172994_, PoseStack p_172995_, double p_172996_, double p_172997_, double p_172998_, Matrix4f p_172999_);

    @Inject(method = "renderChunkLayer", at = @At("RETURN"))
    public void postRenderLayer(RenderType type, PoseStack pose, double x, double y, double z, Matrix4f matrix, CallbackInfo callback) {
        if (type == RenderType.solid()) {
            LatexCoveredBlocks.isRenderingChangedBlockLayer = true;
            renderChunkLayer(LatexCoveredBlocks.latexSolid(), pose, x, y, z, matrix);
            LatexCoveredBlocks.isRenderingChangedBlockLayer = false;
        }
        else if (type == RenderType.cutoutMipped()) {
            LatexCoveredBlocks.isRenderingChangedBlockLayer = true;
            renderChunkLayer(LatexCoveredBlocks.latexCutoutMipped(), pose, x, y, z, matrix);
            LatexCoveredBlocks.isRenderingChangedBlockLayer = false;
        }
        else if (type == RenderType.cutout()) {
            LatexCoveredBlocks.isRenderingChangedBlockLayer = true;
            renderChunkLayer(LatexCoveredBlocks.latexCutout(), pose, x, y, z, matrix);
            LatexCoveredBlocks.isRenderingChangedBlockLayer = false;
        }
    }
}
