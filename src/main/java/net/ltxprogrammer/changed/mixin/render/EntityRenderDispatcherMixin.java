package net.ltxprogrammer.changed.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.EntityRenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
}
