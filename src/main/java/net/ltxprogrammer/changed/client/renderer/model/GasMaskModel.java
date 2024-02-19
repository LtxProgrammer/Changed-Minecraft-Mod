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
    private final ModelPart Root;

    public GasMaskModel(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.Root = root.getChild("Root");
    }

    public static LayerDefinition createMask() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Root = partdefinition.addOrReplaceChild("Root", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, -3.0F, -5.5F, 4.0F, 3.0F, 2.0F, new CubeDeformation(0.05F))
                .texOffs(14, 23).addBox(-3.0F, -6.0F, -5.0F, 6.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 22).addBox(-3.0F, -5.0F, 4.0F, 6.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(13, 3).addBox(3.0F, -3.75F, 3.75F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(13, 0).addBox(-4.0F, -3.75F, 3.75F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = Root.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(24, 5).addBox(-1.0F, -3.0F, -2.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(-2.0F, 0.0F, -4.0F, 0.0F, 0.8727F, 0.0F));

        PartDefinition cube_r2 = Root.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(24, 12).addBox(-2.0F, -3.0F, -2.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(2.0F, 0.0F, -4.0F, 0.0F, -0.8727F, 0.0F));

        PartDefinition BottomStraps = Root.addOrReplaceChild("BottomStraps", CubeListBuilder.create().texOffs(13, 12).addBox(-4.5F, 0.0F, -0.5F, 1.0F, 2.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(13, 1).addBox(3.5F, 0.0F, -0.5F, 1.0F, 2.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.0F, -4.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition StrapRight = Root.addOrReplaceChild("StrapRight", CubeListBuilder.create().texOffs(0, 10).addBox(-2.0F, -3.0F, -4.5F, 2.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(0, 13).addBox(-2.0F, -2.0F, -4.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-2.0F, -2.0F, 3.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -6.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

        PartDefinition StrapLeft = Root.addOrReplaceChild("StrapLeft", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -3.0F, -4.5F, 2.0F, 1.0F, 9.0F, new CubeDeformation(0.0F))
                .texOffs(0, 10).addBox(0.0F, -2.0F, -4.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 4).addBox(0.0F, -2.0F, 3.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -6.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(-6, -6).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

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