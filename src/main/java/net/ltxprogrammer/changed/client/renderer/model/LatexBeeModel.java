package net.ltxprogrammer.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.beast.LatexBee;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class LatexBeeModel extends LatexHumanoidModel<LatexBee> implements LatexHumanoidModelInterface<LatexBee, LatexBeeModel> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_bee"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart RightArm2;
    private final ModelPart LeftArm2;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final ModelPart RightWing;
    private final ModelPart LeftWing;
    private final LatexAnimator<LatexBee, LatexBeeModel> animator;

    public LatexBeeModel(ModelPart root) {
        super(root);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Tail = Torso.getChild("Tail");
        this.RightArm = root.getChild("RightArm");
        this.RightArm2 = root.getChild("RightArm2");
        this.LeftArm = root.getChild("LeftArm");
        this.LeftArm2 = root.getChild("LeftArm2");
        this.RightWing = Torso.getChild("RightWing");
        this.LeftWing = Torso.getChild("LeftWing");
        animator = LatexAnimator.of(this).addPreset(AnimatorPresets.dragonLike(Head, Torso, LeftArm, RightArm, Tail, List.of(), LeftLeg, RightLeg, LeftWing, RightWing))
                .addPreset(AnimatorPresets.armSetTwo(LeftArm, RightArm, LeftArm2, RightArm2));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(28, 57).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(0, 47).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(48, 22).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 51).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(48, 29).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(48, 31).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 49).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(60, 49).addBox(-2.0F, -3.5F, -1.0F, 4.0F, 2.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(60, 19).addBox(-2.0F, -5.25F, -1.025F, 4.0F, 2.0F, 3.0F, new CubeDeformation(-0.2F))
                .texOffs(13, 60).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 3.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(12, 32).addBox(-1.99F, 6.0F, 0.0F, 4.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(42, 10).addBox(-1.99F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(44, 41).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(0, 56).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(52, 35).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(52, 37).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(40, 57).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(16, 53).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 56).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(12, 56).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(60, 44).addBox(-2.0F, -5.25F, -1.025F, 4.0F, 2.0F, 3.0F, new CubeDeformation(-0.2F))
                .texOffs(58, 13).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 3.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(24, 63).addBox(-2.0F, -3.5F, -1.0F, 4.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(64, 58).addBox(-2.01F, 6.0F, 0.0F, 4.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(12, 41).addBox(-2.01F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(28, 41).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.5F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(89, 0).addBox(-4.5F, -1.0F, -4.5F, 9.0F, 2.0F, 8.0F, new CubeDeformation(-0.25F))
                .texOffs(93, 10).addBox(-3.5F, 0.75F, -4.0F, 7.0F, 1.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition Head_r1 = Head.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(0, 34).addBox(3.75F, -7.5F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(24, 0).addBox(-0.25F, -6.5F, -2.5F, 3.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(0, 32).addBox(2.75F, -6.5F, -0.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(32, 10).addBox(-0.25F, -7.5F, -2.5F, 4.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(20, 16).addBox(-0.25F, -8.5F, -2.0F, 3.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.4363F));

        PartDefinition Head_r2 = Head.addOrReplaceChild("Head_r2", CubeListBuilder.create().texOffs(8, 45).addBox(-4.75F, -7.5F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(24, 19).addBox(-3.75F, -6.5F, -0.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(63, 54).addBox(-2.75F, -6.5F, -2.5F, 3.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(60, 0).addBox(-3.75F, -7.5F, -2.5F, 4.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(64, 61).addBox(-2.75F, -8.5F, -2.0F, 3.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 0.0F, 0.0F, -0.4363F));

        PartDefinition Head_r3 = Head.addOrReplaceChild("Head_r3", CubeListBuilder.create().texOffs(24, 4).addBox(1.75F, -13.25F, -2.075F, 1.0F, 1.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 0.4363F, -0.2182F, 0.0F));

        PartDefinition Head_r4 = Head.addOrReplaceChild("Head_r4", CubeListBuilder.create().texOffs(16, 35).addBox(1.75F, -13.0F, 2.0F, 1.0F, 5.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 0.5236F, -0.2182F, 0.0F));

        PartDefinition Head_r5 = Head.addOrReplaceChild("Head_r5", CubeListBuilder.create().texOffs(32, 31).addBox(-2.75F, -13.25F, -2.075F, 1.0F, 1.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 0.4363F, 0.2182F, 0.0F));

        PartDefinition Head_r6 = Head.addOrReplaceChild("Head_r6", CubeListBuilder.create().texOffs(18, 35).addBox(-2.75F, -13.0F, 2.0F, 1.0F, 5.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 0.5236F, 0.2182F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -0.5F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(56, 41).addBox(-4.0F, -0.5F, 1.5F, 8.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(78, 45).addBox(-3.0F, 2.5F, -3.0F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(74, 17).addBox(-4.0F, -0.5F, -3.0F, 8.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(76, 0).addBox(-2.0F, 3.5F, -2.5F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(75, 9).addBox(-3.0F, 1.5F, 1.5F, 6.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(64, 4).addBox(-3.0F, -0.5F, -3.5F, 6.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(32, 73).addBox(-2.0F, 1.5F, -3.5F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 42).addBox(-3.0F, -0.5F, 1.75F, 6.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition lefttuft_r1 = Torso.addOrReplaceChild("lefttuft_r1", CubeListBuilder.create().texOffs(0, 62).addBox(-11.5F, -23.5F, -3.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, 24.5F, 0.0F, 0.0F, 0.0F, 0.6109F));

        PartDefinition righttuft_r1 = Torso.addOrReplaceChild("righttuft_r1", CubeListBuilder.create().texOffs(60, 24).addBox(10.5F, -23.5F, -3.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, 24.5F, 0.0F, 0.0F, 0.0F, -0.6109F));

        PartDefinition LeftWing = Torso.addOrReplaceChild("LeftWing", CubeListBuilder.create(), PartPose.offset(2.0F, 4.0F, 0.0F));

        PartDefinition WingBase_r1 = LeftWing.addOrReplaceChild("WingBase_r1", CubeListBuilder.create().texOffs(0, 0).addBox(9.75F, -19.75F, 8.5F, 1.0F, 7.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(54, 57).addBox(5.75F, -20.75F, 8.5F, 4.0F, 9.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(42, 20).addBox(5.75F, -11.75F, 8.5F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(12, 66).addBox(5.75F, -21.75F, 7.25F, 3.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.0F, 21.5F, 0.0F, 0.2182F, 0.2182F, -0.2618F));

        PartDefinition RightWing = Torso.addOrReplaceChild("RightWing", CubeListBuilder.create(), PartPose.offset(-2.0F, 4.5F, 0.0F));

        PartDefinition WingBase_r2 = RightWing.addOrReplaceChild("WingBase_r2", CubeListBuilder.create().texOffs(4, 0).addBox(-10.75F, -19.75F, 8.5F, 1.0F, 7.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(44, 57).addBox(-9.75F, -20.75F, 8.5F, 4.0F, 9.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 45).addBox(-8.75F, -11.75F, 8.5F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(64, 64).addBox(-8.75F, -21.75F, 7.25F, 3.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 21.0F, 0.0F, 0.2182F, -0.2182F, 0.2618F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 10.5F, 0.0F));

        PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-0.5F, 6.0F, 0.5517F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(24, 16).addBox(-3.0F, -3.0F, -3.1983F, 6.0F, 9.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 6.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(64, 31).addBox(-1.5F, 1.0F, -1.0F, 3.0F, 3.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.4399F, 0.0F, 0.0F));

        PartDefinition RightArm2 = partdefinition.addOrReplaceChild("RightArm2", CubeListBuilder.create().texOffs(36, 31).addBox(-3.0F, -2.5F, -1.7F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(4, 73).addBox(-1.0F, -3.25F, -1.7F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.2F))
                .texOffs(48, 29).addBox(-3.0F, 3.0F, -1.7F, 4.0F, 2.0F, 4.0F, new CubeDeformation(-0.3F))
                .texOffs(0, 47).addBox(-3.0F, 4.5F, -1.7F, 4.0F, 5.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(52, 67).addBox(-0.7F, 8.75F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.3F))
                .texOffs(44, 67).addBox(-3.3F, 8.75F, 0.6F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.3F))
                .texOffs(67, 24).addBox(-3.3F, 8.75F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(-5.0F, 1.5F, -0.8F, 0.0F, 0.0F, 0.0873F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(48, 0).addBox(-3.0F, 6.5F, -1.2F, 4.0F, 5.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(32, 0).addBox(-3.0F, -0.5F, -1.2F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(52, 35).addBox(-3.0F, 5.0F, -1.2F, 4.0F, 2.0F, 4.0F, new CubeDeformation(-0.3F))
                .texOffs(44, 0).addBox(-0.7F, 10.75F, -1.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.3F))
                .texOffs(40, 41).addBox(-3.3F, 10.75F, 1.1F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.3F))
                .texOffs(24, 41).addBox(-3.3F, 10.75F, -1.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.3F)), PartPose.offset(-5.0F, 1.5F, -0.8F));

        PartDefinition LeftArm2 = partdefinition.addOrReplaceChild("LeftArm2", CubeListBuilder.create().texOffs(28, 68).addBox(-1.3F, 8.75F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.3F))
                .texOffs(20, 68).addBox(1.3F, 8.75F, 0.6F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.3F))
                .texOffs(60, 67).addBox(1.3F, 8.75F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.3F))
                .texOffs(16, 51).addBox(-1.0F, 4.5F, -1.7F, 4.0F, 5.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(20, 31).addBox(-1.0F, -2.5F, -1.7F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(20, 73).addBox(-1.0F, -3.25F, -1.7F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.2F))
                .texOffs(32, 51).addBox(-1.0F, 3.0F, -1.7F, 4.0F, 2.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(5.0F, 1.5F, -0.8F, 0.0F, 0.0F, -0.0873F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(38, 65).addBox(-1.3F, 10.75F, -1.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.3F))
                .texOffs(44, 51).addBox(1.3F, 10.75F, 1.1F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.3F))
                .texOffs(28, 51).addBox(1.3F, 10.75F, -1.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.3F))
                .texOffs(48, 20).addBox(-1.0F, 6.5F, -1.2F, 4.0F, 5.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 32).addBox(-1.0F, -0.5F, -1.2F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(48, 51).addBox(-1.0F, 5.0F, -1.2F, 4.0F, 2.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offset(5.0F, 1.5F, -0.8F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void prepareMobModel(LatexBee p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LatexBee entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public PoseStack getPlacementCorrectors(CorrectorType type) {
        PoseStack corrector = LatexHumanoidModelInterface.super.getPlacementCorrectors(type);
        if (type.isArm())
            corrector.translate(0.0f, -5.2f / 17.5f, 0.02f);
        return corrector;
    }

    @Override
    public ModelPart getArm(HumanoidArm humanoidArm) {
        return switch (humanoidArm) {
            case LEFT -> LeftArm;
            case RIGHT -> RightArm;
        };
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
        RightArm2.render(poseStack, buffer, packedLight, packedOverlay);
        LeftArm.render(poseStack, buffer, packedLight, packedOverlay);
        LeftArm2.render(poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public LatexAnimator<LatexBee, LatexBeeModel> getAnimator() {
        return animator;
    }
}