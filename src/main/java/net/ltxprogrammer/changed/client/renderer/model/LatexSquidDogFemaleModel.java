package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.beast.LatexSquidDogFemale;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;

import java.util.ArrayList;
import java.util.List;

public class LatexSquidDogFemaleModel extends LatexHumanoidModel<LatexSquidDogFemale> implements LatexHumanoidModelInterface<LatexSquidDogFemale, LatexSquidDogFemaleModel>, DoubleArmedModel {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_squid_dog_female"), "main");
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart LeftArm;
    private final ModelPart RightArm;
    private final ModelPart LeftArm2;
    private final ModelPart RightArm2;
    private final ModelPart LeftLeg;
    private final ModelPart RightLeg;
    private final ModelPart Tail;
    private final LatexAnimator<LatexSquidDogFemale, LatexSquidDogFemaleModel> animator;

    public LatexSquidDogFemaleModel(ModelPart root) {
        super(root);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.RightArm = root.getChild("RightArm");
        this.RightArm2 = root.getChild("RightArm2");
        this.LeftArm = root.getChild("LeftArm");
        this.LeftArm2 = root.getChild("LeftArm2");
        this.Tail = Torso.getChild("Tail");

        var tailPrimary = Tail.getChild("TailPrimary");
        var tailSecondary = tailPrimary.getChild("TailSecondary");
        var tailTertiary = tailSecondary.getChild("TailTertiary");

        var leftLowerLeg = LeftLeg.getChild("LeftLowerLeg");
        var leftFoot = leftLowerLeg.getChild("LeftFoot");
        var rightLowerLeg = RightLeg.getChild("RightLowerLeg");
        var rightFoot = rightLowerLeg.getChild("RightFoot");

        var upperRightTentacle = new ArrayList<ModelPart>();
        upperRightTentacle.add(Torso.getChild("RightUpperTentacle"));
        upperRightTentacle.add(last(upperRightTentacle).getChild("TentacleSecondaryRU"));
        upperRightTentacle.add(last(upperRightTentacle).getChild("TentacleTertiaryRU"));
        upperRightTentacle.add(last(upperRightTentacle).getChild("TentacleQuaternaryRU"));
        upperRightTentacle.add(last(upperRightTentacle).getChild("TentaclePadRU"));
        var upperLeftTentacle = new ArrayList<ModelPart>();
        upperLeftTentacle.add(Torso.getChild("LeftUpperTentacle"));
        upperLeftTentacle.add(last(upperLeftTentacle).getChild("TentacleSecondaryLU"));
        upperLeftTentacle.add(last(upperLeftTentacle).getChild("TentacleTertiaryLU"));
        upperLeftTentacle.add(last(upperLeftTentacle).getChild("TentacleQuaternaryLU"));
        upperLeftTentacle.add(last(upperLeftTentacle).getChild("TentaclePadLU"));
        var lowerRightTentacle = new ArrayList<ModelPart>();
        lowerRightTentacle.add(Torso.getChild("RightLowerTentacle"));
        lowerRightTentacle.add(last(lowerRightTentacle).getChild("TentacleSecondaryRL"));
        lowerRightTentacle.add(last(lowerRightTentacle).getChild("TentacleTertiaryRL"));
        lowerRightTentacle.add(last(lowerRightTentacle).getChild("TentacleQuaternaryRL"));
        lowerRightTentacle.add(last(lowerRightTentacle).getChild("TentaclePadRL"));
        var lowerLeftTentacle = new ArrayList<ModelPart>();
        lowerLeftTentacle.add(Torso.getChild("LeftLowerTentacle"));
        lowerLeftTentacle.add(last(lowerLeftTentacle).getChild("TentacleSecondaryLL"));
        lowerLeftTentacle.add(last(lowerLeftTentacle).getChild("TentacleTertiaryLL"));
        lowerLeftTentacle.add(last(lowerLeftTentacle).getChild("TentacleQuaternaryLL"));
        lowerLeftTentacle.add(last(lowerLeftTentacle).getChild("TentaclePadLL"));

        animator = LatexAnimator.of(this).hipOffset(-2.5f)
                .addPreset(AnimatorPresets.squidDogLike(
                        Head, Head.getChild("LeftEar"), Head.getChild("RightEar"),
                        Torso, LeftArm2, RightArm2, LeftArm, RightArm,
                        Tail, List.of(tailPrimary, tailSecondary, tailTertiary), upperLeftTentacle, upperRightTentacle, lowerLeftTentacle, lowerRightTentacle,
                        LeftLeg, leftLowerLeg, leftFoot, leftFoot.getChild("LeftPad"), RightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad")));
        animator.torsoWidth = 5.2f;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.7F, 9.3F, 0.0F));

        PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(56, 17).addBox(-2.0F, -0.9F, -2.55F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.35F)), PartPose.offsetAndRotation(0.0F, 1.2F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.75F, -4.1F));

        PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(56, 48).addBox(-1.99F, -0.9F, -2.4F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -1.3F, 2.6F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.9F, 8.2F));

        PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(44, 64).addBox(-2.0F, -8.95F, -0.825F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.255F)), PartPose.offsetAndRotation(0.0F, 7.8F, -5.35F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(56, 58).addBox(-2.0F, 0.25F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 4.55F, -4.8F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.7F, 9.3F, 0.0F));

        PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(0, 53).addBox(-2.0F, -0.9F, -2.55F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.35F)), PartPose.offsetAndRotation(0.0F, 1.2F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.75F, -4.1F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(56, 0).addBox(-2.01F, -0.9F, -2.4F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, -1.3F, 2.6F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.9F, 8.2F));

        PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(64, 35).addBox(-2.0F, -8.95F, -0.825F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.255F)), PartPose.offsetAndRotation(0.0F, 7.8F, -5.35F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(56, 28).addBox(-2.0F, 0.25F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 4.55F, -4.8F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(24, 11).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(24, 0).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 5).addBox(-1.5F, -1.0F, -5.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -2.2F, 0.0F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offset(-3.0F, -7.5F, 0.0F));

        PartDefinition RightEarPivot = RightEar.addOrReplaceChild("RightEarPivot", CubeListBuilder.create().texOffs(12, 37).addBox(-1.9F, -1.2F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(40, 36).addBox(-0.9F, -1.6F, -0.4F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(0, 24).addBox(-0.9F, -2.3F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(0, 37).addBox(0.1F, -3.1F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.5F, -1.25F, 0.0F, -0.1309F, 0.5236F, -0.3491F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offset(3.0F, -7.5F, 0.0F));

        PartDefinition LeftEarPivot = LeftEar.addOrReplaceChild("LeftEarPivot", CubeListBuilder.create().texOffs(24, 4).addBox(-1.1F, -1.2F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(52, 0).addBox(-1.1F, -1.6F, -0.4F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(24, 37).addBox(-1.1F, -2.3F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(0, 39).addBox(-1.1F, -3.1F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-0.5F, -1.25F, 0.0F, -0.1309F, -0.5236F, 0.3491F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 11.0F, 8.0F, new CubeDeformation(0.2F))
                .texOffs(0, 19).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(32, 27).addBox(-4.0F, 0.1F, -2.2F, 8.0F, 5.0F, 4.0F, new CubeDeformation(0.1F))
                .texOffs(32, 0).addBox(-4.0F, 4.5F, -2.2F, 8.0F, 6.0F, 4.0F, new CubeDeformation(-0.2F))
                .texOffs(48, 10).addBox(-4.0F, 9.1F, -2.2F, 8.0F, 3.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, -2.2F, 0.0F));

        PartDefinition Plantoids = Torso.addOrReplaceChild("Plantoids", CubeListBuilder.create(), PartPose.offset(0.0F, 0.1F, -2.3F));

        PartDefinition RightPlantoid_r1 = Plantoids.addOrReplaceChild("RightPlantoid_r1", CubeListBuilder.create().texOffs(70, 8).addBox(-4.35F, -1.7F, -0.8F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.07F))
                .texOffs(68, 44).addBox(0.35F, -1.7F, -0.8F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.07F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.2793F, 0.0F, 0.0F));

        PartDefinition Center_r1 = Plantoids.addOrReplaceChild("Center_r1", CubeListBuilder.create().texOffs(32, 27).addBox(-0.5F, -1.3F, -0.2F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.192F, 0.0F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 11.0F, 0.0F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition Base_r1 = TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(28, 60).addBox(-2.0F, 1.15F, -1.4F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.25F, 5.3F));

        PartDefinition Base_r2 = TailSecondary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(44, 36).addBox(-2.5F, 0.45F, -2.1F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 0.5F, -0.8F, 1.4835F, 0.0F, 0.0F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.25F, 5.2F));

        PartDefinition Base_r3 = TailTertiary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(0, 64).addBox(-2.0F, 0.1F, -2.35F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.5F, 1.8326F, 0.0F, 0.0F));

        PartDefinition RightUpperTentacle = Torso.addOrReplaceChild("RightUpperTentacle", CubeListBuilder.create(), PartPose.offset(-2.5F, 2.7F, 1.0F));

        PartDefinition TentaclePart_r1 = RightUpperTentacle.addOrReplaceChild("TentaclePart_r1", CubeListBuilder.create().texOffs(44, 73).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.192F, -0.4363F, -0.0524F));

        PartDefinition TentacleSecondaryRU = RightUpperTentacle.addOrReplaceChild("TentacleSecondaryRU", CubeListBuilder.create(), PartPose.offset(-1.5F, -0.5F, 3.3F));

        PartDefinition TentaclePart_r2 = TentacleSecondaryRU.addOrReplaceChild("TentaclePart_r2", CubeListBuilder.create().texOffs(69, 24).addBox(0.0F, -1.0F, 3.55F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.5F, 0.5F, -3.3F, 0.2269F, -0.6981F, -0.1134F));

        PartDefinition TentacleTertiaryRU = TentacleSecondaryRU.addOrReplaceChild("TentacleTertiaryRU", CubeListBuilder.create(), PartPose.offset(-2.4F, -0.7F, 2.7F));

        PartDefinition TentaclePart_r3 = TentacleTertiaryRU.addOrReplaceChild("TentaclePart_r3", CubeListBuilder.create().texOffs(12, 73).addBox(1.8F, -1.0F, 6.75F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.9F, 1.2F, -6.0F, 0.2967F, -0.9425F, -0.2094F));

        PartDefinition TentacleQuaternaryRU = TentacleTertiaryRU.addOrReplaceChild("TentacleQuaternaryRU", CubeListBuilder.create(), PartPose.offset(-3.0F, -0.5F, 2.2F));

        PartDefinition TentaclePart_r4 = TentacleQuaternaryRU.addOrReplaceChild("TentaclePart_r4", CubeListBuilder.create().texOffs(72, 0).addBox(4.45F, -1.0F, 9.375F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(6.9F, 1.7F, -8.2F, 0.4712F, -1.1868F, -0.4102F));

        PartDefinition TentaclePadRU = TentacleQuaternaryRU.addOrReplaceChild("TentaclePadRU", CubeListBuilder.create(), PartPose.offset(-3.6F, -0.4F, 1.3F));

        PartDefinition TentaclePart_r5 = TentaclePadRU.addOrReplaceChild("TentaclePart_r5", CubeListBuilder.create().texOffs(0, 19).addBox(6.075F, -1.5F, 16.4F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(16, 64).addBox(6.075F, -2.5F, 12.4F, 2.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(10.5F, 2.1F, -9.5F, 0.6807F, -1.2915F, -0.6283F));

        PartDefinition RightLowerTentacle = Torso.addOrReplaceChild("RightLowerTentacle", CubeListBuilder.create(), PartPose.offset(-2.5F, 6.7F, 1.0F));

        PartDefinition TentaclePart_r6 = RightLowerTentacle.addOrReplaceChild("TentaclePart_r6", CubeListBuilder.create().texOffs(76, 16).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.192F, -0.4276F, 0.0524F));

        PartDefinition TentacleSecondaryRL = RightLowerTentacle.addOrReplaceChild("TentacleSecondaryRL", CubeListBuilder.create(), PartPose.offset(-1.5F, 0.5F, 3.3F));

        PartDefinition TentaclePart_r7 = TentacleSecondaryRL.addOrReplaceChild("TentaclePart_r7", CubeListBuilder.create().texOffs(69, 54).addBox(0.0F, -1.0F, 3.6F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.5F, -0.5F, -3.3F, -0.2182F, -0.6894F, 0.1134F));

        PartDefinition TentacleTertiaryRL = TentacleSecondaryRL.addOrReplaceChild("TentacleTertiaryRL", CubeListBuilder.create(), PartPose.offset(-2.4F, 0.7F, 2.9F));

        PartDefinition TentaclePart_r8 = TentacleTertiaryRL.addOrReplaceChild("TentaclePart_r8", CubeListBuilder.create().texOffs(32, 75).addBox(1.9F, -1.05F, 6.8F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.9F, -1.2F, -6.2F, -0.2967F, -0.9425F, 0.2094F));

        PartDefinition TentacleQuaternaryRL = TentacleTertiaryRL.addOrReplaceChild("TentacleQuaternaryRL", CubeListBuilder.create(), PartPose.offset(-2.9F, 0.4F, 2.0F));

        PartDefinition TentaclePart_r9 = TentacleQuaternaryRL.addOrReplaceChild("TentaclePart_r9", CubeListBuilder.create().texOffs(75, 30).addBox(4.525F, -1.0F, 9.3F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(6.8F, -1.6F, -8.2F, -0.4712F, -1.1868F, 0.4102F));

        PartDefinition TentaclePadRL = TentacleQuaternaryRL.addOrReplaceChild("TentaclePadRL", CubeListBuilder.create(), PartPose.offset(-3.7F, 0.3F, 1.3F));

        PartDefinition TentaclePart_r10 = TentaclePadRL.addOrReplaceChild("TentaclePart_r10", CubeListBuilder.create().texOffs(77, 22).addBox(6.15F, -1.5F, 16.4F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(24, 69).addBox(6.15F, -2.5F, 12.4F, 2.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(10.5F, -1.9F, -9.5F, -0.6807F, -1.2915F, 0.6283F));

        PartDefinition LeftUpperTentacle = Torso.addOrReplaceChild("LeftUpperTentacle", CubeListBuilder.create(), PartPose.offset(2.5F, 2.7F, 1.0F));

        PartDefinition TentaclePart_r11 = LeftUpperTentacle.addOrReplaceChild("TentaclePart_r11", CubeListBuilder.create().texOffs(0, 72).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.192F, 0.4363F, 0.0524F));

        PartDefinition TentacleSecondaryLU = LeftUpperTentacle.addOrReplaceChild("TentacleSecondaryLU", CubeListBuilder.create(), PartPose.offset(1.5F, -0.5F, 3.3F));

        PartDefinition TentaclePart_r12 = TentacleSecondaryLU.addOrReplaceChild("TentaclePart_r12", CubeListBuilder.create().texOffs(68, 14).addBox(-2.0F, -1.0F, 3.55F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.5F, 0.5F, -3.3F, 0.2269F, 0.6981F, 0.1134F));

        PartDefinition TentacleTertiaryLU = TentacleSecondaryLU.addOrReplaceChild("TentacleTertiaryLU", CubeListBuilder.create(), PartPose.offset(2.4F, -0.7F, 2.7F));

        PartDefinition TentaclePart_r13 = TentacleTertiaryLU.addOrReplaceChild("TentaclePart_r13", CubeListBuilder.create().texOffs(66, 70).addBox(-3.8F, -1.0F, 6.75F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.9F, 1.2F, -6.0F, 0.2967F, 0.9425F, 0.2094F));

        PartDefinition TentacleQuaternaryLU = TentacleTertiaryLU.addOrReplaceChild("TentacleQuaternaryLU", CubeListBuilder.create(), PartPose.offset(3.0F, -0.5F, 2.2F));

        PartDefinition TentaclePart_r14 = TentacleQuaternaryLU.addOrReplaceChild("TentaclePart_r14", CubeListBuilder.create().texOffs(70, 61).addBox(-6.45F, -1.0F, 9.375F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-6.9F, 1.7F, -8.2F, 0.4712F, 1.1868F, 0.4102F));

        PartDefinition TentaclePadLU = TentacleQuaternaryLU.addOrReplaceChild("TentaclePadLU", CubeListBuilder.create(), PartPose.offset(3.6F, -0.4F, 1.3F));

        PartDefinition TentaclePart_r15 = TentaclePadLU.addOrReplaceChild("TentaclePart_r15", CubeListBuilder.create().texOffs(0, 0).addBox(-8.075F, -1.5F, 16.4F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(16, 37).addBox(-8.075F, -2.5F, 12.4F, 2.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-10.5F, 2.1F, -9.5F, 0.6807F, 1.2915F, 0.6283F));

        PartDefinition LeftLowerTentacle = Torso.addOrReplaceChild("LeftLowerTentacle", CubeListBuilder.create(), PartPose.offset(2.5F, 6.7F, 1.0F));

        PartDefinition TentaclePart_r16 = LeftLowerTentacle.addOrReplaceChild("TentaclePart_r16", CubeListBuilder.create().texOffs(74, 73).addBox(-1.0F, -1.0F, 0.0F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.192F, 0.4276F, -0.0524F));

        PartDefinition TentacleSecondaryLL = LeftLowerTentacle.addOrReplaceChild("TentacleSecondaryLL", CubeListBuilder.create(), PartPose.offset(1.5F, 0.5F, 3.3F));

        PartDefinition TentaclePart_r17 = TentacleSecondaryLL.addOrReplaceChild("TentaclePart_r17", CubeListBuilder.create().texOffs(36, 69).addBox(-2.0F, -1.0F, 3.6F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.5F, -0.5F, -3.3F, -0.2182F, 0.6894F, -0.1134F));

        PartDefinition TentacleTertiaryLL = TentacleSecondaryLL.addOrReplaceChild("TentacleTertiaryLL", CubeListBuilder.create(), PartPose.offset(2.4F, 0.7F, 2.9F));

        PartDefinition TentaclePart_r18 = TentacleTertiaryLL.addOrReplaceChild("TentaclePart_r18", CubeListBuilder.create().texOffs(74, 67).addBox(-3.9F, -1.05F, 6.8F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.9F, -1.2F, -6.2F, -0.2967F, 0.9425F, -0.2094F));

        PartDefinition TentacleQuaternaryLL = TentacleTertiaryLL.addOrReplaceChild("TentacleQuaternaryLL", CubeListBuilder.create(), PartPose.offset(2.9F, 0.4F, 2.0F));

        PartDefinition TentaclePart_r19 = TentacleQuaternaryLL.addOrReplaceChild("TentaclePart_r19", CubeListBuilder.create().texOffs(56, 74).addBox(-6.525F, -1.0F, 9.3F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-6.8F, -1.6F, -8.2F, -0.4712F, 1.1868F, -0.4102F));

        PartDefinition TentaclePadLL = TentacleQuaternaryLL.addOrReplaceChild("TentaclePadLL", CubeListBuilder.create(), PartPose.offset(3.7F, 0.3F, 1.3F));

        PartDefinition TentaclePart_r20 = TentaclePadLL.addOrReplaceChild("TentaclePart_r20", CubeListBuilder.create().texOffs(32, 52).addBox(-8.15F, -1.5F, 16.4F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(58, 65).addBox(-8.15F, -2.5F, 12.4F, 2.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-10.5F, -1.9F, -9.5F, -0.6807F, 1.2915F, -0.6283F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(16, 48).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(-5.2F, 3.9F, -0.2F));

        PartDefinition RightArm2 = partdefinition.addOrReplaceChild("RightArm2", CubeListBuilder.create().texOffs(40, 48).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(-5.2F, -0.1F, -0.2F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 37).addBox(-0.8F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(5.0F, 3.9F, -0.2F));

        PartDefinition LeftArm2 = partdefinition.addOrReplaceChild("LeftArm2", CubeListBuilder.create().texOffs(28, 36).addBox(-0.8F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(5.0F, -0.1F, -0.2F));

        return LayerDefinition.create(meshdefinition, 96, 96);
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
        this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    @Override
    public void setupAnim(LatexSquidDogFemale entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float HeadPitch) {
        animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, HeadPitch);
    }

    public PoseStack getPlacementCorrectors(CorrectorType type) {
        PoseStack corrector = LatexHumanoidModelInterface.super.getPlacementCorrectors(type);
        /*if (type.isArm())
            corrector.translate(0.0f, -6f / 16.0f, 0.0f);*/
        return corrector;
    }

    @Override
    public ModelPart getArm(HumanoidArm humanoidArm) {
        return switch (humanoidArm) {
            case LEFT -> LeftArm2;
            case RIGHT -> RightArm2;
        };
    }

    public ModelPart getLowerArm(HumanoidArm humanoidArm) {
        return switch (humanoidArm) {
            case LEFT -> LeftArm;
            case RIGHT -> RightArm;
        };
    }

    @Override
    public void translateToUpperHand(HumanoidArm arm, PoseStack poseStack) {
        this.getArm(arm).translateAndRotate(poseStack);
        poseStack.translate(0.0, (this.animator.armLength - 12.0f) / 20.0, 0.0);
    }

    @Override
    public void translateToLowerHand(HumanoidArm arm, PoseStack poseStack) {
        this.getLowerArm(arm).translateAndRotate(poseStack);
        poseStack.translate(0.0, (this.animator.armLength - 12.0f) / 20.0, 0.0);
    }

    @Override
    public void setupHand() {
        animator.setupHand();
    }

    @Override
    public LatexAnimator<LatexSquidDogFemale, LatexSquidDogFemaleModel> getAnimator() {
        return animator;
    }

    @Override
    public ModelPart getHead() {
        return Head;
    }

    public ModelPart getTorso() {
        return Torso;
    }
}
