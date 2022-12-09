package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.beast.LatexBlueDragon;
import net.ltxprogrammer.changed.entity.beast.LatexPinkWyvern;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

public class LatexPinkWyvernModel extends LatexHumanoidModel<LatexPinkWyvern> implements LatexHumanoidModelInterface {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_pink_wyvern"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final LatexHumanoidModelController controller;

    public LatexPinkWyvernModel(ModelPart root) {
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

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(53, 0).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(0, 18).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 32).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(38, 0).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(0, 34).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(16, 36).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 36).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(16, 51).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(40, 16).addBox(-1.99F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(32, 43).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(52, 12).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(38, 2).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 16).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 44).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(16, 41).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(16, 43).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 44).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(48, 44).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(34, 32).addBox(-2.01F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(16, 41).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -34.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(61, 35).addBox(-2.0F, -29.0F, -6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(20, 16).addBox(-1.5F, -27.0F, -5.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition Horn_r1 = Head.addOrReplaceChild("Horn_r1", CubeListBuilder.create().texOffs(32, 7).addBox(2.0F, -19.65F, 20.75F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.7418F, 0.6109F, 0.0F));

        PartDefinition Horn_r2 = Head.addOrReplaceChild("Horn_r2", CubeListBuilder.create().texOffs(32, 10).addBox(2.69F, -23.75F, 17.75F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.6109F, 0.5672F, 0.0F));

        PartDefinition Horn_r3 = Head.addOrReplaceChild("Horn_r3", CubeListBuilder.create().texOffs(24, 0).addBox(3.05F, -29.8F, 10.15F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.3054F, 0.6109F, 0.0F));

        PartDefinition Horn_r4 = Head.addOrReplaceChild("Horn_r4", CubeListBuilder.create().texOffs(36, 16).addBox(-3.0F, -19.65F, 20.75F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.7418F, -0.6109F, 0.0F));

        PartDefinition Horn_r5 = Head.addOrReplaceChild("Horn_r5", CubeListBuilder.create().texOffs(24, 3).addBox(-3.69F, -23.75F, 17.75F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.6109F, -0.5672F, 0.0F));

        PartDefinition Horn_r6 = Head.addOrReplaceChild("Horn_r6", CubeListBuilder.create().texOffs(28, 41).addBox(-4.05F, -29.8F, 9.9F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.3054F, -0.6109F, 0.0F));

        PartDefinition Horn_r7 = Head.addOrReplaceChild("Horn_r7", CubeListBuilder.create().texOffs(61, 56).addBox(1.0F, -31.4F, 19.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.48F, 0.1745F, 0.0F));

        PartDefinition Horn_r8 = Head.addOrReplaceChild("Horn_r8", CubeListBuilder.create().texOffs(27, 62).addBox(1.0F, -35.3F, 8.2F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.2182F, 0.1745F, 0.0F));

        PartDefinition Horn_r9 = Head.addOrReplaceChild("Horn_r9", CubeListBuilder.create().texOffs(62, 46).addBox(1.0F, -31.0F, -19.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, -0.5672F, 0.1745F, 0.0F));

        PartDefinition Horn_r10 = Head.addOrReplaceChild("Horn_r10", CubeListBuilder.create().texOffs(37, 61).addBox(-3.0F, -31.4F, 19.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.48F, -0.1745F, 0.0F));

        PartDefinition Horn_r11 = Head.addOrReplaceChild("Horn_r11", CubeListBuilder.create().texOffs(10, 62).addBox(-3.0F, -35.3F, 8.2F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.2182F, -0.1745F, 0.0F));

        PartDefinition Horn_r12 = Head.addOrReplaceChild("Horn_r12", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -31.0F, -19.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, -0.5672F, -0.1745F, 0.0F));

        PartDefinition LeftEar_r1 = Head.addOrReplaceChild("LeftEar_r1", CubeListBuilder.create().texOffs(53, 63).addBox(-6.2F, -26.3F, -22.5F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 56).addBox(-6.2F, -27.2F, -25.5F, 2.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, -0.8727F, 0.1309F, 0.2618F));

        PartDefinition RightEar_r1 = Head.addOrReplaceChild("RightEar_r1", CubeListBuilder.create().texOffs(44, 53).addBox(4.2F, -27.2F, -25.5F, 2.0F, 8.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(59, 63).addBox(4.2F, -26.3F, -22.5F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, -0.8727F, -0.1309F, -0.2618F));

        PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -29.625F, -0.95F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, -34.75F, -17.5F, 4.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(40, 27).addBox(-3.5F, -34.5F, -18.5F, 7.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(18, 60).addBox(1.5F, -34.75F, -15.5F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(58, 27).addBox(-3.5F, -34.75F, -15.5F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 15.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -25.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(61, 39).addBox(-2.0F, -23.0F, -2.75F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(26, 60).addBox(-3.0F, -23.0F, 1.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(59, 43).addBox(-3.0F, -25.0F, -2.75F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(60, 6).addBox(-3.0F, -22.0F, -2.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 8).addBox(-4.0F, -25.0F, -2.5F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(51, 53).addBox(-4.0F, -25.0F, 1.5F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(58, 32).addBox(-3.0F, -25.0F, 1.75F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition LeftTuft_r1 = Torso.addOrReplaceChild("LeftTuft_r1", CubeListBuilder.create().texOffs(56, 20).addBox(-11.5F, -23.5F, -2.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6109F));

        PartDefinition RightTuft_r1 = Torso.addOrReplaceChild("RightTuft_r1", CubeListBuilder.create().texOffs(54, 56).addBox(10.5F, -23.5F, -2.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.6109F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, -14.0F, 0.0F));

        PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(32, 7).addBox(-1.5F, -13.2F, 6.9F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 14.0F, 0.0F, -0.1309F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(20, 18).addBox(-1.0F, -9.85F, 19.35F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(46, 31).addBox(-2.0F, -9.85F, 15.175F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.1F))
                .texOffs(38, 0).addBox(-1.0F, -10.35F, 13.925F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 14.0F, 0.0F, 0.0436F, 0.0F, 0.0F));

        PartDefinition Base_r3 = Tail.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(16, 32).addBox(-1.5F, -1.375F, -2.6983F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(0.0F, 2.0F, 6.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition Base_r4 = Tail.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, -2.8F, 0.4F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(24, 16).addBox(-8.0F, -26.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(46, 33).addBox(-5.0F, -14.25F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(46, 31).addBox(-8.0F, -14.25F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(46, 2).addBox(-7.999F, -14.25F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(46, 0).addBox(-8.0F, -14.25F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition Wing_r1 = RightArm.addOrReplaceChild("Wing_r1", CubeListBuilder.create().texOffs(44, 36).addBox(-6.5F, -13.89F, 7.7F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-1.0F, -1.0F, 0.0F, 0.3491F, -0.2182F, 0.0F));

        PartDefinition Wing_r2 = RightArm.addOrReplaceChild("Wing_r2", CubeListBuilder.create().texOffs(6, 0).addBox(-6.0F, -9.1F, 17.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 3).addBox(-6.0F, -10.1F, 14.0F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(52, 12).addBox(-6.0F, -12.1F, 12.0F, 0.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -1.0F, 0.0F, 0.6545F, -0.2182F, 0.0F));

        PartDefinition Wing_r3 = RightArm.addOrReplaceChild("Wing_r3", CubeListBuilder.create().texOffs(53, 36).addBox(-6.5F, -12.0F, 13.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -1.0F, 0.0F, 0.6981F, -0.2182F, 0.0F));

        PartDefinition Wing_r4 = RightArm.addOrReplaceChild("Wing_r4", CubeListBuilder.create().texOffs(72, 27).addBox(-6.0F, -10.75F, 17.0F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -1.0F, 0.0F, 0.9599F, -0.2182F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 32).addBox(4.0F, -26.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 50).addBox(4.0F, -14.25F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(12, 48).addBox(7.0F, -14.25F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 48).addBox(6.999F, -14.25F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(47, 45).addBox(7.0F, -14.25F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition Wing_r5 = LeftArm.addOrReplaceChild("Wing_r5", CubeListBuilder.create().texOffs(44, 0).addBox(5.5F, -13.89F, 7.7F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(1.0F, -1.0F, 0.0F, 0.3491F, 0.2182F, 0.0F));

        PartDefinition Wing_r6 = LeftArm.addOrReplaceChild("Wing_r6", CubeListBuilder.create().texOffs(0, 0).addBox(6.0F, -9.1F, 17.0F, 0.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 2).addBox(6.0F, -10.1F, 14.0F, 0.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(10, 54).addBox(6.0F, -12.1F, 12.0F, 0.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -1.0F, 0.0F, 0.6545F, 0.2182F, 0.0F));

        PartDefinition Wing_r7 = LeftArm.addOrReplaceChild("Wing_r7", CubeListBuilder.create().texOffs(30, 53).addBox(5.5F, -12.0F, 13.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -1.0F, 0.0F, 0.6981F, 0.2182F, 0.0F));

        PartDefinition Wing_r8 = LeftArm.addOrReplaceChild("Wing_r8", CubeListBuilder.create().texOffs(64, 16).addBox(6.0F, -10.7F, 17.0F, 0.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, -1.0F, 0.0F, 0.9599F, 0.2182F, 0.0F));

        return LayerDefinition.create(process(meshdefinition), 128, 128);
    }


    @Override
    public void prepareMobModel(LatexPinkWyvern p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(controller, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        controller.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LatexPinkWyvern entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        controller.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public void translateToHand(HumanoidArm p_102854_, PoseStack p_102855_) {
        this.getArm(p_102854_).translateAndRotate(p_102855_);
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

    @Override
    public LatexHumanoidModelController getController() {
        return controller;
    }
}
