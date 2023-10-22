package net.ltxprogrammer.changed.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.ChangedClient;
import net.minecraft.client.Camera;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(value = ParticleEngine.class, remap = false)
public abstract class ParticleEngineMixin {
    @Inject(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;Lnet/minecraft/client/renderer/LightTexture;Lnet/minecraft/client/Camera;FLnet/minecraft/client/renderer/culling/Frustum;)V", at = @At("RETURN"))
    public void render(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, LightTexture lightTexture, Camera camera, float partialTicks, @Nullable Frustum clippingHelper, CallbackInfo callback) {
        ChangedClient.particleSystem.render(poseStack, bufferSource, lightTexture, camera, partialTicks, clippingHelper);
    }

    @Inject(method = "reload", at = @At("RETURN"))
    public void reload(PreparableReloadListener.PreparationBarrier prepBarrier, ResourceManager resourceManager,
                       ProfilerFiller profilerA, ProfilerFiller profilerB, Executor execA, Executor execB, CallbackInfoReturnable<CompletableFuture<Void>> callback) {
        ChangedClient.particleSystem.reload(callback.getReturnValue(), prepBarrier, resourceManager, profilerA, profilerB, execA, execB);
    }

    @Inject(method = "close", at = @At("RETURN"))
    public void close(CallbackInfo ci) {
        ChangedClient.particleSystem.close();
    }
}
