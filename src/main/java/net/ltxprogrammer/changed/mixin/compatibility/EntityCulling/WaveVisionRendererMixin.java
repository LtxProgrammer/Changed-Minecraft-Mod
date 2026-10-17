package net.ltxprogrammer.changed.mixin.compatibility.EntityCulling;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.tr7zw.entityculling.EntityCullingModBase;
import dev.tr7zw.entityculling.NMSCullingHelper;
import dev.tr7zw.entityculling.access.EntityRendererInter;
import net.ltxprogrammer.changed.client.WaveVisionRenderer;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WaveVisionRenderer.class, remap = false)
@RequiredMods("entityculling")
public abstract class WaveVisionRendererMixin {
    @Shadow @Final private EntityRenderDispatcher entityRenderDispatcher;

    @Inject(
            at = {@At("HEAD")},
            method = {"renderEntity"},
            cancellable = true
    )
    private void renderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, CallbackInfo info) {
        if (!EntityCullingModBase.instance.config.skipEntityCulling) {
            dev.tr7zw.entityculling.access.Cullable cullable = (dev.tr7zw.entityculling.access.Cullable)entity;
            if (!cullable.isForcedVisible() && cullable.isCulled() && !NMSCullingHelper.ignoresCulling(entity)) {
                EntityRenderer renderer = this.entityRenderDispatcher.getRenderer(entity);
                if (renderer instanceof EntityRenderer) {
                    EntityRenderer entityRenderer = renderer;
                    if (renderer instanceof EntityRendererInter) {
                        EntityRendererInter entityRendererInter = (EntityRendererInter)renderer;
                        if (EntityCullingModBase.instance.config.renderNametagsThroughWalls && matrices != null && vertexConsumers != null && entityRendererInter.shadowShouldShowName(entity)) {
                            double x = Mth.lerp((double)tickDelta, entity.xOld, entity.getX()) - cameraX;
                            double y = Mth.lerp((double)tickDelta, entity.yOld, entity.getY()) - cameraY;
                            double z = Mth.lerp((double)tickDelta, entity.zOld, entity.getZ()) - cameraZ;
                            Vec3 vec3d = NMSCullingHelper.getRenderOffset(entityRenderer, entity, tickDelta);
                            double d = x + vec3d.x;
                            double e = y + vec3d.y;
                            double f = z + vec3d.z;
                            matrices.pushPose();
                            matrices.translate(d, e, f);
                            entityRendererInter.shadowRenderNameTag(entity, entity.getDisplayName(), matrices, vertexConsumers, this.entityRenderDispatcher.getPackedLightCoords(entity, tickDelta), tickDelta);
                            matrices.popPose();
                        }

                        ++EntityCullingModBase.instance.skippedEntities;
                        info.cancel();
                        return;
                    }
                }
            }

            ++EntityCullingModBase.instance.renderedEntities;
            cullable.setOutOfCamera(false);
        }
    }
}
