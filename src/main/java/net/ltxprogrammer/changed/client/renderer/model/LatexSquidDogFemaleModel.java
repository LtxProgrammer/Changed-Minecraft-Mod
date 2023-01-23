package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.beast.LatexSquidDogFemale;
import net.ltxprogrammer.changed.entity.beast.LatexSquidDogMale;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;

public class LatexSquidDogFemaleModel extends LatexHumanoidModel<LatexSquidDogFemale> implements LatexHumanoidModelInterface {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_squid_dog_female"), "main");
    public final ModelPart Head;
    public final ModelPart Torso;
    public final ModelPart LeftArm;
    public final ModelPart RightArm;
    public final ModelPart LeftArm2;
    public final ModelPart RightArm2;
    public final ModelPart LeftLeg;
    public final ModelPart RightLeg;
    public final LatexHumanoidModelController controller;

    public LatexSquidDogFemaleModel(ModelPart root) {
        super(root);
        this.RightLeg = root.getChild("rightLeg");
        this.LeftLeg = root.getChild("leftLeg");
        this.Head = root.getChild("head");
        this.Torso = root.getChild("body");
        this.RightArm = root.getChild("rightArm");
        this.RightArm2 = root.getChild("rightArm2");
        this.LeftArm = root.getChild("leftArm");
        this.LeftArm2 = root.getChild("leftArm2");
        this.controller = LatexHumanoidModelController.Builder.of(this, Head, Torso, Torso.getChild("Tail"), RightArm, LeftArm, RightLeg, LeftLeg).arms2(RightArm2, LeftArm2).hipOffset(-4.0f).build();
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition rightLeg = partdefinition.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(56, 10).addBox(-2.0F, 14.2F, -2.4F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(-2.45F, 7.5F, -0.65F));

        PartDefinition Toe_r1 = rightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(9, 75).addBox(-2.5F, -1.2F, -0.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(13, 75).addBox(-0.8F, -1.2F, -0.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(20, 77).addBox(-4.2F, -1.2F, -0.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(2.0F, 15.5F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r2 = rightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(37, 76).addBox(-0.8F, -3.1F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(77, 3).addBox(-2.5F, -3.1F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(77, 10).addBox(-4.2F, -3.1F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(2.0F, 18.5F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = rightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(18, 53).addBox(-1.99F, -8.6F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.0F, 16.75F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = rightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(46, 16).addBox(-2.02F, -1.1F, -0.75F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.0F, 6.75F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = rightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(38, 52).addBox(-2.05F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition leftLeg = partdefinition.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(70, 80).addBox(-2.0F, 14.2F, -2.4F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(2.55F, 7.5F, -0.65F));

        PartDefinition Toe_r3 = leftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(0, 93).addBox(-2.5F, -1.2F, -0.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(85, 75).addBox(-0.8F, -1.2F, -0.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(37, 79).addBox(-4.2F, -1.2F, -0.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(2.0F, 15.5F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r4 = leftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(83, 76).addBox(-0.8F, -3.1F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(85, 3).addBox(-2.5F, -3.1F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(85, 10).addBox(-4.2F, -3.1F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(2.0F, 18.5F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = leftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(18, 82).addBox(-2.01F, -8.6F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.0F, 16.75F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = leftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(46, 80).addBox(-1.99F, -1.1F, -0.75F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.0F, 6.75F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = leftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(0, 80).addBox(-1.98F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.0F, 2.5F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.25F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.4F))
                .texOffs(76, 25).addBox(-2.0F, -3.0F, -6.6F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.2F))
                .texOffs(0, 6).addBox(-1.5F, -1.05F, -5.6F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, -6.0F, 0.0F));

        PartDefinition RightEar = head.addOrReplaceChild("RightEar", CubeListBuilder.create().texOffs(59, 76).addBox(-4.5088F, 9.2159F, -20.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.2F))
                .texOffs(46, 29).addBox(-3.5088F, 7.8159F, -20.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.2F))
                .texOffs(32, 79).addBox(-2.5088F, 6.4159F, -20.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(-1.75F, -20.0F, 20.0F, 0.0F, 0.0F, -0.1309F));

        PartDefinition LeftEar = head.addOrReplaceChild("LeftEar", CubeListBuilder.create().texOffs(64, 37).addBox(1.5088F, 9.2159F, -20.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.2F))
                .texOffs(46, 27).addBox(1.5088F, 7.8159F, -20.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.2F))
                .texOffs(28, 79).addBox(1.5088F, 6.4159F, -20.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(1.75F, -20.0F, 20.0F, 0.0F, 0.0F, 0.1309F));

        PartDefinition Hair = head.addOrReplaceChild("Hair", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition Base_r1 = Hair.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(24, 26).addBox(-4.5F, -8.0F, -5.0F, 9.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(27, 22).addBox(-4.5F, -6.0F, -5.0F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(35, 36).addBox(-4.0F, -8.75F, 3.75F, 2.0F, 11.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(19, 37).addBox(2.0F, -8.75F, 3.75F, 2.0F, 11.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(56, 55).addBox(-2.0F, -9.0F, 4.0F, 4.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(17, 34).addBox(3.75F, -4.0F, 0.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(34, 34).addBox(-4.75F, -4.0F, 0.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(28, 33).addBox(-5.0F, -8.0F, -4.0F, 1.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(29, 33).addBox(4.0F, -8.0F, -4.0F, 1.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(28, 34).addBox(-4.0F, -8.75F, -4.0F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(28, 33).addBox(2.0F, -8.75F, -4.0F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(10, 22).addBox(-4.0F, -0.75F, 2.0F, 8.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(1, 16).addBox(-2.0F, -9.0F, -4.0F, 4.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(22, 22).addBox(-4.0F, 7.0F, -2.0F, 8.0F, 7.0F, 4.0F, new CubeDeformation(0.3F))
                .texOffs(0, 16).addBox(-4.5F, 0.4F, -2.0F, 9.0F, 6.0F, 4.0F, new CubeDeformation(0.3F))
                .texOffs(0, 73).addBox(-5.0F, 0.75F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.3F))
                .texOffs(68, 72).addBox(4.0F, 0.75F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.3F))
                .texOffs(40, 17).addBox(-2.0F, 2.4F, -3.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.099F))
                .texOffs(74, 72).addBox(-3.0F, 2.65F, 1.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.2F))
                .texOffs(25, 71).addBox(-3.0F, 0.3F, -2.9F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.2F))
                .texOffs(4, 21).addBox(-3.0F, 3.6F, -2.6F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(24, 0).addBox(-4.0F, 0.3F, -2.5F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.2F))
                .texOffs(22, 17).addBox(-4.0F, 0.3F, 1.5F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.2F))
                .texOffs(70, 63).addBox(-3.0F, 0.3F, 1.85F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, -6.0F, 0.0F));

        PartDefinition LeftTuft_r1 = body.addOrReplaceChild("LeftTuft_r1", CubeListBuilder.create().texOffs(9, 68).addBox(-9.25F, -21.0F, -2.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 22.0F, 0.0F, 0.0F, 0.0F, 0.6109F));

        PartDefinition RightTuft_r1 = body.addOrReplaceChild("RightTuft_r1", CubeListBuilder.create().texOffs(34, 69).addBox(8.25F, -21.0F, -2.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 22.0F, 0.0F, 0.0F, 0.0F, -0.6109F));

        PartDefinition TentacleArm4 = body.addOrReplaceChild("TentacleArm4", CubeListBuilder.create(), PartPose.offset(2.0F, 4.5F, 0.5F));

        PartDefinition TentacleArm_r1 = TentacleArm4.addOrReplaceChild("TentacleArm_r1", CubeListBuilder.create().texOffs(74, 21).addBox(-11.7F, -18.6F, 3.98F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.14F)), PartPose.offsetAndRotation(-2.0F, 25.5F, -0.5F, -0.2618F, 0.2182F, -0.1745F));

        PartDefinition TentacleArm_r2 = TentacleArm4.addOrReplaceChild("TentacleArm_r2", CubeListBuilder.create().texOffs(61, 63).addBox(1.8F, -20.7F, -3.3F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(-2.0F, 25.5F, -0.5F, -0.7854F, -0.9599F, 0.1745F));

        PartDefinition TentacleArm_r3 = TentacleArm4.addOrReplaceChild("TentacleArm_r3", CubeListBuilder.create().texOffs(17, 71).addBox(-1.6F, -14.15F, 0.75F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.16F)), PartPose.offsetAndRotation(-2.0F, 17.5F, -0.5F, -0.5236F, -0.5236F, 0.0873F));

        PartDefinition TentacleArm_r4 = TentacleArm4.addOrReplaceChild("TentacleArm_r4", CubeListBuilder.create().texOffs(64, 30).addBox(-2.75F, -14.0F, -2.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(-2.0F, 17.5F, -0.5F, -0.3491F, -0.2182F, 0.0F));

        PartDefinition TentaclePad4 = TentacleArm4.addOrReplaceChild("TentaclePad4", CubeListBuilder.create().texOffs(46, 62).addBox(-13.75F, -4.1F, 8.05F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.14F))
                .texOffs(0, 0).addBox(-14.8F, -4.6F, 8.05F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.14F))
                .texOffs(76, 18).addBox(-20.0F, -0.85F, 8.05F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.14F))
                .texOffs(29, 53).addBox(-20.0F, -5.35F, 8.05F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.14F))
                .texOffs(52, 61).addBox(-20.0F, -5.1F, 8.05F, 5.0F, 5.0F, 2.0F, new CubeDeformation(0.14F))
                .texOffs(0, 16).addBox(-17.6F, -4.1F, 7.85F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(50, 52).addBox(-16.1F, -4.65F, 7.85F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(52, 2).addBox(-16.1F, -1.55F, 7.85F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(51, 39).addBox(-19.3F, -4.35F, 7.85F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(52, 0).addBox(-19.3F, -4.6F, 7.85F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(48, 38).addBox(-19.3F, -1.6F, 7.85F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(4, 48).addBox(-18.9F, -3.1F, 7.85F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(0, 48).addBox(-19.3F, -1.85F, 7.85F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(16, 32).addBox(-18.3F, -4.6F, 7.85F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(4, 42).addBox(-14.5F, -2.2F, 7.85F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(4, 0).addBox(-14.5F, -4.0F, 7.85F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F)), PartPose.offsetAndRotation(31.25F, -6.5F, 6.2F, 0.0873F, -0.1745F, -0.2182F));

        PartDefinition TentacleArm3 = body.addOrReplaceChild("TentacleArm3", CubeListBuilder.create(), PartPose.offset(2.0F, 9.0F, 1.5F));

        PartDefinition TentacleArm_r5 = TentacleArm3.addOrReplaceChild("TentacleArm_r5", CubeListBuilder.create().texOffs(74, 14).addBox(7.55F, -18.6F, 3.98F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.14F)), PartPose.offsetAndRotation(-2.0F, 21.0F, -1.5F, -0.2618F, -0.2182F, 0.1745F));

        PartDefinition TentacleArm_r6 = TentacleArm3.addOrReplaceChild("TentacleArm_r6", CubeListBuilder.create().texOffs(28, 57).addBox(-3.75F, -20.61F, -3.3F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(-2.0F, 21.0F, -1.5F, -0.7854F, 0.9599F, -0.1745F));

        PartDefinition TentacleArm_r7 = TentacleArm3.addOrReplaceChild("TentacleArm_r7", CubeListBuilder.create().texOffs(71, 66).addBox(-0.4F, -14.15F, 0.75F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.16F)), PartPose.offsetAndRotation(-2.0F, 13.0F, -1.5F, -0.5236F, 0.5236F, -0.0873F));

        PartDefinition TentacleArm_r8 = TentacleArm3.addOrReplaceChild("TentacleArm_r8", CubeListBuilder.create().texOffs(0, 66).addBox(0.75F, -14.0F, -2.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(-2.0F, 13.0F, -1.5F, -0.3491F, 0.2182F, 0.0F));

        PartDefinition TentaclePad3 = TentacleArm3.addOrReplaceChild("TentaclePad3", CubeListBuilder.create().texOffs(41, 69).addBox(-13.75F, -4.1F, 7.9F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.14F))
                .texOffs(16, 26).addBox(-14.8F, -4.6F, 7.9F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.14F))
                .texOffs(76, 48).addBox(-20.0F, -0.85F, 7.9F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.14F))
                .texOffs(27, 76).addBox(-20.0F, -5.35F, 7.9F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.14F))
                .texOffs(16, 62).addBox(-20.0F, -5.1F, 7.9F, 5.0F, 5.0F, 2.0F, new CubeDeformation(0.14F))
                .texOffs(0, 26).addBox(-17.6F, -4.1F, 7.7F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(12, 60).addBox(-16.1F, -4.65F, 7.7F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(0, 60).addBox(-16.1F, -1.55F, 7.7F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(58, 16).addBox(-19.3F, -4.35F, 7.7F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(58, 18).addBox(-19.3F, -4.6F, 7.7F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(56, 12).addBox(-19.3F, -1.6F, 7.7F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(56, 10).addBox(-18.9F, -3.1F, 7.7F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(55, 1).addBox(-19.3F, -1.85F, 7.7F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(12, 42).addBox(-18.3F, -4.6F, 7.7F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(50, 54).addBox(-14.5F, -2.2F, 7.7F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(53, 53).addBox(-14.5F, -4.0F, 7.7F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F)), PartPose.offsetAndRotation(31.25F, 11.0F, 5.45F, -0.2618F, -0.2182F, 0.1745F));

        PartDefinition TentacleArm2 = body.addOrReplaceChild("TentacleArm2", CubeListBuilder.create(), PartPose.offset(-2.0F, 4.5F, 0.5F));

        PartDefinition TentacleArm_r9 = TentacleArm2.addOrReplaceChild("TentacleArm_r9", CubeListBuilder.create().texOffs(73, 56).addBox(-19.7F, -27.2F, 9.9F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.14F)), PartPose.offsetAndRotation(2.0F, 25.5F, -0.5F, 0.0873F, 0.1745F, 0.2182F));

        PartDefinition TentacleArm_r10 = TentacleArm2.addOrReplaceChild("TentacleArm_r10", CubeListBuilder.create().texOffs(37, 62).addBox(2.1F, -19.35F, 21.8F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(2.0F, 25.5F, -0.5F, 0.5672F, -0.7854F, -0.0436F));

        PartDefinition TentacleArm_r11 = TentacleArm2.addOrReplaceChild("TentacleArm_r11", CubeListBuilder.create().texOffs(68, 50).addBox(0.4F, -16.2F, 11.4F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.16F)), PartPose.offsetAndRotation(2.0F, 17.5F, -0.5F, 0.3491F, -0.5236F, -0.0873F));

        PartDefinition TentacleArm_r12 = TentacleArm2.addOrReplaceChild("TentacleArm_r12", CubeListBuilder.create().texOffs(25, 64).addBox(-0.5F, -18.0F, 3.75F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(2.0F, 17.5F, -0.5F, 0.1745F, -0.2182F, -0.1309F));

        PartDefinition TentaclePad2 = TentacleArm2.addOrReplaceChild("TentaclePad2", CubeListBuilder.create().texOffs(22, 77).addBox(-22.15F, -4.1F, 8.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.14F))
                .texOffs(0, 42).addBox(-21.2F, -4.6F, 8.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.14F))
                .texOffs(76, 51).addBox(-19.0F, -5.35F, 8.0F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.14F))
                .texOffs(77, 0).addBox(-19.0F, -0.85F, 8.0F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.14F))
                .texOffs(62, 16).addBox(-20.0F, -5.1F, 8.0F, 5.0F, 5.0F, 2.0F, new CubeDeformation(0.14F))
                .texOffs(12, 26).addBox(-18.2F, -4.1F, 7.8F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(9, 66).addBox(-19.8F, -4.65F, 7.8F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(65, 52).addBox(-19.8F, -1.55F, 7.8F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(65, 0).addBox(-16.7F, -4.6F, 7.8F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(38, 64).addBox(-16.7F, -4.35F, 7.8F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(34, 64).addBox(-16.7F, -1.6F, 7.8F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(63, 50).addBox(-17.1F, -3.1F, 7.8F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(12, 62).addBox(-16.7F, -1.85F, 7.8F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(64, 30).addBox(-17.5F, -4.6F, 7.8F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(0, 62).addBox(-21.5F, -2.2F, 7.8F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(60, 49).addBox(-21.5F, -4.0F, 7.8F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F)), PartPose.offsetAndRotation(2.25F, 1.5F, 0.25F, 0.0873F, 0.1745F, 0.2182F));

        PartDefinition TentacleArm1 = body.addOrReplaceChild("TentacleArm1", CubeListBuilder.create(), PartPose.offset(-2.0F, 9.0F, 1.5F));

        PartDefinition TentacleArm_r13 = TentacleArm1.addOrReplaceChild("TentacleArm_r13", CubeListBuilder.create().texOffs(73, 30).addBox(15.7F, -27.2F, 9.9F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.14F)), PartPose.offsetAndRotation(2.0F, 21.0F, -1.5F, 0.0873F, -0.1745F, -0.2182F));

        PartDefinition TentacleArm_r14 = TentacleArm1.addOrReplaceChild("TentacleArm_r14", CubeListBuilder.create().texOffs(63, 43).addBox(-4.1F, -19.35F, 21.8F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(2.0F, 21.0F, -1.5F, 0.5672F, 0.7854F, 0.0436F));

        PartDefinition TentacleArm_r15 = TentacleArm1.addOrReplaceChild("TentacleArm_r15", CubeListBuilder.create().texOffs(60, 70).addBox(-2.4F, -16.2F, 11.4F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.16F)), PartPose.offsetAndRotation(2.0F, 13.0F, -1.5F, 0.3491F, 0.5236F, 0.0873F));

        PartDefinition TentacleArm_r16 = TentacleArm1.addOrReplaceChild("TentacleArm_r16", CubeListBuilder.create().texOffs(64, 56).addBox(-1.5F, -18.0F, 3.75F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(2.0F, 13.0F, -1.5F, 0.1745F, 0.2182F, 0.1309F));

        PartDefinition TentaclePad1 = TentacleArm1.addOrReplaceChild("TentaclePad1", CubeListBuilder.create().texOffs(77, 35).addBox(-22.15F, -4.1F, 8.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.14F))
                .texOffs(44, 75).addBox(-21.2F, -4.6F, 8.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.14F))
                .texOffs(77, 7).addBox(-19.0F, -0.85F, 8.0F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.14F))
                .texOffs(10, 77).addBox(-19.0F, -5.35F, 8.0F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.14F))
                .texOffs(62, 23).addBox(-20.0F, -5.1F, 8.0F, 5.0F, 5.0F, 2.0F, new CubeDeformation(0.14F))
                .texOffs(42, 22).addBox(-18.2F, -4.1F, 7.8F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(20, 69).addBox(-19.8F, -4.65F, 7.8F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(57, 68).addBox(-19.8F, -1.55F, 7.8F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(68, 7).addBox(-16.7F, -4.6F, 7.8F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(68, 1).addBox(-16.7F, -4.35F, 7.8F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(9, 68).addBox(-16.7F, -1.6F, 7.8F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(67, 50).addBox(-17.1F, -3.1F, 7.8F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(68, 3).addBox(-16.7F, -1.85F, 7.8F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(0, 66).addBox(-17.5F, -4.6F, 7.8F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(68, 9).addBox(-21.5F, -2.2F, 7.8F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F))
                .texOffs(16, 69).addBox(-21.5F, -4.0F, 7.8F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.14F)), PartPose.offsetAndRotation(2.25F, 5.0F, -2.2F, -0.2618F, 0.2182F, -0.1745F));

        PartDefinition Tail = body.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 8.0F, 1.0F));

        PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(48, 68).addBox(-1.5F, 1.1914F, -1.7983F, 3.0F, 6.0F, 3.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.0F, 7.0F, 6.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition Base_r3 = Tail.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(56, 0).addBox(-1.5F, 0.5F, -1.0F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition Plantoids = body.addOrReplaceChild("Plantoids", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 0.0F));

        PartDefinition Center_r1 = Plantoids.addOrReplaceChild("Center_r1", CubeListBuilder.create().texOffs(24, 40).addBox(-1.0F, -11.5F, -2.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 9.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftPlantoid_r1 = Plantoids.addOrReplaceChild("LeftPlantoid_r1", CubeListBuilder.create().texOffs(9, 19).addBox(0.85F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.45F))
                .texOffs(3, 19).addBox(-3.85F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.45F)), PartPose.offsetAndRotation(0.0F, -1.0F, -2.0F, -0.1047F, 0.0F, 0.0F));

        PartDefinition rightArm = partdefinition.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(32, 33).addBox(-3.0446F, -1.0427F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.2F))
                .texOffs(54, 77).addBox(0.0554F, 10.9573F, -2.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(50, 77).addBox(-3.1446F, 10.9573F, 1.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(77, 40).addBox(-3.1446F, 10.9573F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(40, 77).addBox(-3.1446F, 10.9573F, -2.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-6.0F, -2.0F, -0.15F, 0.0F, 0.0F, 0.0436F));

        PartDefinition rightArm2 = partdefinition.addOrReplaceChild("rightArm2", CubeListBuilder.create(), PartPose.offset(-6.0F, -4.5F, 0.0F));

        PartDefinition Finger_r1 = rightArm2.addOrReplaceChild("Finger_r1", CubeListBuilder.create().texOffs(45, 69).addBox(-1.5683F, 2.5152F, -2.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(60, 70).addBox(-1.5683F, 2.5152F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(70, 66).addBox(-1.5683F, 2.5152F, 1.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(16, 71).addBox(1.6317F, 2.5152F, -2.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(0, 26).addBox(-1.4683F, -9.4848F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(-3.2205F, 7.7764F, 0.05F, 0.0F, 0.0F, 0.1745F));

        PartDefinition leftArm = partdefinition.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(40, 0).addBox(-0.9554F, -1.0427F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.2F))
                .texOffs(77, 78).addBox(-1.0554F, 10.9573F, -2.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(78, 76).addBox(2.1446F, 10.9573F, 1.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(78, 74).addBox(2.1446F, 10.9573F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(78, 60).addBox(2.1446F, 10.9573F, -2.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(6.0F, -2.0F, -0.15F, 0.0F, 0.0F, -0.0436F));

        PartDefinition leftArm2 = partdefinition.addOrReplaceChild("leftArm2", CubeListBuilder.create(), PartPose.offset(6.0F, -4.5F, 0.0F));

        PartDefinition Finger_r2 = leftArm2.addOrReplaceChild("Finger_r2", CubeListBuilder.create().texOffs(68, 71).addBox(-13.9F, 15.75F, 0.9F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(72, 14).addBox(-13.9F, 15.75F, 2.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(72, 43).addBox(-13.9F, 15.75F, 4.1F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(14.954F, -7.7821F, -2.95F, 0.0F, 0.0F, -0.1745F));

        PartDefinition Finger_r3 = leftArm2.addOrReplaceChild("Finger_r3", CubeListBuilder.create().texOffs(72, 45).addBox(-14.1F, 15.75F, 0.9F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(11.954F, -7.7821F, -2.95F, 0.0F, 0.0F, -0.1745F));

        PartDefinition LeftArm_r1 = leftArm2.addOrReplaceChild("LeftArm_r1", CubeListBuilder.create().texOffs(16, 33).addBox(-17.0F, 4.5F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(14.954F, -8.5321F, 0.05F, 0.0F, 0.0F, -0.1745F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green,
                               float blue, float alpha) {
        Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        Torso.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        LeftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        RightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        LeftArm2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        RightArm2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        LeftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        RightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void prepareMobModel(LatexSquidDogFemale p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(controller, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    @Override
    public void setupAnim(LatexSquidDogFemale entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float HeadPitch) {
        controller.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, HeadPitch);
    }

    public PoseStack getPlacementCorrectors(HumanoidArm arm) {
        PoseStack corrector = new PoseStack();
        corrector.translate(0.0f, 2.5f / 18.0f, 0.0f);
        return corrector;
    }

    @Override
    public ModelPart getArm(HumanoidArm humanoidArm) {
        return switch (humanoidArm) {
            case LEFT -> LeftArm;
            case RIGHT -> RightArm;
        };
    }

    @Override
    public void setupHand() {
        controller.setupHand();
    }

    @Override
    public LatexHumanoidModelController getController() {
        return controller;
    }

    @Override
    public ModelPart getHead() {
        return Head;
    }
}
