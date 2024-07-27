package net.ltxprogrammer.changed.mixin.compatibility.CTM;

import net.ltxprogrammer.changed.client.LatexCoveredBlocks;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team.chisel.ctm.client.util.BlockRenderLayer;

@Mixin(value = BlockRenderLayer.class, remap = false)
public abstract class BlockRenderLayerMixin {
    @Inject(method = "fromType", at = @At("HEAD"), cancellable = true)
    private static void remapChanged(RenderType layer, CallbackInfoReturnable<BlockRenderLayer> cir) {
        if (layer == LatexCoveredBlocks.latexSolid())
            cir.setReturnValue(BlockRenderLayer.SOLID);
        else if (layer == LatexCoveredBlocks.latexCutout())
            cir.setReturnValue(BlockRenderLayer.CUTOUT);
        else if (layer == LatexCoveredBlocks.latexCutoutMipped())
            cir.setReturnValue(BlockRenderLayer.CUTOUT_MIPPED);
    }
}
