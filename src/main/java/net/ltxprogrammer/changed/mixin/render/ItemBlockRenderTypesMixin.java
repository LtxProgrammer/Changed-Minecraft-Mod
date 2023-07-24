package net.ltxprogrammer.changed.mixin.render;

import net.ltxprogrammer.changed.client.LatexCoveredBlocks;
import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

@Mixin(value = ItemBlockRenderTypes.class, remap = false)
public abstract class ItemBlockRenderTypesMixin {
    @Inject(method = "canRenderInLayer(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/renderer/RenderType;)Z", at = @At("HEAD"), cancellable = true)
    private static void canCoveredBlockRenderInLayer(BlockState state, RenderType type, CallbackInfoReturnable<Boolean> callback) {
        if (!state.getProperties().contains(COVERED) || state.getValue(COVERED) == LatexType.NEUTRAL)
            return;

        if (type == RenderType.solid() || type == RenderType.cutoutMipped() || type == RenderType.cutout()) {
            callback.setReturnValue(false);
            return;
        }

        if (type == LatexCoveredBlocks.latexSolid())
            callback.setReturnValue(ItemBlockRenderTypes.canRenderInLayer(state.setValue(COVERED, LatexType.NEUTRAL), RenderType.solid()));
        else if (type == LatexCoveredBlocks.latexCutoutMipped())
            callback.setReturnValue(ItemBlockRenderTypes.canRenderInLayer(state.setValue(COVERED, LatexType.NEUTRAL), RenderType.cutoutMipped()));
        else if (type == LatexCoveredBlocks.latexCutout())
            callback.setReturnValue(ItemBlockRenderTypes.canRenderInLayer(state.setValue(COVERED, LatexType.NEUTRAL), RenderType.cutout()));
    }
}
