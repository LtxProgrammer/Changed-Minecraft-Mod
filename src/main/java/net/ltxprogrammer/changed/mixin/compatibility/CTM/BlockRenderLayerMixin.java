package net.ltxprogrammer.changed.mixin.compatibility.CTM;

import net.ltxprogrammer.changed.client.LatexCoveredBlockRenderer;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.chisel.ctm.client.util.BlockRenderLayer;

@Mixin(value = BlockRenderLayer.class, remap = false)
@RequiredMods("ctm")
public abstract class BlockRenderLayerMixin {
    @Inject(method = "fromType", at = @At("HEAD"), cancellable = true)
    private static void remapChanged(RenderType layer, CallbackInfoReturnable<BlockRenderLayer> cir) {
        if (layer == LatexCoveredBlockRenderer.latexSolid())
            cir.setReturnValue(BlockRenderLayer.SOLID);
        else if (layer == LatexCoveredBlockRenderer.latexCutout())
            cir.setReturnValue(BlockRenderLayer.CUTOUT);
        else if (layer == LatexCoveredBlockRenderer.latexCutoutMipped())
            cir.setReturnValue(BlockRenderLayer.CUTOUT_MIPPED);
    }
}
