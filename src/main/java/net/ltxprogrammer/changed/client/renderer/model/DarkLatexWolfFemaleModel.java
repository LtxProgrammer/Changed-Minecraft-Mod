package net.ltxprogrammer.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.beast.DarkLatexWolfFemale;
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
public class DarkLatexWolfFemaleModel extends LatexHumanoidModel<DarkLatexWolfFemale> implements LatexHumanoidModelInterface<DarkLatexWolfFemale, DarkLatexWolfFemaleModel> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("dark_latex_wolf_female"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final LatexAnimator<DarkLatexWolfFemale, DarkLatexWolfFemaleModel> animator;

    public DarkLatexWolfFemaleModel(ModelPart root) {
        super(root);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Tail = Torso.getChild("Tail");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        animator = LatexAnimator.of(this).addPreset(AnimatorPresets.wolfLike(Head, Torso, LeftArm, RightArm, Tail, List.of(), LeftLeg, RightLeg));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(43, 42).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-2.5F, 10.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(40, 11).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.17F))
                .texOffs(40, 11).addBox(-2.0F, 3.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(16, 55).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 55).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 55).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(32, 0).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(32, 0).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(29, 42).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(-0.005F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(16, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(12, 44).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(2.5F, 10.0F, 0.0F));

        PartDefinition LeftUpperLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLowerLeg_r1", CubeListBuilder.create().texOffs(32, 32).addBox(-2.0F, 3.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.1F))
                .texOffs(32, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.17F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(12, 55).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(12, 55).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(12, 55).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(12, 34).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(12, 34).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(12, 34).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(40, 21).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(-0.005F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(24, 0).addBox(-1.5F, -1.0F, -6.0F, 3.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create().texOffs(36, 60).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(32, 57).addBox(-0.5F, -1.6F, -0.4F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(50, 38).addBox(-0.5F, -2.1F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(49, 6).addBox(0.5F, -2.993F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0499F)), PartPose.offsetAndRotation(-3.1F, -7.25F, 0.0F, 0.0F, 0.0F, -0.2618F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create().texOffs(0, 58).addBox(-1.0F, -1.0F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(12, 59).addBox(-1.0F, -1.605F, -0.4F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(45, 49).addBox(-1.0F, -2.1F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(38, 52).addBox(-1.0F, -3.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0499F)), PartPose.offsetAndRotation(2.6F, -7.375F, 0.0F, 0.0F, 0.0F, 0.2618F));

        PartDefinition Mask = Head.addOrReplaceChild("Mask", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -7.0F, -5.0F, 2.0F, 5.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(32, 32).addBox(1.0F, -6.0F, -5.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(28, 32).addBox(-2.0F, -6.0F, -5.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(40, 11).addBox(-3.0F, -5.0F, -5.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(37, 12).addBox(2.0F, -5.0F, -5.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 16).addBox(3.0F, -4.0F, -5.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 32).addBox(-4.0F, -4.0F, -5.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(36, 17).addBox(-3.0F, -2.0F, -5.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(36, 15).addBox(2.0F, -2.0F, -5.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(32, 11).addBox(2.0F, -1.0F, -5.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 6).addBox(-4.0F, -1.0F, -5.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(48, 0).addBox(-2.0F, -2.0F, -7.0F, 4.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition WaistLower_r1 = Torso.addOrReplaceChild("WaistLower_r1", CubeListBuilder.create().texOffs(0, 26).addBox(-4.0F, -2.0F, -2.0F, 8.0F, 2.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 26).addBox(-4.0F, -3.0F, -2.0F, 8.0F, 2.0F, 4.0F, new CubeDeformation(0.1F))
                .texOffs(0, 20).addBox(-4.0F, -8.0F, -2.0F, 8.0F, 8.0F, 4.0F, new CubeDeformation(-0.3F))
                .texOffs(0, 16).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

        PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(0, 48).addBox(-1.5F, 0.1914F, -1.4483F, 3.0F, 6.0F, 3.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, 2.0F, 6.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition Base_r3 = Tail.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(16, 68).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition Plantoids = Torso.addOrReplaceChild("Plantoids", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Center_r1 = Plantoids.addOrReplaceChild("Center_r1", CubeListBuilder.create().texOffs(5, 63).addBox(-1.0F, -11.5F, -2.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftPlantoid_r1 = Plantoids.addOrReplaceChild("LeftPlantoid_r1", CubeListBuilder.create().texOffs(21, 63).addBox(0.75F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.4F))
                .texOffs(21, 59).addBox(-3.75F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.4F)), PartPose.offsetAndRotation(0.0F, 3.0F, -2.0F, -0.1047F, 0.0F, 0.0F));

        PartDefinition LowerHair = Torso.addOrReplaceChild("LowerHair", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Base_r4 = LowerHair.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(1, 17).addBox(2.5F, -1.0F, -3.0F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(1, 17).addBox(-4.5F, -1.0F, -3.0F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(7, 18).addBox(-4.5F, 0.0F, -3.0F, 2.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(7, 18).addBox(2.5F, 0.0F, -3.0F, 2.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 32).addBox(-3.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(16, 34).addBox(0.0F, 10.75F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 34).addBox(-3.0F, 10.75F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 34).addBox(-3.0F, 10.75F, -0.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 34).addBox(-3.0F, 10.75F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 0.0F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(24, 16).addBox(-1.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(16, 34).addBox(-1.0F, 10.75F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 34).addBox(2.0F, 10.75F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 34).addBox(2.0F, 10.75F, -0.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 34).addBox(2.0F, 10.75F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void prepareMobModel(DarkLatexWolfFemale p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull DarkLatexWolfFemale entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public PoseStack getPlacementCorrectors(CorrectorType type) {
        PoseStack corrector = LatexHumanoidModelInterface.super.getPlacementCorrectors(type);
        if (type == CorrectorType.HAIR)
            corrector.translate(0.0f, 0.5f / 15.0f, 0.0f);
        else if (type == CorrectorType.LOWER_HAIR)
            corrector.translate(0.0f, -0.5f / 16.0f, -0.025f);
        return corrector;
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
    public LatexAnimator<DarkLatexWolfFemale, DarkLatexWolfFemaleModel> getAnimator() {
        return animator;
    }
}