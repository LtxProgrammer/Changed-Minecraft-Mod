package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.FormRenderHandler;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.minecraft.client.Camera;
import net.minecraft.client.model.ArmedModel;
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
        if (this.getParentModel() instanceof LatexHumanoidModel<?> armedModel)
            FormRenderHandler.renderModelPartWithTexture(armedModel.getArm(arm), stackCorrector, stack, bufferSource.getBuffer(this.renderType()), LightTexture.FULL_BRIGHT, 1F);
    }
}