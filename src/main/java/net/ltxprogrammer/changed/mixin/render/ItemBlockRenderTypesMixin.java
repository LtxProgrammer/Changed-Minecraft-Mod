package net.ltxprogrammer.changed.mixin.render;

import net.ltxprogrammer.changed.client.ChangedClient;
import net.ltxprogrammer.changed.client.ChangedShaders;
import net.ltxprogrammer.changed.client.WaveVisionRenderer;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.init.ChangedTags;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

@Mixin(value = ItemBlockRenderTypes.class, remap = false, priority = 500)
public abstract class ItemBlockRenderTypesMixin {
    @Unique
    private static boolean recurseFlag = false;

    private static boolean checkLatexCoveredLayers(BlockState state, RenderType type, CallbackInfoReturnable<Boolean> callback) {
        if (!state.getProperties().contains(COVERED) || state.getValue(COVERED) == LatexType.NEUTRAL)
            return false;

        if (type == RenderType.solid() || type == RenderType.cutoutMipped() || type == RenderType.cutout() || type == RenderType.translucent()) {
            callback.setReturnValue(false);
            return true;
        }
        final BlockState uncoveredState = state.setValue(COVERED, LatexType.NEUTRAL);

        if (type == ChangedShaders.latexSolid())
            callback.setReturnValue(ItemBlockRenderTypes.canRenderInLayer(uncoveredState, RenderType.solid()));
        else if (type == ChangedShaders.latexCutoutMipped())
            callback.setReturnValue(ItemBlockRenderTypes.canRenderInLayer(uncoveredState, RenderType.cutoutMipped()));
        else if (type == ChangedShaders.latexCutout())
            callback.setReturnValue(
                    ItemBlockRenderTypes.canRenderInLayer(uncoveredState, RenderType.cutout()) ||
                            ItemBlockRenderTypes.canRenderInLayer(uncoveredState, RenderType.translucent()));
        return callback.isCancelled();
    }

    private static boolean checkResonantBlockLayers(BlockState state, RenderType type, CallbackInfoReturnable<Boolean> callback) {
        if (!ChangedClient.shouldRenderingWaveVision())
            return false;
        if (!state.is(ChangedTags.Blocks.CRYSTALLINE) || recurseFlag)
            return false;

        if (type == RenderType.solid() || type == RenderType.cutoutMipped() || type == RenderType.cutout() || type == RenderType.translucent()) {
            callback.setReturnValue(false);
            return true;
        }

        recurseFlag = true;
        if (type == ChangedShaders.waveVisionResonantSolid(WaveVisionRenderer.LATEX_RESONANCE_NEUTRAL))
            callback.setReturnValue(ItemBlockRenderTypes.canRenderInLayer(state, RenderType.solid()));
        else if (type == ChangedShaders.waveVisionResonantCutoutMipped(WaveVisionRenderer.LATEX_RESONANCE_NEUTRAL))
            callback.setReturnValue(ItemBlockRenderTypes.canRenderInLayer(state, RenderType.cutoutMipped()));
        else if (type == ChangedShaders.waveVisionResonantCutout(WaveVisionRenderer.LATEX_RESONANCE_NEUTRAL))
            callback.setReturnValue(
                    ItemBlockRenderTypes.canRenderInLayer(state, RenderType.cutout()) ||
                            ItemBlockRenderTypes.canRenderInLayer(state, RenderType.translucent()));
        recurseFlag = false;
        return callback.isCancelled();
    }

    @Inject(method = "canRenderInLayer(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/renderer/RenderType;)Z", at = @At("HEAD"), cancellable = true)
    private static void canCoveredBlockRenderInLayer(BlockState state, RenderType type, CallbackInfoReturnable<Boolean> callback) {
        if (checkLatexCoveredLayers(state, type, callback))
            return;
        if (checkResonantBlockLayers(state, type, callback))
            return;
    }
}
