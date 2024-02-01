package net.ltxprogrammer.changed.client.renderer.model;

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

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class LatexBeeModel extends LatexHumanoidModel<LatexBee> implements LatexHumanoidModelInterface<LatexBee, LatexBeeModel>, DoubleArmedModel {
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
        this.RightArm = root.getChild("RightArm");
        this.RightArm2 = root.getChild("RightArm2");
        this.LeftArm = root.getChild("LeftArm");
        this.LeftArm2 = root.getChild("LeftArm2");
        this.Tail = Torso.getChild("Tail");
        this.LeftWing = Torso.getChild("LeftWing");
        this.RightWing = Torso.getChild("RightWing");

        var tailPrimary = Tail.getChild("TailPrimary");
        var tailSecondary = tailPrimary.getChild("TailSecondary");

        var leftLowerLeg = LeftLeg.getChild("LeftLowerLeg");
        var leftFoot = leftLowerLeg.getChild("LeftFoot");
        var rightLowerLeg = RightLeg.getChild("RightLowerLeg");
        var rightFoot = rightLowerLeg.getChild("RightFoot");

        animator = LatexAnimator.of(this).hipOffset(-1.5f)
                .addPreset(AnimatorPresets.beeLike(
                        Head, Head.getChild("LeftEar"), Head.getChild("RightEar"),
                        Torso, LeftArm2, RightArm2, LeftArm, RightArm,
                        Tail, List.of(tailPrimary, tailSecondary), LeftWing, RightWing,
                        LeftLeg, leftLowerLeg, leftFoot, leftFoot.getChild("LeftPad"), RightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad")));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));

        PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(48, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(68, 44).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(10, 70).addBox(-2.0F, -4.95F, -0.725F, 4.0F, 2.0F, 3.0F, new CubeDeformation(-0.3F))
                .texOffs(37, 0).addBox(-2.0F, -3.65F, -0.725F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.005F))
                .texOffs(29, 68).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(63, 37).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));

        PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(0, 47).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(56, 53).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(72, 15).addBox(-2.0F, -4.95F, -0.725F, 4.0F, 2.0F, 3.0F, new CubeDeformation(-0.3F))
                .texOffs(24, 54).addBox(-2.0F, -3.65F, -0.725F, 4.0F, 1.0F, 3.0F, new CubeDeformation(0.005F))
                .texOffs(68, 8).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offset(-3.0F, -7.5F, 0.0F));

        PartDefinition RightEarPivot = RightEar.addOrReplaceChild("RightEarPivot", CubeListBuilder.create().texOffs(32, 25).addBox(-4.6F, 0.8F, 1.0F, 4.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(48, 11).addBox(-5.6F, 0.8F, -1.0F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(55, 72).addBox(-4.6F, 0.8F, -1.0F, 4.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.2F, -0.9F, -0.6F, -0.2182F, 0.1745F, -0.4363F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offset(3.0F, -7.5F, 0.0F));

        PartDefinition LeftEarPivot = LeftEar.addOrReplaceChild("LeftEarPivot", CubeListBuilder.create().texOffs(43, 72).addBox(0.6F, 0.8F, -1.0F, 4.0F, 3.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(42, 25).addBox(0.6F, 0.8F, 1.0F, 4.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(18, 32).addBox(4.6F, 0.8F, -1.0F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.2F, -0.9F, -0.6F, -0.2182F, -0.1745F, 0.4363F));

        PartDefinition RightAntenna = Head.addOrReplaceChild("RightAntenna", CubeListBuilder.create(), PartPose.offset(-2.5F, -8.0F, -1.0F));

        PartDefinition RightAntennaPivot = RightAntenna.addOrReplaceChild("RightAntennaPivot", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.75F, 1.0F, -0.1745F, 0.0873F, -0.1309F));

        PartDefinition AntennaPart_r1 = RightAntennaPivot.addOrReplaceChild("AntennaPart_r1", CubeListBuilder.create().texOffs(0, 19).addBox(-2.75F, -12.25F, -1.075F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(2.5F, 8.25F, 0.0F, 0.4363F, 0.2182F, 0.0F));

        PartDefinition AntennaPart_r2 = RightAntennaPivot.addOrReplaceChild("AntennaPart_r2", CubeListBuilder.create().texOffs(0, 32).addBox(-2.75F, -12.0F, 2.0F, 1.0F, 5.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.5F, 8.25F, 0.0F, 0.5236F, 0.2182F, 0.0F));

        PartDefinition LeftAntenna = Head.addOrReplaceChild("LeftAntenna", CubeListBuilder.create(), PartPose.offset(2.5F, -8.0F, -1.0F));

        PartDefinition LeftAntennaPivot = LeftAntenna.addOrReplaceChild("LeftAntennaPivot", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -0.75F, 1.0F, -0.1745F, -0.0873F, 0.1309F));

        PartDefinition AntennaPart_r3 = LeftAntennaPivot.addOrReplaceChild("AntennaPart_r3", CubeListBuilder.create().texOffs(0, 16).addBox(1.75F, -12.25F, -1.075F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-2.5F, 8.25F, 0.0F, 0.4363F, -0.2182F, 0.0F));

        PartDefinition AntennaPart_r4 = LeftAntennaPivot.addOrReplaceChild("AntennaPart_r4", CubeListBuilder.create().texOffs(6, 16).addBox(1.75F, -12.0F, 2.0F, 1.0F, 5.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.5F, 8.25F, 0.0F, 0.5236F, -0.2182F, 0.0F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.2F))
                .texOffs(24, 8).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(28, 28).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(20, 44).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.2F))
                .texOffs(44, 44).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 5.0F, 4.0F, new CubeDeformation(0.35F)), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition LeftWing = Torso.addOrReplaceChild("LeftWing", CubeListBuilder.create(), PartPose.offset(0.5F, 2.5F, 1.0F));

        PartDefinition LeftWingPivot = LeftWing.addOrReplaceChild("LeftWingPivot", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition WingBase_r1 = LeftWingPivot.addOrReplaceChild("WingBase_r1", CubeListBuilder.create().texOffs(0, 0).addBox(5.75F, -21.75F, 8.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(76, 20).addBox(5.75F, -20.75F, 9.0F, 5.0F, 9.0F, 0.0F, CubeDeformation.NONE)
                .texOffs(0, 4).addBox(5.75F, -11.75F, 9.0F, 4.0F, 1.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.5F, 23.5F, -1.0F, 0.2182F, 0.2182F, -0.2618F));

        PartDefinition RightWing = Torso.addOrReplaceChild("RightWing", CubeListBuilder.create(), PartPose.offset(-0.5F, 2.5F, 1.0F));

        PartDefinition RightWingPivot = RightWing.addOrReplaceChild("RightWingPivot", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition WingBase_r2 = RightWingPivot.addOrReplaceChild("WingBase_r2", CubeListBuilder.create().texOffs(0, 69).addBox(-10.75F, -20.75F, 9.0F, 5.0F, 9.0F, 0.0F, CubeDeformation.NONE)
                .texOffs(0, 5).addBox(-9.75F, -11.75F, 9.0F, 4.0F, 1.0F, 0.0F, CubeDeformation.NONE)
                .texOffs(0, 2).addBox(-8.75F, -21.75F, 8.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.5F, 23.5F, -1.0F, 0.2182F, -0.2182F, 0.2618F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 10.5F, 0.0F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition Base_r1 = TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(64, 0).addBox(-2.0F, 0.75F, -1.5F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.2654F, 0.0F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 3.5F));

        PartDefinition Base_r2 = TailSecondary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(24, 0).addBox(-0.5F, 7.75F, -0.6F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.75F, 1.0F, 1.7017F, 0.0F, 0.0F));

        PartDefinition Base_r3 = TailSecondary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(0, 32).addBox(-3.0F, -1.15F, -2.1F, 6.0F, 9.0F, 6.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.75F, 1.0F, 1.4399F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(60, 63).addBox(-3.0F, 5.0F, -2.0F, 4.0F, 5.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(64, 27).addBox(-3.0F, 3.5F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(-0.3F))
                .texOffs(12, 54).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 5.5F, 0.0F));

        PartDefinition RightArm2 = partdefinition.addOrReplaceChild("RightArm2", CubeListBuilder.create().texOffs(0, 60).addBox(-3.0F, 5.0F, -2.0F, 4.0F, 5.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(16, 64).addBox(-3.0F, 3.5F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(-0.3F))
                .texOffs(40, 53).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 1.5F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(56, 11).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(44, 63).addBox(-1.0F, 5.0F, -2.0F, 4.0F, 5.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(60, 21).addBox(-1.0F, 3.5F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offset(5.0F, 5.5F, 0.0F));

        PartDefinition LeftArm2 = partdefinition.addOrReplaceChild("LeftArm2", CubeListBuilder.create().texOffs(52, 32).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(28, 59).addBox(-1.0F, 5.0F, -2.0F, 4.0F, 5.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(48, 26).addBox(-1.0F, 3.5F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offset(5.0F, 1.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 96, 96);
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

    public ModelPart getLeg(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.LeftLeg : this.RightLeg;
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

    public ModelPart getHead() {
        return this.Head;
    }

    public ModelPart getTorso() {
        return Torso;
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