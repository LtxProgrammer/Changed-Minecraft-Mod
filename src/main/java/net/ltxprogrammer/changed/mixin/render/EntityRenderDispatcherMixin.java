package net.ltxprogrammer.changed.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.EntityRenderHelper;
import net.ltxprogrammer.changed.init.ChangedEntityRenderers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
    @Inject(method = "render", at = @At("HEAD"))
    public <E extends Entity> void beforeRender(E entity, double camX, double camY, double camZ, float p_114389_, float p_114390_, PoseStack poseStack, MultiBufferSource buffers, int p_114393_, CallbackInfo callback) {
        EntityRenderHelper.ENTITY_RENDER_DISPATCHER_ENTITY_MINUS_CAMERA.addLast(new Vec3(camX, camY, camZ));
    }

    @Inject(method = "render", at = @At("RETURN"))
    public <E extends Entity> void afterRender(E entity, double camX, double camY, double camZ, float p_114389_, float p_114390_, PoseStack poseStack, MultiBufferSource buffers, int p_114393_, CallbackInfo callback) {
        EntityRenderHelper.ENTITY_RENDER_DISPATCHER_ENTITY_MINUS_CAMERA.removeLast();
    }

    @Inject(method = "getRenderer", at = @At("HEAD"), cancellable = true)
    public <E extends Entity> void getRendererOrOverridden(E entity, CallbackInfoReturnable<EntityRenderer<? super E>> callback) {
        EntityRenderer<? super E> override = ChangedEntityRenderers.getRenderer(entity);
        if (override == null) return;
        callback.setReturnValue(override);
    }

    @Redirect(method = "onResourceManagerReload", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderers;createEntityRenderers(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;)Ljava/util/Map;"))
    public Map<EntityType<?>, EntityRenderer<?>> reloadChangedRenderers(EntityRendererProvider.Context context) {
        ChangedEntityRenderers.registerComplexRenderers(context);
        return EntityRenderers.createEntityRenderers(context);
    }
}
