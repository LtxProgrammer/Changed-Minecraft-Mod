package net.ltxprogrammer.changed.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.Pillow;
import net.ltxprogrammer.changed.block.entity.PillowBlockEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.DyeColor;

import java.util.Arrays;
import java.util.Comparator;

public class PillowRenderer implements BlockEntityRenderer<PillowBlockEntity> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("pillow"), "main");
    public static final RenderType[] RENDER_TYPES = Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map((color) -> {
        return RenderType.entitySolid(Changed.modResource("textures/blocks/pillow/" + color.getName() + ".png"));
    }).toArray(RenderType[]::new);
    private final ModelPart pillow;

    public PillowRenderer(BlockEntityRendererProvider.Context context) {
        this.pillow = context.bakeLayer(LAYER_LOCATION);
    }
    
    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("root", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 13.0F, 5.0F, 13.0F, CubeDeformation.NONE), PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 64, 32);
    }
    
    @Override
    public void render(PillowBlockEntity entity, float partialTicks, PoseStack stack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        stack.pushPose();
        stack.translate(0.5D, 0.0D, 0.5D);
        stack.mulPose(Vector3f.YP.rotationDegrees(180.0F + Pillow.getRot16ForState(entity.getBlockState()) * 22.5F));
        stack.translate(-0.5D + (1.5D / 16.0D), 0.0D, -0.5D + (1.5D / 16.0D));
        VertexConsumer vertexconsumer = bufferSource.getBuffer(RENDER_TYPES[entity.getColor().getId()]);
        pillow.render(stack, vertexconsumer, packedLight, packedOverlay);
        stack.popPose();
    }
}
