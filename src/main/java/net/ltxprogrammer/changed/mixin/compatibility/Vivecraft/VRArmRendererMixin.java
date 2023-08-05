package net.ltxprogrammer.changed.mixin.compatibility.Vivecraft;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.FormRenderHandler;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.provider.ControllerType;
import org.vivecraft.render.VRArmRenderer;

@Mixin(value = VRArmRenderer.class, remap = false)
public abstract class VRArmRendererMixin {
    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void latexHandOverride(ControllerType side, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn,
                                   AbstractClientPlayer playerIn, ModelPart rendererArmIn, ModelPart rendererArmwearIn, CallbackInfo callback) {
        if (FormRenderHandler.renderHand((VRArmRenderer)(Object)this, matrixStackIn, bufferIn, combinedLightIn, playerIn, rendererArmIn, rendererArmwearIn))
            callback.cancel();
    }
}
