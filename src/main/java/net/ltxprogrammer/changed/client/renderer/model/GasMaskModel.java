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

public class GasMaskModel extends Model {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("gas_mask"), "main");
    public static final ModelLayerLocation LAYER_LOCATION_SNOUTED = new ModelLayerLocation(Changed.modResource("gas_mask_snouted"), "main");
    public static final ModelLayerLocation LAYER_LOCATION_LARGE_SNOUTED = new ModelLayerLocation(Changed.modResource("gas_mask_large_snouted"), "main");
    private final ModelPart Root;

    public GasMaskModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.Root = root.getChild("Root");
    }

    public static LayerDefinition createMask() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        
        PartDefinition Root = partdefinition.addOrReplaceChild("Root", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, -3.0F, -5.5F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.05F))
                .texOffs(14, 23).addBox(-3.0F, -6.0F, -5.0F, 6.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 22).addBox(-3.0F, -5.0F, 4.0F, 6.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = Root.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(24, 5).addBox(-1.0F, -3.0F, -2.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(-2.0F, 0.0F, -4.0F, 0.0F, 0.8727F, 0.0F));

        PartDefinition cube_r2 = Root.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(24, 12).addBox(-2.0F, -3.0F, -2.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(2.0F, 0.0F, -4.0F, 0.0F, -0.8727F, 0.0F));

        PartDefinition BottomStraps = Root.addOrReplaceChild("BottomStraps", CubeListBuilder.create().texOffs(13, 12).addBox(-4.5F, 0.0F, -0.5F, 1.0F, 2.0F, 9.0F, CubeDeformation.NONE)
                .texOffs(13, 1).addBox(3.5F, 0.0F, -0.5F, 1.0F, 2.0F, 9.0F, CubeDeformation.NONE)
                .texOffs(13, 3).addBox(2.5F, 0.0F, 7.5F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(13, 0).addBox(-3.5F, 0.0F, 7.5F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -3.0F, -4.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition StrapRight = Root.addOrReplaceChild("StrapRight", CubeListBuilder.create().texOffs(0, 10).addBox(-2.0F, -3.0F, -4.5F, 2.0F, 1.0F, 9.0F, CubeDeformation.NONE)
                .texOffs(0, 13).addBox(-2.0F, -2.0F, -4.5F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 0).addBox(-2.0F, -2.0F, 3.5F, 2.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.0F, -6.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

        PartDefinition StrapLeft = Root.addOrReplaceChild("StrapLeft", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -3.0F, -4.5F, 2.0F, 1.0F, 9.0F, CubeDeformation.NONE)
                .texOffs(0, 10).addBox(0.0F, -2.0F, -4.5F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 4).addBox(0.0F, -2.0F, 3.5F, 2.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static LayerDefinition createMaskSnouted() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Root = partdefinition.addOrReplaceChild("Root", CubeListBuilder.create().texOffs(1, 28).addBox(-2.0F, -3.1F, -6.25F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.15F))
                .texOffs(14, 23).addBox(-3.0F, -6.0F, -5.0F, 6.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 22).addBox(-3.0F, -5.0F, 4.0F, 6.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = Root.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(24, 5).addBox(-1.0F, -3.0F, -2.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.175F)), PartPose.offsetAndRotation(-2.0F, -0.1F, -4.5F, 0.0F, 0.8727F, 0.0F));

        PartDefinition cube_r2 = Root.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(24, 12).addBox(-2.0F, -3.0F, -2.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.175F)), PartPose.offsetAndRotation(2.0F, -0.1F, -4.5F, 0.0F, -0.8727F, 0.0F));

        PartDefinition BottomStraps = Root.addOrReplaceChild("BottomStraps", CubeListBuilder.create().texOffs(13, 12).addBox(-4.5F, 0.0F, -0.5F, 1.0F, 2.0F, 9.0F, CubeDeformation.NONE)
                .texOffs(13, 1).addBox(3.5F, 0.0F, -0.5F, 1.0F, 2.0F, 9.0F, CubeDeformation.NONE)
                .texOffs(13, 3).addBox(2.5F, 0.0F, 7.5F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(13, 0).addBox(-3.5F, 0.0F, 7.5F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -3.0F, -4.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition StrapRight = Root.addOrReplaceChild("StrapRight", CubeListBuilder.create().texOffs(0, 10).addBox(-2.0F, -3.0F, -4.5F, 2.0F, 1.0F, 9.0F, CubeDeformation.NONE)
                .texOffs(0, 13).addBox(-2.0F, -2.0F, -4.5F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 0).addBox(-2.0F, -2.0F, 3.5F, 2.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.0F, -6.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

        PartDefinition StrapLeft = Root.addOrReplaceChild("StrapLeft", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -3.0F, -4.5F, 2.0F, 1.0F, 9.0F, CubeDeformation.NONE)
                .texOffs(0, 10).addBox(0.0F, -2.0F, -4.5F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 4).addBox(0.0F, -2.0F, 3.5F, 2.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static LayerDefinition createMaskLargeSnouted() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Root = partdefinition.addOrReplaceChild("Root", CubeListBuilder.create().texOffs(0, 27).addBox(-2.0F, -3.1F, -7.25F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.15F))
                .texOffs(14, 23).addBox(-3.0F, -6.0F, -5.0F, 6.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 22).addBox(-3.0F, -5.0F, 4.0F, 6.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = Root.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(24, 5).addBox(-1.0F, -3.0F, -2.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.175F)), PartPose.offsetAndRotation(-2.5F, -0.1F, -4.75F, 0.0F, 0.8727F, 0.0F));

        PartDefinition cube_r2 = Root.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(24, 12).addBox(-2.0F, -3.0F, -2.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.175F)), PartPose.offsetAndRotation(2.5F, -0.1F, -4.75F, 0.0F, -0.8727F, 0.0F));

        PartDefinition BottomStraps = Root.addOrReplaceChild("BottomStraps", CubeListBuilder.create().texOffs(13, 12).addBox(-4.5F, 0.0F, -0.5F, 1.0F, 2.0F, 9.0F, CubeDeformation.NONE)
                .texOffs(13, 1).addBox(3.5F, 0.0F, -0.5F, 1.0F, 2.0F, 9.0F, CubeDeformation.NONE)
                .texOffs(13, 3).addBox(2.5F, 0.0F, 7.5F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(13, 0).addBox(-3.5F, 0.0F, 7.5F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -3.0F, -4.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition StrapRight = Root.addOrReplaceChild("StrapRight", CubeListBuilder.create().texOffs(0, 10).addBox(-2.0F, -3.0F, -4.5F, 2.0F, 1.0F, 9.0F, CubeDeformation.NONE)
                .texOffs(0, 13).addBox(-2.0F, -2.0F, -4.5F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 0).addBox(-2.0F, -2.0F, 3.5F, 2.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.0F, -6.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

        PartDefinition StrapLeft = Root.addOrReplaceChild("StrapLeft", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -3.0F, -4.5F, 2.0F, 1.0F, 9.0F, CubeDeformation.NONE)
                .texOffs(0, 10).addBox(0.0F, -2.0F, -4.5F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 4).addBox(0.0F, -2.0F, 3.5F, 2.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    public ResourceLocation getTexture() {
        return Changed.modResource("textures/items/3d/gas_mask.png");
    }
}