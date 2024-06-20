package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.FormRenderHandler;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModelInterface;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.ChangedCompatibility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;

public class LatexPartialLayer<T extends ChangedEntity, M extends AdvancedHumanoidModel<T> & AdvancedHumanoidModelInterface<T, M>> extends RenderLayer<T, M> implements FirstPersonLayer<T> {
    private final M model;
    private final ResourceLocation texture;

    public LatexPartialLayer(RenderLayerParent<T, M> p_174536_, M model, ResourceLocation texture) {
        super(p_174536_);
        this.model = model;
        this.texture = texture;
    }

    public void prepareMobModel(T entity, float partialTicks) {
        if (ChangedCompatibility.isFirstPersonRendering()) {
            model.getHead().visible = false;
            model.getTorso().visible = !entity.isVisuallySwimming();
        }

        else {
            model.getHead().visible = true;
            model.getTorso().visible = true;
        }
    }

    public void render(PoseStack pose, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = minecraft.shouldEntityAppearGlowing(entity) && entity.isInvisible();
        if (!entity.isInvisible() || flag) {
            VertexConsumer vertexconsumer;
            if (flag) {
                vertexconsumer = bufferSource.getBuffer(RenderType.outline(texture));
            } else {
                vertexconsumer = bufferSource.getBuffer(renderType());
            }

            this.prepareMobModel(entity, partialTicks);
            this.model.getAnimator().setupVariables(entity, partialTicks);
            this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
            this.model.renderToBuffer(pose, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public AdvancedHumanoidModel<T> getModel() {
        return model;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public RenderType renderType() {
        return RenderType.entityCutoutNoCull(texture);
    }

    public ModelPart getArm(HumanoidArm arm) {
        return model.getArm(arm);
    }

    @Override
    public void renderFirstPersonOnArms(PoseStack stack, MultiBufferSource bufferSource, int packedLight, T entity, HumanoidArm arm, PoseStack stackCorrector) {
        this.getModel().setupAnim(entity, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        model.setupHand();
        stack.pushPose();
        stack.scale(ZFIGHT_OFFSET, ZFIGHT_OFFSET, ZFIGHT_OFFSET);
        FormRenderHandler.renderModelPartWithTexture(this.getArm(arm), stackCorrector, stack, bufferSource.getBuffer(this.renderType()), packedLight, 1F);
        stack.popPose();
    }
}
