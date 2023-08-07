package net.ltxprogrammer.changed.mixin.compatibility.Rubidium;

import me.jellysquid.mods.sodium.client.render.chunk.passes.BlockRenderPass;
import net.ltxprogrammer.changed.extension.rubidium.ChangedBlockRenderPass;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.common.IExtensibleEnum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockRenderPass.class, remap = false)
public abstract class BlockRenderPassMixin implements IExtensibleEnum {
    private static BlockRenderPass create(String name, RenderType layer, boolean translucent, float alphaCutoff) {
        throw new IllegalStateException("Enum not extended");
    }

    @Unique
    private static void doNothing(BlockRenderPass pass) {
        // does nothing lol
    }

    @Inject(method = "values", at = @At("HEAD"))
    private static void injectChangedPasses(CallbackInfoReturnable<BlockRenderPass[]> callback) {
        // Ensures ChangedBlockRenderPass values are loaded
        doNothing(ChangedBlockRenderPass.LATEX_SOLID);
        doNothing(ChangedBlockRenderPass.LATEX_CUTOUT);
        doNothing(ChangedBlockRenderPass.LATEX_CUTOUT_MIPPED);
    }
}
