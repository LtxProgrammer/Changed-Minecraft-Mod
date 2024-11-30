package net.ltxprogrammer.changed.mixin.compatibility.Rubidium;

import it.unimi.dsi.fastutil.objects.Reference2IntArrayMap;
import me.jellysquid.mods.sodium.client.render.chunk.passes.BlockRenderPass;
import me.jellysquid.mods.sodium.client.render.chunk.passes.BlockRenderPassManager;
import net.ltxprogrammer.changed.client.ChangedShaders;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.extension.rubidium.ChangedBlockRenderPass;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockRenderPassManager.class, remap = false)
@RequiredMods("rubidium")
public abstract class BlockRenderPassManagerMixin {
    @Shadow @Final private Reference2IntArrayMap<RenderType> mappingsId;

    @Inject(method = "addMapping", at = @At("RETURN"))
    private void changedRenderMappings(RenderType layer, BlockRenderPass type, CallbackInfo ci) {
        if (layer == RenderType.solid())
            this.mappingsId.put(ChangedShaders.latexSolid(), ChangedBlockRenderPass.LATEX_SOLID.ordinal());
        else if (layer == RenderType.cutoutMipped())
            this.mappingsId.put(ChangedShaders.latexCutoutMipped(), ChangedBlockRenderPass.LATEX_CUTOUT_MIPPED.ordinal());
        else if (layer == RenderType.cutout())
            this.mappingsId.put(ChangedShaders.latexCutout(), ChangedBlockRenderPass.LATEX_CUTOUT.ordinal());
    }
}
