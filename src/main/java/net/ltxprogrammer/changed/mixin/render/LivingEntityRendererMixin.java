package net.ltxprogrammer.changed.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.LivingEntityRendererExtender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, M extends EntityModel<T>> implements LivingEntityRendererExtender<T, M> {
    @Shadow protected abstract boolean isBodyVisible(T p_115341_);

    @Shadow public abstract M getModel();

    @Shadow @Final public List<RenderLayer<T, M>> layers;

    @Shadow @Nullable protected abstract RenderType getRenderType(T p_115322_, boolean p_115323_, boolean p_115324_, boolean p_115325_);

    @Shadow protected abstract float getWhiteOverlayProgress(T p_115334_, float p_115335_);

    @Shadow protected M model;

    @Shadow protected abstract float getAttackAnim(T p_115343_, float p_115344_);

    @Override
    public void directRender(T entity, float yRot, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        this.model.attackTime = this.getAttackAnim(entity, partialTicks);
        this.model.riding = false;
        this.model.young = entity.isBaby();
        this.model.setupAnim(entity, 0.0f, 0.0f, entity.tickCount + partialTicks, 0.0f, 0.0f);

        boolean bodyVisible = this.isBodyVisible(entity);
        boolean shouldBeVisible = !bodyVisible && !entity.isInvisibleTo(Minecraft.getInstance().player);
        boolean shouldGlow = Minecraft.getInstance().shouldEntityAppearGlowing(entity);
        var renderType = this.getRenderType(entity, bodyVisible, shouldBeVisible, shouldGlow);
        if (renderType != null) {
            int overlay = LivingEntityRenderer.getOverlayCoords(entity, this.getWhiteOverlayProgress(entity, partialTicks));
            this.model.renderToBuffer(poseStack, bufferSource.getBuffer(renderType), packedLight, overlay, 1.0f, 1.0f, 1.0f, 1.0f);
        }

        for (var layer : this.layers) {
            layer.render(poseStack, bufferSource, packedLight, entity, 0.0f, 0.0f, partialTicks, 0.0f, 0.0f, 0.0f);
        }
    }
}
