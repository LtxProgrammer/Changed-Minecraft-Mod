package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.FormRenderHandler;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class EmissiveBodyLayer<M extends EntityModel<T>, T extends LivingEntity> extends EyesLayer<T, M> implements FirstPersonLayer<T> {
    private final RenderType renderType;
    private final ResourceLocation emissiveTexture;

    public EmissiveBodyLayer(RenderLayerParent<T, M> p_116964_, ResourceLocation emissiveTexture) {
        super(p_116964_);
        this.renderType = RenderType.eyes(emissiveTexture);
        this.emissiveTexture = emissiveTexture;
    }

    public ResourceLocation getEmissiveTexture() {
        return emissiveTexture;
    }

    public RenderType renderType() {
        return renderType;
    }

    @Override
    public void renderFirstPersonOnArms(PoseStack stack, MultiBufferSource bufferSource, int packedLight, T entity, HumanoidArm arm, PoseStack stackCorrector) {
        stack.pushPose();
        stack.scale(ZFIGHT_OFFSET, ZFIGHT_OFFSET, ZFIGHT_OFFSET);
        if (this.getParentModel() instanceof AdvancedHumanoidModel<?> armedModel)
            FormRenderHandler.renderModelPartWithTexture(armedModel.getArm(arm), stackCorrector, stack, bufferSource.getBuffer(this.renderType()), LightTexture.FULL_BRIGHT, 1F);
        stack.popPose();
    }
}