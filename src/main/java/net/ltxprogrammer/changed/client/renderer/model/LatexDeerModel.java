package net.ltxprogrammer.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.beast.LatexDeer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class LatexDeerModel extends LatexHumanoidModel<LatexDeer> implements LatexHumanoidModelInterface {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_deer"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final LatexHumanoidModelController controller;

    public LatexDeerModel(ModelPart root) {
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

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(0, 50).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(30, 44).addBox(-2.5F, -1.1F, -0.17F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(24, 46).addBox(-1.11F, -1.1F, -0.17F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(52, 6).addBox(-3.89F, -1.1F, -0.17F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.8727F, 0.0F, 0.0F));

        PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(39, 47).addBox(-1.11F, -3.1F, -1.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(4, 48).addBox(-2.5F, -3.1F, -1.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(8, 48).addBox(-3.89F, -3.1F, -1.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(42, 47).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(40, 5).addBox(-1.99F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(44, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(48, 33).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(22, 54).addBox(-2.5F, -1.1F, -0.17F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(55, 7).addBox(-1.11F, -1.1F, -0.17F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(22, 56).addBox(-3.89F, -1.1F, -0.17F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.8727F, 0.0F, 0.0F));

        PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(0, 56).addBox(-1.11F, -3.1F, -1.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(7, 56).addBox(-2.5F, -3.1F, -1.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(11, 56).addBox(-3.89F, -3.1F, -1.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(28, 47).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(36, 36).addBox(-2.01F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(12, 44).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -34.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(12, 32).addBox(-2.0F, -29.0F, -6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(16, 38).addBox(-1.5F, -27.0F, -5.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition Antler_r1 = Head.addOrReplaceChild("Antler_r1", CubeListBuilder.create().texOffs(18, 54).addBox(-2.0F, -34.5F, 9.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.3491F, 0.0873F, 0.1309F));

        PartDefinition Antler_r2 = Head.addOrReplaceChild("Antler_r2", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, -35.0F, 3.75F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-2.0F, -38.0F, 4.75F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.1745F, 0.0873F, 0.1309F));

        PartDefinition Antler_r3 = Head.addOrReplaceChild("Antler_r3", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, -37.5F, -10.25F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, -0.2618F, 0.0873F, 0.1309F));

        PartDefinition Antler_r4 = Head.addOrReplaceChild("Antler_r4", CubeListBuilder.create().texOffs(0, 16).addBox(1.0F, -34.5F, 9.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.3491F, -0.0873F, -0.1309F));

        PartDefinition Antler_r5 = Head.addOrReplaceChild("Antler_r5", CubeListBuilder.create().texOffs(24, 0).addBox(1.0F, -35.0F, 3.75F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(4, 0).addBox(1.0F, -38.0F, 4.75F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, 0.1745F, -0.0873F, -0.1309F));

        PartDefinition Antler_r6 = Head.addOrReplaceChild("Antler_r6", CubeListBuilder.create().texOffs(52, 39).addBox(1.0F, -37.5F, -10.25F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0F, 0.0F, -0.2618F, -0.0873F, -0.1309F));

        PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(24, 19).addBox(-1.0F, -29.625F, -0.95F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, 0.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offsetAndRotation(13.25F, -7.0F, -1.0F, 0.0F, 0.0F, -0.48F));

        PartDefinition cube_r1 = RightEar.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(53, 46).addBox(7.5F, -28.25F, -0.75F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(16, 36).addBox(8.5F, -28.25F, 1.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 38).addBox(6.75F, -28.25F, -0.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(20, 17).addBox(8.5F, -28.25F, -1.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offsetAndRotation(-13.25F, -7.0F, -1.0F, 0.0F, 0.0F, 0.48F));

        PartDefinition cube_r2 = LeftEar.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(56, 13).addBox(-11.5F, -28.25F, -0.75F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(36, 19).addBox(-11.5F, -28.25F, -1.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(34, 36).addBox(-7.75F, -28.25F, -0.25F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(28, 36).addBox(-11.5F, -28.25F, 1.5F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(24, 8).addBox(-2.0F, -34.9F, -4.0F, 4.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(24, 0).addBox(-4.0F, -34.5F, -3.5F, 8.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(31, 59).addBox(2.0F, -34.75F, 0.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(16, 33).addBox(2.5F, -34.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.025F))
                .texOffs(56, 17).addBox(-3.5F, -33.0F, -4.25F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 21).addBox(3.5F, -34.0F, -4.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 33).addBox(-4.5F, -34.0F, -4.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(52, 39).addBox(2.5F, -31.0F, -1.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(51, 51).addBox(-4.5F, -31.0F, -1.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(32, 25).addBox(-4.5F, -34.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.025F))
                .texOffs(55, 58).addBox(-4.0F, -34.75F, 0.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(52, 4).addBox(-4.0F, -34.0F, -4.5F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(24, 44).addBox(-1.0F, -32.25F, -4.25F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 26).addBox(-4.0F, -34.0F, 4.0F, 8.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -25.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(36, 17).addBox(-2.0F, -23.0F, -2.75F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(60, 21).addBox(-3.0F, -23.0F, 1.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(45, 58).addBox(-3.0F, -25.0F, -2.75F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(60, 19).addBox(-3.0F, -22.0F, -2.5F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 0).addBox(-4.0F, -25.0F, -2.5F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(24, 5).addBox(-4.0F, -25.0F, 1.5F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 56).addBox(-3.0F, -25.0F, 1.75F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition LeftTuft_r1 = Torso.addOrReplaceChild("LeftTuft_r1", CubeListBuilder.create().texOffs(11, 54).addBox(-11.5F, -23.5F, -2.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.6109F));

        PartDefinition RightTuft_r1 = Torso.addOrReplaceChild("RightTuft_r1", CubeListBuilder.create().texOffs(0, 56).addBox(10.5F, -23.5F, -2.5F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.6109F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, -14.0F, 0.0F));

        PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(56, 6).addBox(-1.5F, -2.5586F, -0.6983F, 3.0F, 4.0F, 3.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, 2.0F, 6.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(23, 56).addBox(-1.5F, 1.0F, -1.0F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(24, 17).addBox(-8.0F, -26.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(39, 59).addBox(-5.11F, -14.25F, -1.89F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(7, 58).addBox(-7.89F, -14.25F, 0.89F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(0, 58).addBox(-7.89F, -14.25F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(46, 56).addBox(-7.89F, -14.25F, -1.89F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 32).addBox(4.0F, -26.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(42, 60).addBox(4.11F, -14.25F, -1.89F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(60, 35).addBox(6.89F, -14.25F, 0.89F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(60, 33).addBox(6.89F, -14.25F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(60, 23).addBox(6.89F, -14.25F, -1.89F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 25.0F, 0.0F));

        return LayerDefinition.create(process(meshdefinition), 128, 128);
    }


    @Override
    public void prepareMobModel(LatexDeer p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(controller, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        controller.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LatexDeer entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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