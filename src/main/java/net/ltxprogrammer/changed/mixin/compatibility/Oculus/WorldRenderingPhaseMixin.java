package net.ltxprogrammer.changed.mixin.compatibility.Oculus;

import net.coderbot.iris.pipeline.WorldRenderingPhase;
import net.ltxprogrammer.changed.client.ChangedShaders;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = WorldRenderingPhase.class, remap = false)
@RequiredMods("oculus")
public abstract class WorldRenderingPhaseMixin {
    @Inject(method = "fromTerrainRenderType", at = @At("HEAD"), cancellable = true)
    private static void changedTerrainRenderTypes(RenderType renderType, CallbackInfoReturnable<WorldRenderingPhase> callback) {
        if (renderType == ChangedShaders.latexSolid()) {
            callback.setReturnValue(WorldRenderingPhase.TERRAIN_SOLID);
        } else if (renderType == ChangedShaders.latexCutout()) {
            callback.setReturnValue(WorldRenderingPhase.TERRAIN_CUTOUT);
        } else if (renderType == ChangedShaders.latexCutoutMipped()) {
            callback.setReturnValue(WorldRenderingPhase.TERRAIN_CUTOUT_MIPPED);
        }
    }
}
