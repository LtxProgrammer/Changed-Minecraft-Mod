package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.beast.LatexStiger;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;

import java.util.List;

public class LatexStigerModel extends LatexHumanoidModel<LatexStiger> implements LatexHumanoidModelInterface<LatexStiger, LatexStigerModel> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_stiger"), "main");
    public final ModelPart Head;
    public final ModelPart Torso;
    public final ModelPart RightArm;
    public final ModelPart RightArm2;
    public final ModelPart RightArm3;
    public final ModelPart LeftArm;
    public final ModelPart LeftArm2;
    public final ModelPart LeftArm3;
    public final ModelPart LeftLeg;
    public final ModelPart RightLeg;
    public final LatexAnimator<LatexStiger, LatexStigerModel> animator;

    public LatexStigerModel(ModelPart root) {
        super(root);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.RightArm = root.getChild("RightArm");
        this.RightArm2 = root.getChild("RightArm2");
        this.RightArm3 = root.getChild("RightArm3");
        this.LeftArm = root.getChild("LeftArm");
        this.LeftArm2 = root.getChild("LeftArm2");
        this.LeftArm3 = root.getChild("LeftArm3");
        animator = LatexAnimator.of(this).addPreset(AnimatorPresets.wolfLikeOld(Head, Torso, LeftArm, RightArm, Torso.getChild("Tail"), List.of(), LeftLeg, RightLeg))
                .addPreset(AnimatorPresets.armSetTwo(LeftArm, RightArm, LeftArm2, RightArm2))
                .addPreset(AnimatorPresets.armSetThree(LeftArm, RightArm, LeftArm3, RightArm3));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(62, 26).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(0, 48).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(12, 48).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 50).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(48, 36).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(48, 38).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(61, 13).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(52, 32).addBox(-1.99F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(16, 59).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(56, 61).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(12, 50).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(32, 50).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(40, 52).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(51, 16).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(52, 30).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(52, 32).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(60, 52).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(28, 52).addBox(-2.01F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(44, 55).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.5F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(24, 0).addBox(-2.0F, -2.5F, -6.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 5).addBox(-1.5F, -0.5F, -5.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition leftupperantenna_r1 = Head.addOrReplaceChild("leftupperantenna_r1", CubeListBuilder.create().texOffs(0, 32).addBox(2.0F, -29.25F, -25.1F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 26.5F, 0.0F, -0.6981F, 0.0873F, 0.0F));

        PartDefinition leftlowerantenna_r1 = Head.addOrReplaceChild("leftlowerantenna_r1", CubeListBuilder.create().texOffs(0, 0).addBox(1.5F, -34.0F, -13.6F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 26.5F, 0.0F, -0.3054F, 0.0873F, 0.0F));

        PartDefinition rightupperantenna_r1 = Head.addOrReplaceChild("rightupperantenna_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-3.0F, -29.25F, -25.1F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 26.5F, 0.0F, -0.6981F, -0.0873F, 0.0F));

        PartDefinition rightlowerantenna_r1 = Head.addOrReplaceChild("rightlowerantenna_r1", CubeListBuilder.create().texOffs(16, 32).addBox(-3.5F, -34.0F, -13.6F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 26.5F, 0.0F, -0.3054F, -0.0873F, 0.0F));

        PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(47, 1).addBox(-1.0F, -29.625F, -0.95F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.5F, 26.5F, 0.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.0F, -7.5F, 0.0F, 0.0F, 0.0F, -0.2618F));

        PartDefinition rightear_r1 = RightEar.addOrReplaceChild("rightear_r1", CubeListBuilder.create().texOffs(63, 67).addBox(2.25F, -35.0F, -1.0F, 2.0F, 4.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.0F, 33.0F, 0.0F, 0.0F, 0.0F, -0.2182F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offsetAndRotation(2.5F, -7.5F, 0.0F, 0.0F, 0.0F, 0.2618F));

        PartDefinition leftear_r1 = LeftEar.addOrReplaceChild("leftear_r1", CubeListBuilder.create().texOffs(53, 67).addBox(-4.25F, -35.0F, -1.0F, 2.0F, 4.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.5F, 33.0F, 0.0F, 0.0F, 0.0F, 0.2182F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(16, 31).addBox(-2.0F, -34.75F, -4.0F, 4.0F, 1.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(32, 31).addBox(-4.0F, -34.5F, -3.5F, 8.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(10, 69).addBox(2.0F, -34.75F, 0.0F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(48, 16).addBox(2.5F, -34.0F, -3.0F, 2.0F, 3.0F, 7.0F, new CubeDeformation(0.025F))
                .texOffs(24, 18).addBox(3.5F, -34.0F, -4.5F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(24, 4).addBox(-4.5F, -34.0F, -4.5F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 64).addBox(2.5F, -31.0F, -1.0F, 2.0F, 2.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(32, 63).addBox(-4.5F, -31.0F, -1.0F, 2.0F, 2.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(45, 45).addBox(-4.5F, -34.0F, -3.0F, 2.0F, 3.0F, 7.0F, new CubeDeformation(0.025F))
                .texOffs(68, 35).addBox(-4.0F, -34.75F, 0.0F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(56, 50).addBox(-4.0F, -34.0F, -4.5F, 8.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(56, 43).addBox(-4.0F, -34.0F, 4.0F, 8.0F, 6.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(64, 10).addBox(-3.5F, -33.0F, -4.25F, 7.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 26.5F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.5F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(60, 0).addBox(-4.0F, 0.5F, 1.5F, 8.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(68, 61).addBox(-3.0F, 3.25F, -2.5F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(48, 26).addBox(-4.0F, 0.5F, -2.5F, 8.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(68, 40).addBox(-3.0F, 2.5F, 1.5F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(66, 22).addBox(-3.0F, 0.5F, -2.75F, 6.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(20, 16).addBox(-2.0F, 2.5F, -2.75F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(64, 32).addBox(-3.0F, 0.5F, 1.75F, 6.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition lefttuft_r1 = Torso.addOrReplaceChild("lefttuft_r1", CubeListBuilder.create().texOffs(41, 65).addBox(-11.5F, -23.5F, -2.5F, 1.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 25.5F, 0.0F, 0.0F, 0.0F, 0.6109F));

        PartDefinition righttuft_r1 = Torso.addOrReplaceChild("righttuft_r1", CubeListBuilder.create().texOffs(64, 3).addBox(10.5F, -23.5F, -2.5F, 1.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 25.5F, 0.0F, 0.0F, 0.0F, -0.6109F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 11.5F, 0.0F));

        PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(24, 16).addBox(-3.0F, -3.0F, -3.1983F, 6.0F, 9.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 6.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(42, 16).addBox(-1.5F, 1.0F, -1.0F, 3.0F, 3.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.4399F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 32).addBox(-3.0F, -1.5F, -1.7F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(32, 46).addBox(0.0F, 10.25F, -1.7F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(32, 44).addBox(-3.0F, 10.25F, 1.3F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(44, 2).addBox(-3.0F, 10.25F, -0.2F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(44, 0).addBox(-3.0F, 10.25F, -1.7F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-5.0F, 0.5F, -0.8F, 0.0F, 0.0F, 0.1745F));

        PartDefinition RightArm2 = partdefinition.addOrReplaceChild("RightArm2", CubeListBuilder.create().texOffs(36, 36).addBox(-3.0F, -1.5F, -1.2F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(36, 36).addBox(0.0F, 10.25F, -1.2F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(32, 36).addBox(-3.0F, 10.25F, 1.8F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(12, 34).addBox(-3.0F, 10.25F, 0.3F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(32, 33).addBox(-3.0F, 10.25F, -1.2F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-5.0F, 3.0F, -0.8F, 0.0F, 0.0F, 0.0873F));

        PartDefinition RightArm3 = partdefinition.addOrReplaceChild("RightArm3", CubeListBuilder.create().texOffs(32, 0).addBox(-3.0F, -1.5F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(32, 31).addBox(0.0F, 10.25F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(12, 32).addBox(-3.0F, 10.25F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(28, 4).addBox(-3.0F, 10.25F, -0.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(20, 18).addBox(-3.0F, 10.25F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 4.5F, 0.5F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(16, 56).addBox(-1.0F, 10.25F, -1.7F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(44, 55).addBox(2.0F, 10.25F, 1.3F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(40, 54).addBox(2.0F, 10.25F, -0.2F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(52, 43).addBox(2.0F, 10.25F, -1.7F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(48, 0).addBox(-1.0F, -1.5F, -1.7F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(5.0F, 0.5F, -0.8F, 0.0F, 0.0F, -0.1745F));

        PartDefinition LeftArm2 = partdefinition.addOrReplaceChild("LeftArm2", CubeListBuilder.create().texOffs(32, 42).addBox(-1.0F, 10.25F, -1.2F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(28, 42).addBox(2.0F, 10.25F, 1.8F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 42).addBox(2.0F, 10.25F, 0.3F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(32, 40).addBox(2.0F, 10.25F, -1.2F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 48).addBox(-1.0F, -1.5F, -1.2F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(5.0F, 3.0F, -0.8F, 0.0F, 0.0F, -0.0873F));

        PartDefinition LeftArm3 = partdefinition.addOrReplaceChild("LeftArm3", CubeListBuilder.create().texOffs(28, 40).addBox(-1.0F, 10.25F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 40).addBox(2.0F, 10.25F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(20, 37).addBox(2.0F, 10.25F, -0.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 37).addBox(2.0F, 10.25F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 40).addBox(-1.0F, -1.5F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 4.5F, 0.5F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green,
                               float blue, float alpha) {
        RightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        LeftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        Torso.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        RightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        RightArm2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        RightArm3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        LeftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        LeftArm2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        LeftArm3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void prepareMobModel(LatexStiger p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    @Override
    public void setupAnim(LatexStiger entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float HeadPitch) {
        animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, HeadPitch);
    }

    public PoseStack getPlacementCorrectors(CorrectorType type) {
        PoseStack corrector = LatexHumanoidModelInterface.super.getPlacementCorrectors(type);
        if (type.isArm())
            corrector.translate(0.0f, -2.0f / 8.5f, 0.04f);
        else if (type == CorrectorType.HAIR)
            corrector.translate(0.0f, -1.0f / 16.0f, 0.0f);
        return corrector;
    }

    @Override
    public ModelPart getArm(HumanoidArm humanoidArm) {
        return switch (humanoidArm) {
            case LEFT -> LeftArm;
            case RIGHT -> RightArm;
        };
    }

    public ModelPart getLeg(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.LeftLeg : this.RightLeg;
    }

    @Override
    public void setupHand() {
        animator.setupHand();
    }

    @Override
    public LatexAnimator<LatexStiger, LatexStigerModel> getAnimator() {
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
