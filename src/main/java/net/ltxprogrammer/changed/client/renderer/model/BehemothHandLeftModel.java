package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.beast.boss.BehemothHandLeft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class BehemothHandLeftModel extends EntityModel<BehemothHandLeft> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("behemoth_hand_left"), "main");
    private final ModelPart Hand;

    public BehemothHandLeftModel(ModelPart root) {
        this.Hand = root.getChild("Hand");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Hand = partdefinition.addOrReplaceChild("Hand", CubeListBuilder.create().texOffs(0, 24).addBox(-9.0F, -10.0F, -4.75F, 18.0F, 10.0F, 9.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition cube_r1 = Hand.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 43).addBox(-10.0F, -15.0F, 3.5F, 20.0F, 15.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(54, 33).addBox(3.0F, -1.5F, -5.5F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(54, 35).addBox(-8.0F, -1.5F, -5.5F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(54, 40).addBox(-4.0F, -11.5F, -5.5F, 9.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(54, 38).addBox(-5.0F, -9.5F, -5.5F, 11.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(68, 20).addBox(-7.0F, -8.5F, -5.5F, 15.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(48, 0).addBox(-8.0F, -7.5F, -5.5F, 17.0F, 6.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 0).addBox(-10.0F, -14.0F, -4.5F, 20.0F, 16.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

        PartDefinition Pinkie = Hand.addOrReplaceChild("Pinkie", CubeListBuilder.create(), PartPose.offset(7.0F, -23.0F, 4.0F));

        PartDefinition cube_r2 = Pinkie.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(20, 59).addBox(-10.0F, -7.2143F, -2.9557F, 5.0F, 5.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(7.5F, -3.75F, 0.0F, 0.3491F, 0.0F, 0.0F));

        PartDefinition cube_r3 = Pinkie.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 59).addBox(4.0F, -8.2143F, -1.9557F, 6.0F, 10.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-7.0F, -3.25F, -0.5F, 0.3491F, 0.0F, 0.0F));

        PartDefinition cube_r4 = Pinkie.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(68, 7).addBox(-11.0F, -21.125F, -3.675F, 6.0F, 5.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(8.0F, 14.75F, -5.5F, -0.3491F, 0.0F, 0.0F));

        PartDefinition Middle = Hand.addOrReplaceChild("Middle", CubeListBuilder.create(), PartPose.offset(0.0F, -23.0F, 4.0F));

        PartDefinition cube_r5 = Middle.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(48, 61).addBox(-10.0F, -7.2143F, -2.9557F, 5.0F, 5.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(7.5F, -3.75F, 0.0F, 0.3491F, 0.0F, 0.0F));

        PartDefinition cube_r6 = Middle.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(28, 61).addBox(-3.0F, -8.2143F, -1.9557F, 6.0F, 10.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -3.25F, -0.5F, 0.3491F, 0.0F, 0.0F));

        PartDefinition cube_r7 = Middle.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(69, 25).addBox(-11.0F, -21.125F, -3.7F, 6.0F, 5.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(8.0F, 14.75F, -5.5F, -0.3491F, 0.0F, 0.0F));

        PartDefinition Index = Hand.addOrReplaceChild("Index", CubeListBuilder.create(), PartPose.offset(-7.0F, -23.0F, 4.0F));

        PartDefinition cube_r8 = Index.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(76, 61).addBox(-10.0F, -7.2143F, -2.9557F, 5.0F, 5.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(7.5F, -3.75F, 0.0F, 0.3491F, 0.0F, 0.0F));

        PartDefinition cube_r9 = Index.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(56, 61).addBox(-10.0F, -8.2143F, -1.9557F, 6.0F, 10.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(7.0F, -3.25F, -0.5F, 0.3491F, 0.0F, 0.0F));

        PartDefinition cube_r10 = Index.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(73, 38).addBox(-10.0F, -21.125F, -3.7F, 6.0F, 5.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(7.0F, 14.75F, -5.5F, -0.3491F, 0.0F, 0.0F));

        PartDefinition Thumb = Hand.addOrReplaceChild("Thumb", CubeListBuilder.create(), PartPose.offset(-10.0F, -16.0F, -1.0F));

        PartDefinition cube_r11 = Thumb.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(56, 7).addBox(-5.0F, -3.3015F, -5.0399F, 7.0F, 7.0F, 1.0F, new CubeDeformation(-0.15F))
                .texOffs(42, 43).addBox(-6.0F, -4.3015F, -4.5399F, 11.0F, 9.0F, 9.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(-5.75F, 0.925F, 0.75F, -0.4301F, -0.609F, 0.2567F));

        PartDefinition cube_r12 = Thumb.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(47, 15).addBox(-5.0F, -4.3015F, -2.7899F, 6.0F, 9.0F, 9.0F, new CubeDeformation(-0.45F)), PartPose.offsetAndRotation(0.0F, 1.0F, 1.0F, -0.3491F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(BehemothHandLeft entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Hand.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
