package net.ltxprogrammer.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.beast.LatexPinkYuinDragon;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class LatexPinkYuinDragonModel extends LatexHumanoidModel<LatexPinkYuinDragon> implements LatexHumanoidModelInterface {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_pink_yuin_dragon"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final ModelPart RightWing;
    private final ModelPart LeftWing;
    private final LatexHumanoidModelController controller;

    public LatexPinkYuinDragonModel(ModelPart root) {
        super(root);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Tail = Torso.getChild("Tail");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        this.RightWing = Torso.getChild("rightwing");
        this.LeftWing = Torso.getChild("leftwing");
        controller = LatexHumanoidModelController.Builder.of(this, Head, Torso, Tail, RightArm, LeftArm, RightLeg, LeftLeg).wings(RightWing, LeftWing).build();
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(30, 55).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(36, 17).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(36, 19).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(51, 1).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(28, 44).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(44, 45).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(28, 47).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(52, 0).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(44, 14).addBox(-1.99F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(44, 45).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(14, 55).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(14, 55).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(30, 55).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(14, 57).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(46, 55).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(56, 14).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(56, 16).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(0, 52).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(12, 44).addBox(-2.01F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(28, 45).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -34.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(12, 32).addBox(-2.0F, -29.0F, -6.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(44, 2).addBox(-1.5F, -27.0F, -5.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition lefthorn_r1 = Head.addOrReplaceChild("lefthorn_r1", CubeListBuilder.create().texOffs(62, 25).addBox(1.5F, -24.85F, -24.25F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(28, 61).addBox(1.5F, -27.0F, -25.25F, 2.0F, 5.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, -0.7854F, -0.0873F, 0.0F));

        PartDefinition righthorn_r1 = Head.addOrReplaceChild("righthorn_r1", CubeListBuilder.create().texOffs(56, 46).addBox(-3.5F, -24.85F, -24.25F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(34, 61).addBox(-3.5F, -27.0F, -25.25F, 2.0F, 5.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, -0.7854F, 0.0873F, 0.0F));

        PartDefinition Head_r1 = Head.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(24, 44).addBox(-2.4F, -4.25F, -1.25F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -26.0F, 0.0F, 0.1745F, -0.3927F, -0.8727F));

        PartDefinition Head_r2 = Head.addOrReplaceChild("Head_r2", CubeListBuilder.create().texOffs(40, 45).addBox(1.4F, -4.25F, -1.25F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -26.0F, 0.0F, 0.1745F, 0.3927F, 0.8727F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(24, 8).addBox(-2.0F, -34.9F, -4.0F, 4.0F, 1.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(24, 0).addBox(-4.0F, -34.5F, -3.5F, 8.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(8, 61).addBox(2.0F, -34.75F, 0.0F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(16, 33).addBox(2.5F, -34.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.025F))
                .texOffs(40, 61).addBox(3.5F, -31.0F, -3.25F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.025F))
                .texOffs(40, 5).addBox(-4.5F, -31.0F, -3.25F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.025F))
                .texOffs(56, 44).addBox(-3.5F, -33.0F, -4.25F, 7.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(24, 0).addBox(3.5F, -34.0F, -4.5F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 16).addBox(-4.5F, -34.0F, -4.5F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(55, 50).addBox(2.5F, -31.0F, -1.0F, 2.0F, 2.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(46, 55).addBox(-4.5F, -31.0F, -1.0F, 2.0F, 2.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(32, 25).addBox(-4.5F, -34.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.025F))
                .texOffs(60, 20).addBox(-4.0F, -34.75F, 0.0F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(24, 5).addBox(-4.0F, -34.0F, -4.5F, 8.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(52, 9).addBox(-1.0F, -32.25F, -4.25F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(44, 25).addBox(-4.0F, -34.0F, 4.0F, 8.0F, 6.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition LeftEar = Hair.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offsetAndRotation(-16.25F, -9.35F, -1.0F, 0.0F, 0.0F, 0.6109F));

        PartDefinition cube_r1 = LeftEar.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(28, 36).addBox(-11.5F, -28.25F, -0.75F, 4.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(0, 5).addBox(-11.5F, -28.25F, -1.2F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(26, 55).addBox(-7.75F, -28.25F, -0.25F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(20, 17).addBox(-11.5F, -28.25F, 1.7F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

        PartDefinition RightEar = Hair.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offsetAndRotation(16.25F, -9.35F, -1.0F, 0.0F, 0.0F, -0.6109F));

        PartDefinition cube_r2 = RightEar.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(54, 40).addBox(7.5F, -28.25F, -0.75F, 4.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(40, 14).addBox(8.5F, -28.25F, 1.7F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(42, 55).addBox(6.75F, -28.25F, -0.25F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(44, 0).addBox(8.5F, -28.25F, -1.2F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -25.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition rightwing = Torso.addOrReplaceChild("rightwing", CubeListBuilder.create(), PartPose.offset(-2.0F, -21.0F, 2.0F));

        PartDefinition wingbase_r1 = rightwing.addOrReplaceChild("wingbase_r1", CubeListBuilder.create().texOffs(48, 40).addBox(-10.0F, -26.0F, 0.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 61).addBox(-11.0F, -26.0F, 0.0F, 1.0F, 16.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(24, 61).addBox(-8.0F, -25.0F, 0.0F, 1.0F, 12.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(28, 33).addBox(-7.0F, -24.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(2, 32).addBox(-6.0F, -22.0F, 0.5F, 1.0F, 4.0F, 0.0F, CubeDeformation.NONE)
                .texOffs(42, 17).addBox(-7.0F, -23.0F, 0.5F, 1.0F, 8.0F, 0.0F, CubeDeformation.NONE)
                .texOffs(4, 61).addBox(-10.0F, -25.0F, 0.5F, 2.0F, 15.0F, 0.0F, CubeDeformation.NONE)
                .texOffs(16, 36).addBox(-5.0F, -22.0F, 0.0F, 3.0F, 4.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 48).addBox(-6.0F, -23.0F, 0.0F, 5.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 21.0F, -2.0F, 0.0F, 0.6545F, 0.0F));

        PartDefinition leftwing = Torso.addOrReplaceChild("leftwing", CubeListBuilder.create(), PartPose.offset(2.0F, -21.0F, 0.0F));

        PartDefinition wingbase_r2 = leftwing.addOrReplaceChild("wingbase_r2", CubeListBuilder.create().texOffs(36, 40).addBox(8.0F, -26.0F, 0.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(60, 57).addBox(10.0F, -26.0F, 0.0F, 1.0F, 16.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(20, 61).addBox(7.0F, -25.0F, 0.0F, 1.0F, 12.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(24, 19).addBox(6.0F, -24.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 32).addBox(5.0F, -22.0F, 0.5F, 1.0F, 4.0F, 0.0F, CubeDeformation.NONE)
                .texOffs(40, 17).addBox(6.0F, -23.0F, 0.5F, 1.0F, 8.0F, 0.0F, CubeDeformation.NONE)
                .texOffs(60, 9).addBox(8.0F, -25.0F, 0.5F, 2.0F, 15.0F, 0.0F, CubeDeformation.NONE)
                .texOffs(0, 0).addBox(2.0F, -22.0F, 0.0F, 3.0F, 4.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 50).addBox(1.0F, -23.0F, 0.0F, 5.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.0F, 21.0F, 0.0F, 0.0F, -0.6545F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, -14.0F, 0.0F));

        PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(60, 28).addBox(-1.0F, -10.35F, 13.925F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 14.0F, 0.0F, 0.0436F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(36, 36).addBox(-1.5F, -13.2F, 6.9F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 14.0F, 0.0F, -0.1309F, 0.0F, 0.0F));

        PartDefinition Base_r3 = Tail.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(40, 5).addBox(-1.5F, -1.375F, -2.6983F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(0.0F, 2.0F, 6.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition Base_r4 = Tail.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(48, 32).addBox(-2.0F, -2.8F, 0.4F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(24, 17).addBox(-8.0F, -26.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(8, 61).addBox(-5.0F, -14.25F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(60, 34).addBox(-8.0F, -14.25F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(55, 57).addBox(-8.0F, -14.25F, -0.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(47, 57).addBox(-8.0F, -14.25F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 32).addBox(4.0F, -26.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(62, 46).addBox(4.0F, -14.25F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(46, 62).addBox(7.0F, -14.25F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(61, 48).addBox(7.0F, -14.25F, -0.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 61).addBox(7.0F, -14.25F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 25.0F, 0.0F));

        return LayerDefinition.create(process(meshdefinition), 128, 128);
    }

    @Override
    public void prepareMobModel(LatexPinkYuinDragon p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(controller, p_102861_, p_102862_, p_102863_, p_102864_);
    }


    public void setupHand() {
        controller.setupHand();
    }


    @Override
    public void setupAnim(@NotNull LatexPinkYuinDragon entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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


    @Override
    public LatexHumanoidModelController getController() {
        return controller;
    }
}