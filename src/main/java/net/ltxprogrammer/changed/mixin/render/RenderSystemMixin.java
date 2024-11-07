package net.ltxprogrammer.changed.mixin.render;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import net.ltxprogrammer.changed.client.ChangedClient;
import net.minecraft.client.renderer.ShaderInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderSystem.class)
public class RenderSystemMixin {
    @Inject(method = "setupShaderLights", at = @At("HEAD")) // This function is always called after minecraft sets its own uniforms
    private static void additionalUniforms(ShaderInstance shader, CallbackInfo ci) {
        if (!ChangedClient.isRenderingWaveVision()) return;

        Uniform waveEffectUniform = shader.getUniform("WaveEffect");
        if (waveEffectUniform != null) {
            waveEffectUniform.set(ChangedClient.getWaveEffect());
        }

        if (ChangedClient.getWaveRenderPhase() != ChangedClient.WaveVisionRenderPhase.TERRAIN) return;

        Uniform waveResonanceUniform = shader.getUniform("WaveResonance");
        if (waveResonanceUniform != null) {
            waveResonanceUniform.set(ChangedClient.getWaveResonance());
        }
    }
}
