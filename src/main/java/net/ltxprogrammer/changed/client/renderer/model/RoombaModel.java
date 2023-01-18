package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.robot.Roomba;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class RoombaModel extends EntityModel<Roomba> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("roomba"), "main");
    private final ModelPart bone2;
    private final ModelPart bone3;
    private final ModelPart bb_main;

    public RoombaModel(ModelPart root) {
        this.bone2 = root.getChild("bone2");
        this.bone3 = root.getChild("bone3");
        this.bb_main = root.getChild("bb_main");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bone2 = partdefinition.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(0, 0).addBox(-5.5F, 0.0F, -3.0F, 1.0F, 0.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(7.5F, 24.0F, -3.0F));

        PartDefinition cube_r1 = bone2.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -3.0F, 1.0F, 0.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-5.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition cube_r2 = bone2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -3.0F, 1.0F, 0.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-5.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition cube_r3 = bone2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -3.0F, 1.0F, 0.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-5.0F, 0.0F, 0.0F, -3.1416F, 0.7854F, 3.1416F));

        PartDefinition cube_r4 = bone2.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -3.0F, 1.0F, 0.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-5.0F, 0.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        PartDefinition cube_r5 = bone2.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -3.0F, 1.0F, 0.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-5.0F, 0.0F, 0.0F, -3.1416F, -0.7854F, 3.1416F));

        PartDefinition cube_r6 = bone2.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -3.0F, 1.0F, 0.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-5.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r7 = bone2.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -3.0F, 1.0F, 0.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-5.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition bone3 = partdefinition.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -3.0F, 1.0F, 0.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.5F, 24.0F, -3.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition cube_r8 = bone3.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -3.0F, 1.0F, 0.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition cube_r9 = bone3.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -3.0F, 1.0F, 0.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition cube_r10 = bone3.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -3.0F, 1.0F, 0.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.1416F, 0.7854F, 3.1416F));

        PartDefinition cube_r11 = bone3.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -3.0F, 1.0F, 0.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.1416F, 0.0F, 3.1416F));

        PartDefinition cube_r12 = bone3.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -3.0F, 1.0F, 0.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -3.1416F, -0.7854F, 3.1416F));

        PartDefinition cube_r13 = bone3.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -3.0F, 1.0F, 0.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r14 = bone3.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -3.0F, 1.0F, 0.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -4.0F, 8.0F, 4.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(Roomba entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        bone2.yRot = (float) Math.toRadians(ageInTicks);
        bone3.yRot = (float) -Math.toRadians(ageInTicks);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        bone2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        bone3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        bb_main.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
