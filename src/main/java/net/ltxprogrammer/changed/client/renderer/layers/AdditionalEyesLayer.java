package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.entity.BasicPlayerInfo;
import net.ltxprogrammer.changed.entity.EyeStyle;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.Util;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class AdditionalEyesLayer<M extends LatexHumanoidModel<T>, T extends LatexEntity> extends RenderLayer<T, M> {
    public static final ModelLayerLocation HEAD = CustomEyesLayer.HEAD;
    private final ModelPart head;
    private final ResourceLocation additionalEyesId;
    private final CustomEyesLayer.ColorFunction<T> eyesColorFn;
    private final Map<EyeStyle, ResourceLocation> eyesTextures = new HashMap<>();

    private ResourceLocation getEyesTexture(EyeStyle style) {
        return new ResourceLocation(additionalEyesId.getNamespace(), "textures/eyes/" + additionalEyesId.getPath() + "/" + style.getId().getNamespace() + "/" + style.getId().getPath() + ".png");
    }

    public AdditionalEyesLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet, ResourceLocation additionalEyesId) {
        super(parent);
        this.head = modelSet.bakeLayer(HEAD);
        this.eyesColorFn = CustomEyesLayer.fixedColor(Color3.WHITE);
        this.additionalEyesId = additionalEyesId;
    }

    public AdditionalEyesLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet, ResourceLocation additionalEyesId, CustomEyesLayer.ColorFunction<T> eyesColorFn) {
        super(parent);
        this.head = modelSet.bakeLayer(HEAD);
        this.eyesColorFn = eyesColorFn;
        this.additionalEyesId = additionalEyesId;
    }

    private void renderHead(PoseStack pose, VertexConsumer buffer, int packedLight, int overlay, Color3 color, float alpha) {
        head.render(pose, buffer, packedLight, overlay, color.red(), color.green(), color.blue(), alpha);
    }

    @Override
    public void render(PoseStack pose, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible())
            return;

        BasicPlayerInfo info = new BasicPlayerInfo();
        info.copyFrom(entity.getBasicPlayerInfo());
        if (Changed.config.client.basicPlayerInfo.isOverrideOthersToMatchStyle())
            info.setEyeStyle(Changed.config.client.basicPlayerInfo.getEyeStyle());

        int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);

        head.copyFrom(this.getParentModel().getHead());
        eyesColorFn.getColorSafe(entity, info).ifPresent(data -> {
            renderHead(pose, bufferSource.getBuffer(data.getRenderType(eyesTextures.computeIfAbsent(info.getEyeStyle(), this::getEyesTexture))), packedLight, overlay, data.color, data.alpha);
        });
    }
}