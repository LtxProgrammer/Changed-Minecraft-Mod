package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.renderer.model.CorrectorType;
import net.ltxprogrammer.changed.client.renderer.model.HairModel;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModelInterface;
import net.ltxprogrammer.changed.client.renderer.model.hair.HairRemodel;
import net.ltxprogrammer.changed.entity.HairStyle;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

public class LatexHumanoidHairLayer<T extends LatexEntity, M extends LatexHumanoidModel<T>> extends RenderLayer<T, M> {
    private final HairModel modelUpper;
    private final HairModel modelLower;

    public LatexHumanoidHairLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
        super(parent);
        modelUpper = HairRemodel.RIG_UPPER.apply(modelSet);
        modelLower = HairRemodel.RIG_LOWER.apply(modelSet);
    }

    public void render(PoseStack pose, MultiBufferSource bufferSource, int packedLight, T entity, float p_116670_, float p_116671_, float red, float green, float blue, float alpha) {
        if (entity.isInvisible())
            return;
        HairStyle style = entity.getHairStyle();
        if (style.textureLayers.isEmpty())
            return;

        ModelPart head = this.getParentModel().getHead();

        pose.pushPose();
        pose.translate(head.x / 16.0F, head.y / 16.0F, head.z / 16.0F);
        if (this.getParentModel() instanceof LatexHumanoidModelInterface modelInterface)
            pose.mulPoseMatrix(modelInterface.getPlacementCorrectors(CorrectorType.LOWER_HAIR).last().pose());
        int colorLayer = 0;
        int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);
        for (ResourceLocation layer : style.textureLayers) {
            Color3 color = entity.getHairColor(colorLayer);
            VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(layer));
            modelLower.renderToBuffer(pose, buffer, packedLight,
                    overlay, color.red(), color.green(), color.blue(), alpha);
            ++colorLayer;
        }
        pose.popPose();
        pose.pushPose();
        head.translateAndRotate(pose);
        if (this.getParentModel() instanceof LatexHumanoidModelInterface modelInterface)
            pose.mulPoseMatrix(modelInterface.getPlacementCorrectors(CorrectorType.HAIR).last().pose());
        for (ResourceLocation layer : style.textureLayers) {
            Color3 color = entity.getHairColor(colorLayer);
            VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(layer));
            modelUpper.renderToBuffer(pose, buffer, packedLight,
                    overlay, color.red(), color.green(), color.blue(), alpha);
            ++colorLayer;
        }
        pose.popPose();
    }
}
