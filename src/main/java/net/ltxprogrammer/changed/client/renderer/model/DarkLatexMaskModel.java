package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class DarkLatexMaskModel extends Model {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("dark_latex_mask"), "main");
    private final ModelPart Mask;

    public DarkLatexMaskModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.Mask = root.getChild("Mask");
    }

    public static LayerDefinition createMask() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Mask = partdefinition.addOrReplaceChild("Mask", CubeListBuilder.create().texOffs(0, 10).addBox(-1.0F, -4.0F, -1.0F, 2.0F, 5.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(13, 7).addBox(1.0F, -3.0F, -1.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(10, 11).addBox(-2.0F, -3.0F, -1.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(13, 13).addBox(-3.0F, -2.0F, -1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(13, 10).addBox(2.0F, -2.0F, -1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(6, 11).addBox(3.0F, -1.0F, -1.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(10, 5).addBox(-4.0F, -1.0F, -1.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(13, 4).addBox(-3.0F, 1.0F, -1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(13, 0).addBox(2.0F, 1.0F, -1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(10, 2).addBox(2.0F, 2.0F, -1.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(7, 0).addBox(-4.0F, 2.0F, -1.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(5, 6).addBox(-2.0F, 1.0F, -3.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.001F))
                .texOffs(0, 4).addBox(1.0F, 1.0F, -3.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.001F))
                .texOffs(0, 0).addBox(-1.0F, 1.0F, -3.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.001F))
                .texOffs(5, 4).addBox(-1.0F, 2.0F, -3.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offset(0.0F, 21.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Mask.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public ResourceLocation getTexture() {
        return Changed.modResource("textures/items/3d/dark_latex_mask.png");
    }
}