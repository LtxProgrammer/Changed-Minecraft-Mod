package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.FormRenderHandler;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;

public class CustomCoatLayer<M extends AdvancedHumanoidModel<T>, T extends ChangedEntity> extends RenderLayer<T, M> implements FirstPersonLayer<T> {
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
        if (entity.isInvisible())
            return;

        var info = entity.getBasicPlayerInfo();
        var coatColor = info.getHairColor();

        int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
        model.renderToBuffer(pose, bufferSource.getBuffer(getRenderTypeForColor(coatColor)), packedLight, overlay, coatColor.red(), coatColor.green(), coatColor.blue(), 1.0F);
    }

    // Entity doesn't have to be the correct type syntactically, but expect bugs if the wrong renderer is used
    public RenderType getRenderTypeForColor(Color3 color) {
        return color.brightness() < 0.5f ? renderTypeDark : renderTypeLight;
    }

    @Override
    public void renderFirstPersonOnArms(PoseStack stack, MultiBufferSource bufferSource, int packedLight, T entity, HumanoidArm arm, PoseStack stackCorrector) {
        var info = entity.getBasicPlayerInfo();
        var coatColor = info.getHairColor();
        stack.pushPose();
        stack.scale(ZFIGHT_OFFSET, ZFIGHT_OFFSET, ZFIGHT_OFFSET);
        FormRenderHandler.renderModelPartWithTexture(model.getArm(arm), stackCorrector, stack, bufferSource.getBuffer(this.getRenderTypeForColor(coatColor)), packedLight,
                coatColor.red(), coatColor.green(), coatColor.blue(), 1F);
        stack.popPose();
    }
}