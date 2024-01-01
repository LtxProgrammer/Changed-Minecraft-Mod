package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class LatexTranslucentLayer<T extends LatexEntity, M extends LatexHumanoidModel<T>> extends RenderLayer<T, M> {
    private final LatexHumanoidModel<T> model;
    private final ResourceLocation texture;

    public LatexTranslucentLayer(RenderLayerParent<T, M> p_174536_, M model, ResourceLocation texture) {
        super(p_174536_);
        this.model = model;
        this.texture = texture;
    }

    public void render(PoseStack p_117470_, MultiBufferSource p_117471_, int p_117472_, T entity, float p_117474_, float p_117475_, float p_117476_, float p_117477_, float p_117478_, float p_117479_) {
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = minecraft.shouldEntityAppearGlowing(entity) && entity.isInvisible();
        if (!entity.isInvisible() || flag) {
            VertexConsumer vertexconsumer;
            if (flag) {
                vertexconsumer = p_117471_.getBuffer(RenderType.outline(texture));
            } else {
                vertexconsumer = p_117471_.getBuffer(RenderType.entityTranslucent(texture));
            }

            this.model.renderToBuffer(p_117470_, vertexconsumer, p_117472_, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public LatexHumanoidModel<T> getModel() {
        return model;
    }

    public ResourceLocation getTexture() {
        return texture;
    }
}
