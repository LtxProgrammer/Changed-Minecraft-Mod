package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.CorrectorType;
import net.ltxprogrammer.changed.client.renderer.model.HairModel;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModelInterface;
import net.ltxprogrammer.changed.client.renderer.model.hair.HairRemodel;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LatexHumanoidHairLayer<T extends LatexEntity, M extends LatexHumanoidModel<T>> extends RenderLayer<T, M> {
    private static final Map<ModelLayerLocation, HairModel> MODEL_BY_LOCATION = new HashMap<>();

    public LatexHumanoidHairLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        super(parent);
        Arrays.stream(HairStyle.values()).filter(style -> !MODEL_BY_LOCATION.containsKey(style)).forEach(style -> {
            try {
                if (style.headHair != null)
                    MODEL_BY_LOCATION.computeIfAbsent(style.headHair.get(), location -> new HairModel(modelSet.bakeLayer(location)));
                if (style.lowerHair != null)
                    MODEL_BY_LOCATION.computeIfAbsent(style.lowerHair.get(), location -> new HairModel(modelSet.bakeLayer(location)));

                var override = HairRemodel.getOverride(style);
                if (override != null) {
                    if (override.head() != null)
                        MODEL_BY_LOCATION.computeIfAbsent(override.head(), location -> new HairModel(modelSet.bakeLayer(location)));
                    if (override.lower() != null)
                        MODEL_BY_LOCATION.computeIfAbsent(override.lower(), location -> new HairModel(modelSet.bakeLayer(location)));
                }
            } catch (Exception ex) {
                Changed.LOGGER.error("Failed to load HairStyle model for {}", style.getSerializedName(), ex);
            }
        });
    }

    public void render(PoseStack pose, MultiBufferSource bufferSource, int packedLight, T entity, float p_116670_, float p_116671_, float red, float green, float blue, float alpha) {
        HairStyle style = entity.getHairStyle();
        if (!style.hasModel() || style.textures.length == 0)
            return;
        if (entity.isInvisible())
            return;

        ModelPart head = this.getParentModel().getHead();
        Model headHair = style.headHair != null ? MODEL_BY_LOCATION.get(style.headHair.get()) : null;
        Model lowerHair = style.lowerHair != null ? MODEL_BY_LOCATION.get(style.lowerHair.get()) : null;
        ResourceLocation[] textures = style.textures;

        if (Changed.config.client.useNewModels.get()) {
            var override = HairRemodel.getOverride(style);
            if (override == null)
                return;

            headHair = override.head() != null ? MODEL_BY_LOCATION.get(override.head()) : null;
            lowerHair = override.lower() != null ? MODEL_BY_LOCATION.get(override.lower()) : null;
            textures = override.textures();
            if (textures.length == 0)
                return;
        }

        pose.pushPose();
        pose.translate(head.x / 16.0F, head.y / 16.0F, head.z / 16.0F);
        if (this.getParentModel() instanceof LatexHumanoidModelInterface modelInterface)
            pose.mulPoseMatrix(modelInterface.getPlacementCorrectors(CorrectorType.LOWER_HAIR).last().pose());
        int colorLayer = 0;
        int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
        if (lowerHair != null) {
            for (ResourceLocation layer : textures) {
                ChangedParticles.Color3 color = entity.getHairColor(colorLayer);
                VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(layer));
                lowerHair.renderToBuffer(pose, buffer, packedLight,
                        overlay, color.red(), color.green(), color.blue(), alpha);
                ++colorLayer;
            }
        }
        pose.popPose();
        pose.pushPose();
        head.translateAndRotate(pose);
        if (this.getParentModel() instanceof LatexHumanoidModelInterface modelInterface)
            pose.mulPoseMatrix(modelInterface.getPlacementCorrectors(CorrectorType.HAIR).last().pose());
        if (headHair != null) {
            for (ResourceLocation layer : textures) {
                ChangedParticles.Color3 color = entity.getHairColor(colorLayer);
                VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(layer));
                headHair.renderToBuffer(pose, buffer, packedLight,
                        overlay, color.red(), color.green(), color.blue(), alpha);
                ++colorLayer;
            }
        }
        pose.popPose();
    }
}
