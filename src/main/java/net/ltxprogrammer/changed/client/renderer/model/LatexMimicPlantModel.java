package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.beast.LatexMimicPlant;
import net.ltxprogrammer.changed.entity.beast.LatexPurpleFox;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;

public class LatexMimicPlantModel extends LatexHumanoidModel<LatexMimicPlant> implements LatexHumanoidModelInterface {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_mimic_plant"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final LatexHumanoidModelController controller;

    public LatexMimicPlantModel(ModelPart root) {
        super(root);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Tail = Torso.getChild("Tail");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        controller = LatexHumanoidModelController.Builder.of(this, Head, Torso, Tail, RightArm, LeftArm, RightLeg, LeftLeg).build();
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(14, 55).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(24, 19).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 44).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(54, 2).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(24, 46).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(50, 13).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 51).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(41, 55).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(32, 42).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(44, 21).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(54, 0).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(60, 29).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(20, 61).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(38, 64).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(24, 61).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(22, 64).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(64, 29).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(55, 10).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(42, 2).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(12, 44).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -34.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(58, 38).addBox(-2.0F, -29.0F, -6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(44, 31).addBox(-1.5F, -27.0F, -5.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(24, 8).addBox(-2.0F, -35.0F, -4.0F, 4.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(36, 17).addBox(-4.0F, -34.5F, -3.5F, 8.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(60, 24).addBox(2.0F, -35.05F, 0.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(16, 33).addBox(2.5F, -34.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.025F))
                .texOffs(56, 36).addBox(-3.5F, -33.0F, -4.25F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(63, 62).addBox(3.5F, -34.0F, -4.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(59, 62).addBox(-4.5F, -34.0F, -4.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(25, 58).addBox(2.5F, -31.0F, -1.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(55, 55).addBox(-4.5F, -31.0F, -1.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(32, 25).addBox(-4.5F, -34.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.025F))
                .texOffs(0, 48).addBox(-4.0F, -34.95F, 0.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(24, 6).addBox(-4.0F, -34.0F, -4.5F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(48, 42).addBox(-4.0F, -34.0F, 4.0F, 8.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition RightEar = Hair.addOrReplaceChild("RightEar", CubeListBuilder.create().texOffs(67, 0).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(16, 67).addBox(-0.5F, -1.6F, -0.4F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(34, 67).addBox(-0.5F, -2.1F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(24, 67).addBox(0.5F, -3.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-3.05F, -34.275F, 0.0F, 0.0F, 0.0F, -0.2618F));

        PartDefinition LeftEar = Hair.addOrReplaceChild("LeftEar", CubeListBuilder.create().texOffs(71, 4).addBox(-1.0F, -1.0F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(10, 67).addBox(-1.0F, -1.6F, -0.4F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(40, 67).addBox(-1.0F, -2.1F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(28, 67).addBox(-1.0F, -2.9F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(2.6F, -34.5F, 0.0F, 0.0F, 0.0F, 0.2618F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -25.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, -14.0F, 0.0F));

        PartDefinition leafpart_r1 = Tail.addOrReplaceChild("leafpart_r1", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, -0.35F, 0.6F, 3.0F, 3.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(-1.5F, 0.3F, 16.925F, 1.1345F, 0.0F, 0.0F));

        PartDefinition leafpart_r2 = Tail.addOrReplaceChild("leafpart_r2", CubeListBuilder.create().texOffs(56, 19).addBox(-2.0F, -0.35F, -0.4F, 5.0F, 3.0F, 2.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(-0.5F, -0.325F, 15.925F, 1.309F, 0.0F, 0.0F));

        PartDefinition leafpart_r3 = Tail.addOrReplaceChild("leafpart_r3", CubeListBuilder.create().texOffs(26, 53).addBox(-4.0F, 0.65F, -0.4F, 7.0F, 3.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.5F, -0.4F, 13.025F, 1.4835F, 0.0F, 0.0F));

        PartDefinition leafpart_r4 = Tail.addOrReplaceChild("leafpart_r4", CubeListBuilder.create().texOffs(24, 0).addBox(-6.0F, -0.35F, -0.4F, 9.0F, 4.0F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(1.5F, 0.175F, 11.3F, 1.7017F, 0.0F, 0.0F));

        PartDefinition leafpart_r5 = Tail.addOrReplaceChild("leafpart_r5", CubeListBuilder.create().texOffs(36, 36).addBox(-6.0F, 0.375F, -0.4F, 9.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, 1.95F, 7.725F, 2.0071F, 0.0F, 0.0F));

        PartDefinition leafpart_r6 = Tail.addOrReplaceChild("leafpart_r6", CubeListBuilder.create().texOffs(0, 54).addBox(-4.0F, -0.35F, -1.4F, 7.0F, 3.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.5F, 1.3F, 5.7F, 1.789F, 0.0F, 0.0F));

        PartDefinition top_r1 = Tail.addOrReplaceChild("top_r1", CubeListBuilder.create().texOffs(12, 32).addBox(-3.0F, -0.125F, -1.4F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -2.725F, 13.925F, 1.4399F, 0.0F, 0.0F));

        PartDefinition top_r2 = Tail.addOrReplaceChild("top_r2", CubeListBuilder.create().texOffs(52, 31).addBox(-5.0F, -0.125F, -1.4F, 7.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -1.65F, 11.575F, 1.6144F, 0.0F, 0.0F));

        PartDefinition top_r3 = Tail.addOrReplaceChild("top_r3", CubeListBuilder.create().texOffs(48, 49).addBox(-5.0F, -0.35F, -1.4F, 7.0F, 4.0F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(1.5F, -0.45F, 8.3F, 1.9199F, 0.0F, 0.0F));

        PartDefinition top_r4 = Tail.addOrReplaceChild("top_r4", CubeListBuilder.create().texOffs(58, 6).addBox(-3.0F, -0.35F, -1.4F, 5.0F, 3.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.5F, -0.25F, 5.9F, 1.8326F, 0.0F, 0.0F));

        PartDefinition leafline_r1 = Tail.addOrReplaceChild("leafline_r1", CubeListBuilder.create().texOffs(44, 42).addBox(-1.0F, 0.65F, -1.4F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.5F, -3.3F, 13.925F, 1.309F, 0.0F, 0.0F));

        PartDefinition leafline_r2 = Tail.addOrReplaceChild("leafline_r2", CubeListBuilder.create().texOffs(0, 48).addBox(-1.0F, 0.65F, -1.4F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.5F, -2.7F, 17.0F, 0.9163F, 0.0F, 0.0F));

        PartDefinition leafline_r3 = Tail.addOrReplaceChild("leafline_r3", CubeListBuilder.create().texOffs(0, 16).addBox(-0.1F, 0.65F, -1.4F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(0.2F, -2.6F, 10.2F, 1.6581F, -0.6109F, 0.0F));

        PartDefinition leafline_r4 = Tail.addOrReplaceChild("leafline_r4", CubeListBuilder.create().texOffs(28, 33).addBox(-1.0F, 0.65F, -1.4F, 1.0F, 2.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(-0.825F, -2.9F, 12.75F, 1.5272F, -0.2618F, 0.0F));

        PartDefinition leafline_r5 = Tail.addOrReplaceChild("leafline_r5", CubeListBuilder.create().texOffs(9, 59).addBox(-1.0F, 0.65F, -1.4F, 1.0F, 2.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(1.8F, -2.9F, 12.5F, 1.5272F, 0.2618F, 0.0F));

        PartDefinition leafline_r6 = Tail.addOrReplaceChild("leafline_r6", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, 0.65F, -1.4F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(-0.1F, -2.6F, 10.15F, 1.6581F, 0.6109F, 0.0F));

        PartDefinition leafline_r7 = Tail.addOrReplaceChild("leafline_r7", CubeListBuilder.create().texOffs(28, 46).addBox(-1.0F, 0.65F, -1.4F, 1.0F, 4.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(0.5F, -2.6F, 9.85F, 1.7017F, 0.0F, 0.0F));

        PartDefinition leafline_r8 = Tail.addOrReplaceChild("leafline_r8", CubeListBuilder.create().texOffs(8, 48).addBox(-1.0F, 0.65F, -1.4F, 1.0F, 3.0F, 1.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.5F, -1.575F, 7.4F, 1.8937F, 0.0F, 0.0F));

        PartDefinition leafline_r9 = Tail.addOrReplaceChild("leafline_r9", CubeListBuilder.create().texOffs(60, 24).addBox(0.05F, -0.5F, -0.3F, 1.0F, 2.0F, 1.0F, new CubeDeformation(-0.125F)), PartPose.offsetAndRotation(-4.95F, 0.0F, 9.8F, 1.3963F, -0.1745F, 0.1745F));

        PartDefinition leafline_r10 = Tail.addOrReplaceChild("leafline_r10", CubeListBuilder.create().texOffs(34, 60).addBox(0.0F, -0.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(-0.125F)), PartPose.offsetAndRotation(3.95F, 0.0F, 10.025F, 1.3963F, 0.1745F, -0.1745F));

        PartDefinition leafline_r11 = Tail.addOrReplaceChild("leafline_r11", CubeListBuilder.create().texOffs(28, 36).addBox(-4.0F, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(-2.45F, 0.25F, 6.525F, 0.3927F, 0.9599F, 0.2182F));

        PartDefinition leafline_r12 = Tail.addOrReplaceChild("leafline_r12", CubeListBuilder.create().texOffs(40, 13).addBox(0.0F, -0.5F, -0.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(2.45F, 0.25F, 6.525F, 0.3927F, -0.9599F, -0.2182F));

        PartDefinition leafline_r13 = Tail.addOrReplaceChild("leafline_r13", CubeListBuilder.create().texOffs(44, 0).addBox(-3.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.2F, 5.9F, 0.2618F, 0.3054F, 0.0F));

        PartDefinition leafline_r14 = Tail.addOrReplaceChild("leafline_r14", CubeListBuilder.create().texOffs(20, 17).addBox(0.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(2.1F, -1.25F, 9.35F, -0.9599F, -1.0472F, -0.5672F));

        PartDefinition leafline_r15 = Tail.addOrReplaceChild("leafline_r15", CubeListBuilder.create().texOffs(28, 38).addBox(0.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(-2.1F, -1.25F, 9.35F, -0.6109F, -2.0944F, 0.5672F));

        PartDefinition leafline_r16 = Tail.addOrReplaceChild("leafline_r16", CubeListBuilder.create().texOffs(48, 15).addBox(0.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(3.25F, -1.9F, 11.575F, -0.2182F, -1.2217F, 0.2618F));

        PartDefinition leafline_r17 = Tail.addOrReplaceChild("leafline_r17", CubeListBuilder.create().texOffs(52, 0).addBox(0.0F, -0.5F, -0.5F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(-3.25F, -1.9F, 11.575F, 0.2182F, -1.9199F, -0.2182F));

        PartDefinition leafline_r18 = Tail.addOrReplaceChild("leafline_r18", CubeListBuilder.create().texOffs(40, 21).addBox(0.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, -0.65F, 7.9F, -0.3927F, -2.5307F, 0.3054F));

        PartDefinition leafline_r19 = Tail.addOrReplaceChild("leafline_r19", CubeListBuilder.create().texOffs(40, 23).addBox(0.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, -0.65F, 7.9F, 0.3927F, -0.6109F, -0.3054F));

        PartDefinition leafline_r20 = Tail.addOrReplaceChild("leafline_r20", CubeListBuilder.create().texOffs(24, 44).addBox(0.0F, -0.5F, -0.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.2F, 5.9F, 0.2618F, -0.3054F, 0.0F));

        PartDefinition leafline_r21 = Tail.addOrReplaceChild("leafline_r21", CubeListBuilder.create().texOffs(55, 55).addBox(-1.0F, 0.65F, -1.4F, 1.0F, 4.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.5F, -0.025F, 3.9F, 1.9635F, 0.0F, 0.0F));

        PartDefinition leafline_r22 = Tail.addOrReplaceChild("leafline_r22", CubeListBuilder.create().texOffs(55, 62).addBox(-1.0F, 0.65F, -1.4F, 1.0F, 2.0F, 1.0F, new CubeDeformation(-0.125F)), PartPose.offsetAndRotation(0.5F, -0.2F, 3.1F, 1.6581F, 0.0F, 0.0F));

        PartDefinition tailleafstart_r1 = Tail.addOrReplaceChild("tailleafstart_r1", CubeListBuilder.create().texOffs(12, 61).addBox(-2.0F, -2.7F, 0.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 2.65F, 5.6F, 1.5272F, 0.0F, 0.0F));

        PartDefinition tailstart_r1 = Tail.addOrReplaceChild("tailstart_r1", CubeListBuilder.create().texOffs(0, 59).addBox(-2.0F, 0.0F, -1.0F, 3.0F, 4.0F, 3.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.5F, -0.275F, 0.6F, 0.9163F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(24, 17).addBox(-8.0F, -26.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(50, 64).addBox(-5.0F, -14.25F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(64, 49).addBox(-8.0F, -14.25F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(46, 64).addBox(-8.0F, -14.25F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(42, 64).addBox(-8.0F, -14.25F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 32).addBox(4.0F, -26.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(29, 65).addBox(4.0F, -14.25F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(25, 65).addBox(7.0F, -14.25F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(64, 57).addBox(7.0F, -14.25F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(64, 55).addBox(7.0F, -14.25F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 25.0F, 0.0F));

        return LayerDefinition.create(process(meshdefinition), 128, 128);
    }


    @Override
    public void prepareMobModel(LatexMimicPlant p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(controller, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        controller.setupHand();
    }

    @Override
    public LatexHumanoidModelController getController() {
        return controller;
    }

    @Override
    public void setupAnim(LatexMimicPlant entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        controller.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public ModelPart getArm(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }

    public ModelPart getHead() {
        return this.Head;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        RightLeg.render(poseStack, buffer, packedLight, packedOverlay);
        LeftLeg.render(poseStack, buffer, packedLight, packedOverlay);
        Head.render(poseStack, buffer, packedLight, packedOverlay);
        Torso.render(poseStack, buffer, packedLight, packedOverlay);
        RightArm.render(poseStack, buffer, packedLight, packedOverlay);
        LeftArm.render(poseStack, buffer, packedLight, packedOverlay);
    }
}
