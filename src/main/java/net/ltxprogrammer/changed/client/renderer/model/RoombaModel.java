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
    private final ModelPart RightSweeper;
    private final ModelPart LeftSweeper;
    private final ModelPart Root;

    public RoombaModel(ModelPart root) {
        this.Root = root.getChild("Root");
        this.RightSweeper = Root.getChild("RightSweeper");
        this.LeftSweeper = Root.getChild("LeftSweeper");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Root = partdefinition.addOrReplaceChild("Root", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -2.2F, -5.0F, 8.0F, 2.0F, 10.0F, CubeDeformation.NONE)
                .texOffs(0, 16).addBox(2.5F, -1.0F, -1.5F, 1.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(10, 12).addBox(-3.5F, -1.0F, -1.5F, 1.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(10, 14).addBox(4.0F, -2.2F, -4.0F, 1.0F, 2.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(0, 12).addBox(-5.0F, -2.2F, -4.0F, 1.0F, 2.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition RightSweeper = Root.addOrReplaceChild("RightSweeper", CubeListBuilder.create().texOffs(2, 12).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 0.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-3.0F, -0.1F, -4.5F));

        PartDefinition cube_r1 = RightSweeper.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 12).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 0.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r2 = RightSweeper.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(4, 4).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 0.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition cube_r3 = RightSweeper.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(2, 4).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 0.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition LeftSweeper = Root.addOrReplaceChild("LeftSweeper", CubeListBuilder.create().texOffs(4, 0).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 0.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(3.0F, -0.1F, -4.5F));

        PartDefinition cube_r4 = LeftSweeper.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 0.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition cube_r5 = LeftSweeper.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(2, 0).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 0.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition cube_r6 = LeftSweeper.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 4).addBox(-0.5F, 0.0F, -2.0F, 1.0F, 0.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.025F, 0.0F, 0.0F, -0.7854F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(Roomba entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.getCharge() > 0f) {
            RightSweeper.yRot = -ageInTicks;
            LeftSweeper.yRot = ageInTicks;
        } else {
            RightSweeper.yRot = 0f;
            LeftSweeper.yRot = 0f;
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
