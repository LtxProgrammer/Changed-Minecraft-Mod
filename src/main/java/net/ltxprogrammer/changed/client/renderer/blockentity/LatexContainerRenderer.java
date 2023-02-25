package net.ltxprogrammer.changed.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.entity.LatexContainerBlockEntity;
import net.ltxprogrammer.changed.entity.LatexType;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class LatexContainerRenderer<T extends LatexContainerBlockEntity> implements BlockEntityRenderer<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_fill"), "main");
    public static final ResourceLocation TEXTURE = Changed.modResource("textures/blocks/latex_fill.png");
    private static final RenderType renderType = RenderType.entityCutout(TEXTURE);
    private final ModelPart goo;

    public LatexContainerRenderer(BlockEntityRendererProvider.Context context) {
        this.goo = context.bakeLayer(LAYER_LOCATION);
    }

    public static LayerDefinition createLatexFill() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("LatexLiquidFill", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-2.0F, -12.0F, -2.0F, 4.0F, 16.0F, 4.0F, CubeDeformation.NONE),
                PartPose.offset(-8.0F, 16.0F, 8.0F));
        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void render(T blockEntity, float partialTicks, PoseStack pose, MultiBufferSource buffers, int packedLight, int packedOverlay) {
        if (blockEntity.getFillLevel() == 0 || blockEntity.getFillType() == LatexType.NEUTRAL)
            return;
        var color = blockEntity.getFillType().color;

        pose.pushPose();

        float fillPercent = (float)blockEntity.getFillLevel() / 16.0f;
        pose.translate(1, (1 - (fillPercent / 4.0f)) - (12.0f / 16.0f), 0);
        pose.scale(1, fillPercent, 1);
        goo.render(pose, buffers.getBuffer(renderType), packedLight, packedOverlay, color.red(), color.green(), color.blue(), 1);

        pose.popPose();
    }
}
