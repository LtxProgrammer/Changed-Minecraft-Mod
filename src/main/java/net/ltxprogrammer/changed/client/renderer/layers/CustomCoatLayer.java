package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class CustomCoatLayer<M extends LatexHumanoidModel<T>, T extends LatexEntity> extends RenderLayer<T, M> {
    private final M model;
    private final RenderType renderTypeDark;
    private final RenderType renderTypeLight;

    public CustomCoatLayer(RenderLayerParent<T, M> parent, M model, ResourceLocation textureBase) {
        super(parent);
        this.model = model;
        this.renderTypeDark = RenderType.entityCutout(new ResourceLocation(textureBase.getNamespace(), textureBase.getPath() + "_dark.png"));
        this.renderTypeLight = RenderType.entityCutout(new ResourceLocation(textureBase.getNamespace(), textureBase.getPath() + "_light.png"));
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        var info = entity.getBasicPlayerInfo();
        var coatColor = info.getHairColor();

        int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
        model.renderToBuffer(pose, bufferSource.getBuffer(coatColor.brightness() < 0.5f ? renderTypeDark : renderTypeLight), packedLight, overlay, coatColor.red(), coatColor.green(), coatColor.blue(), 1.0F);
    }
}