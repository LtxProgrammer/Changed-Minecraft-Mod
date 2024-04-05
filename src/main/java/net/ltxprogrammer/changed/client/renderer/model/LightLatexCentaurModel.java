package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.tfanimations.HelperModel;
import net.ltxprogrammer.changed.client.tfanimations.Limb;
import net.ltxprogrammer.changed.client.tfanimations.TransfurHelper;
import net.ltxprogrammer.changed.entity.beast.LightLatexCentaur;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LightLatexCentaurModel extends AdvancedHumanoidModel<LightLatexCentaur> implements AdvancedHumanoidModelInterface<LightLatexCentaur, LightLatexCentaurModel>, LowerTorsoedModel {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("light_latex_centaur"), "main");
    private final ModelPart FrontRightLeg;
    private final ModelPart FrontLeftLeg;
    private final ModelPart BackRightLeg;
    private final ModelPart BackLeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart LowerTorso;
    private final ModelPart Saddle;
    private final ModelPart Tail;
    private final HumanoidAnimator<LightLatexCentaur, LightLatexCentaurModel> animator;

    public LightLatexCentaurModel(ModelPart root) {
        super(root);
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.LowerTorso = root.getChild("LowerTorso");
        this.Saddle = LowerTorso.getChild("Saddle");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");

        this.FrontRightLeg = LowerTorso.getChild("RightLeg");
        this.FrontLeftLeg = LowerTorso.getChild("LeftLeg");
        this.BackRightLeg = LowerTorso.getChild("RightLeg2");
        this.BackLeftLeg = LowerTorso.getChild("LeftLeg2");
        this.Tail = LowerTorso.getChild("Tail");

        var tailPrimary = Tail.getChild("TailPrimary");
        var tailSecondary = tailPrimary.getChild("TailSecondary");
        var tailTertiary = tailSecondary.getChild("TailTertiary");

        var leftLowerLeg = FrontLeftLeg.getChild("LeftLowerLeg");
        var rightLowerLeg = FrontRightLeg.getChild("RightLowerLeg");

        var leftLowerLeg2 = BackLeftLeg.getChild("LeftLowerLeg2");
        var leftFoot2 = leftLowerLeg2.getChild("LeftFoot2");
        var rightLowerLeg2 = BackRightLeg.getChild("RightLowerLeg2");
        var rightFoot2 = rightLowerLeg2.getChild("RightFoot2");

        animator = HumanoidAnimator.of(this).addPreset(AnimatorPresets.taurLike(
                Head, Head.getChild("LeftEar"), Head.getChild("RightEar"),
                Torso, LeftArm, RightArm,
                Tail, List.of(tailPrimary, tailSecondary, tailTertiary),
                LowerTorso, FrontLeftLeg, leftLowerLeg, leftLowerLeg.getChild("LeftFoot"), FrontRightLeg, rightLowerLeg, rightLowerLeg.getChild("RightFoot"),
                BackLeftLeg, leftLowerLeg2, leftFoot2, leftFoot2.getChild("LeftPad2"), BackRightLeg, rightLowerLeg2, rightFoot2, rightFoot2.getChild("RightPad2")))
                .forwardOffset(-7.0f).hipOffset(-1.5f).legLength(13.5f).torsoLength(11.05f);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(36, 25).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(20, 45).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(10, 16).addBox(-1.5F, -1.0F, -5.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -1.5F, -7.0F));

        PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -29.625F, -0.95F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.5F, 26.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offset(-3.0F, -7.5F, 0.0F));

        PartDefinition RightEarPivot = RightEar.addOrReplaceChild("RightEarPivot", CubeListBuilder.create().texOffs(0, 16).addBox(-4.6F, 0.8F, 1.0F, 4.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(12, 0).addBox(-5.6F, 0.8F, -1.0F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(72, 37).addBox(-4.6F, 0.8F, -1.0F, 4.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.2F, -0.9F, -0.6F, -0.2182F, 0.1745F, -0.4363F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offset(3.0F, -7.5F, 0.0F));

        PartDefinition LeftEarPivot = LeftEar.addOrReplaceChild("LeftEarPivot", CubeListBuilder.create().texOffs(76, 19).addBox(0.6F, 0.8F, -1.0F, 4.0F, 3.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 25).addBox(0.6F, 0.8F, 1.0F, 4.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 28).addBox(4.6F, 0.8F, -1.0F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.2F, -0.9F, -0.6F, -0.2182F, -0.1745F, 0.4363F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(35, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.2F))
                .texOffs(28, 41).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 45).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 10.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -1.5F, -7.0F));

        PartDefinition LowerTorso = partdefinition.addOrReplaceChild("LowerTorso", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -2.0F, -2.0F, 8.0F, 6.0F, 19.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 10.5F, -7.0F));

        PartDefinition Saddle = LowerTorso.addOrReplaceChild("Saddle", CubeListBuilder.create().texOffs(0, 25).addBox(-4.0F, -5.0F, 3.0F, 8.0F, 9.0F, 10.0F, new CubeDeformation(0.15F)), PartPose.offset(0.0F, 3.0F, 0.0F));

        PartDefinition LeftLeg2 = LowerTorso.addOrReplaceChild("LeftLeg2", CubeListBuilder.create(), PartPose.offset(3.5F, 0.0F, 15.875F));

        PartDefinition LeftThigh_r1 = LeftLeg2.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(60, 41).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg2 = LeftLeg2.addOrReplaceChild("LeftLowerLeg2", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg2.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(68, 27).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot2 = LeftLowerLeg2.addOrReplaceChild("LeftFoot2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition LeftArch_r1 = LeftFoot2.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(43, 75).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad2 = LeftFoot2.addOrReplaceChild("LeftPad2", CubeListBuilder.create().texOffs(67, 52).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition RightLeg2 = LowerTorso.addOrReplaceChild("RightLeg2", CubeListBuilder.create(), PartPose.offset(-3.5F, 0.0F, 16.375F));

        PartDefinition RightThigh_r1 = RightLeg2.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(60, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg2 = RightLeg2.addOrReplaceChild("RightLowerLeg2", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition RightCalf_r1 = RightLowerLeg2.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(12, 67).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot2 = RightLowerLeg2.addOrReplaceChild("RightFoot2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition RightArch_r1 = RightFoot2.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(0, 74).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad2 = RightFoot2.addOrReplaceChild("RightPad2", CubeListBuilder.create().texOffs(26, 25).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftLeg = LowerTorso.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(3.5F, 0.0F, -1.7F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(55, 55).addBox(-2.0F, -6.89F, -4.2461F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.25F, 5.5348F, 3.9528F, 0.0873F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.25F, 5.7848F, 3.7028F));

        PartDefinition LeftLowerLeg_r1 = LeftLowerLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(55, 66).addBox(-2.0F, 3.8638F, 2.7342F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -5.275F, -5.575F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create().texOffs(67, 61).addBox(-1.95F, 0.0F, -2.0F, 4.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 5.7152F, -4.3278F));

        PartDefinition RightLeg = LowerTorso.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-4.0F, 0.0F, -1.7F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(0, 59).addBox(-9.5F, -6.89F, -4.2461F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(7.75F, 5.5348F, 3.9528F, 0.0873F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 5.7848F, 3.7028F));

        PartDefinition RightLowerLeg_r1 = RightLowerLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(67, 0).addBox(-2.0F, 3.8638F, 2.7342F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.25F, -5.275F, -5.575F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create().texOffs(28, 71).addBox(-2.025F, 0.0F, -2.0F, 4.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.25F, 5.7152F, -4.3278F));

        PartDefinition Tail = LowerTorso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, -0.25F, 14.5F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.25F, -0.1745F, 0.0F, 0.0F));

        PartDefinition Base_r1 = TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(71, 68).addBox(-2.0F, 0.75F, -1.5F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.25F, 4.5F));

        PartDefinition Base_r2 = TailSecondary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(19, 55).addBox(-2.5F, -0.45F, -2.1F, 5.0F, 7.0F, 5.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.75F, 2.5F));

        PartDefinition Base_r3 = TailTertiary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(72, 11).addBox(-2.0F, -1.2F, -1.95F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.15F)), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 1.8326F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 0.5F, -7.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(39, 55).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 0.5F, -7.0F));

        return LayerDefinition.create(meshdefinition, 96, 96);
    }

    @Override
    public void prepareMobModel(LightLatexCentaur p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LightLatexCentaur entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public PoseStack getPlacementCorrectors(CorrectorType type) {
        PoseStack corrector = AdvancedHumanoidModelInterface.super.getPlacementCorrectors(type);
        if (type == CorrectorType.HAIR)
            corrector.translate(0.0f, -1.5f / 15.0f, 0.0f);
        else if (type == CorrectorType.LOWER_HAIR)
            corrector.translate(0.0f, -2.0f / 16.0f, 0.0f);
        return corrector;
    }

    public ModelPart getArm(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }

    public ModelPart getLeg(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.FrontLeftLeg : this.FrontRightLeg;
    }

    public ModelPart getHead() {
        return this.Head;
    }

    public ModelPart getTorso() {
        return Torso;
    }

    @Override
    public HelperModel getTransfurHelperModel(Limb limb) {
        if (limb == Limb.LOWER_TORSO)
            return TransfurHelper.getTaurTorso();
        else if (limb == Limb.TORSO)
            return null;
        return super.getTransfurHelperModel(limb);
    }

    @Override
    public boolean shouldPartTransfur(ModelPart part) {
        return super.shouldPartTransfur(part) && part != this.Saddle;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        LowerTorso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        RightArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        LeftArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public HumanoidAnimator<LightLatexCentaur, LightLatexCentaurModel> getAnimator() {
        return animator;
    }

    @Override
    public ModelPart getLowerTorso() {
        return LowerTorso;
    }
}
