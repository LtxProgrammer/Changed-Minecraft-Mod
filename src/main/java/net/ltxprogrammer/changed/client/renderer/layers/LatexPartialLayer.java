package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.FormRenderHandler;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;

import java.util.function.Function;

public class LatexPartialLayer<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends RenderLayer<T, M> implements FirstPersonLayer<T> {
    private final M model;
    private final Function<T, ResourceLocation> textureFunction;

    public LatexPartialLayer(RenderLayerParent<T, M> parent, M model, ResourceLocation texture) {
        this(parent, model, entity -> texture);
    }

    public LatexPartialLayer(RenderLayerParent<T, M> parent, M model, Function<T, ResourceLocation> textureFunction) {
        super(parent);
        this.model = model;
        this.textureFunction = textureFunction;
    }

    public void render(PoseStack pose, MultiBufferSource bufferSource, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        Minecraft minecraft = Minecraft.getInstance();
        boolean flag = minecraft.shouldEntityAppearGlowing(entity) && entity.isInvisible();
        if (!entity.isInvisible() || flag) {
            VertexConsumer vertexconsumer;
            if (flag) {
                vertexconsumer = bufferSource.getBuffer(RenderType.outline(textureFunction.apply(entity)));
            } else {
                vertexconsumer = bufferSource.getBuffer(renderType(entity));
            }

            this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
            this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

            // Ensure limbs line up so modded animations copy over.
            this.model.getAnimator(entity).applyPropertyModel(this.getParentModel().preparePropertyModel(entity));
            this.model.renderToBuffer(pose, vertexconsumer, packedLight, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public AdvancedHumanoidModel<T> getModel() {
        return model;
    }

    public Function<T, ResourceLocation> getTextureFunction() {
        return textureFunction;
    }

    public RenderType renderType(T entity) {
        return RenderType.entityCutoutNoCull(textureFunction.apply(entity));
    }

    public ModelPart getArm(HumanoidArm arm) {
        return model.getArm(arm);
    }

    @Override
    public void renderFirstPersonOnArms(PoseStack stack, MultiBufferSource bufferSource, int packedLight, T entity, HumanoidArm arm, PartPose armPose, float partialTick) {
        this.getModel().setupAnim(entity, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        model.setupHand(entity);
        stack.pushPose();
        stack.scale(ZFIGHT_OFFSET, ZFIGHT_OFFSET, ZFIGHT_OFFSET);
        var armPart = model.getArm(arm);
        armPart.loadPose(armPose);
        FormRenderHandler.renderModelPartWithTexture(armPart, stack, bufferSource.getBuffer(this.renderType(entity)), packedLight, 1F);
        stack.popPose();
    }
}
