package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.beast.LightLatexWolfFemale;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LightLatexWolfFemaleModel extends LatexHumanoidModel<LightLatexWolfFemale> implements LatexHumanoidModelInterface<LightLatexWolfFemale, LightLatexWolfFemaleModel> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("light_latex_wolf_female"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final LatexAnimator<LightLatexWolfFemale, LightLatexWolfFemaleModel> animator;

    public LightLatexWolfFemaleModel(ModelPart root) {
        super(root);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Tail = Torso.getChild("Tail");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");

        var tailPrimary = Tail.getChild("TailPrimary");
        var tailSecondary = tailPrimary.getChild("TailSecondary");
        var tailTertiary = tailSecondary.getChild("TailTertiary");

        var leftLowerLeg = LeftLeg.getChild("LeftLowerLeg");
        var leftFoot = leftLowerLeg.getChild("LeftFoot");
        var rightLowerLeg = RightLeg.getChild("RightLowerLeg");
        var rightFoot = rightLowerLeg.getChild("RightFoot");

        animator = LatexAnimator.of(this).hipOffset(-1.5f)
                .addPreset(AnimatorPresets.wolfLike(
                        Head, Head.getChild("LeftEar"), Head.getChild("RightEar"),
                        Torso, LeftArm, RightArm,
                        Tail, List.of(tailPrimary, tailSecondary, tailTertiary),
                        LeftLeg, leftLowerLeg, leftFoot, leftFoot.getChild("LeftPad"), RightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad")));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.4F, 10.5F, 0.0F));

        PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(16, 48).addBox(-1.9F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-0.1F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(-0.1F, 6.375F, -3.45F));

        PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(56, 17).addBox(-1.9F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(59, 43).addBox(-1.9F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(54, 55).addBox(-1.9F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.4F, 10.5F, 0.0F));

        PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(52, 6).addBox(-2.1F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.1F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.1F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(0, 53).addBox(-2.1F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(42, 60).addBox(-2.1F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(27, 55).addBox(-2.1F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(24, 11).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(24, 0).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(24, 4).addBox(-1.5F, -1.0F, -5.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(18, 37).addBox(-1.0F, -29.625F, -0.95F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.5F, 26.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offset(-3.0F, -7.5F, 0.0F));

        PartDefinition RightEarPivot = RightEar.addOrReplaceChild("RightEarPivot", CubeListBuilder.create().texOffs(0, 4).addBox(-1.9F, -1.2F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(0, 19).addBox(-0.9F, -1.6F, -0.4F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(24, 6).addBox(-0.9F, -2.3F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(0, 37).addBox(0.1F, -3.1F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.5F, -1.25F, 0.0F, -0.1309F, 0.5236F, -0.3491F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offset(3.0F, -7.5F, 0.0F));

        PartDefinition LeftEarPivot = LeftEar.addOrReplaceChild("LeftEarPivot", CubeListBuilder.create().texOffs(0, 0).addBox(-1.1F, -1.2F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(0, 23).addBox(-1.1F, -1.6F, -0.4F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(12, 37).addBox(-1.1F, -2.3F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(22, 37).addBox(-1.1F, -3.1F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-0.5F, -1.25F, 0.0F, -0.1309F, -0.5236F, 0.3491F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -33.0F, -4.0F, 8.0F, 11.0F, 8.0F, new CubeDeformation(0.2F))
                .texOffs(0, 19).addBox(-4.0F, -33.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.35F)), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Waist_r1 = Torso.addOrReplaceChild("Waist_r1", CubeListBuilder.create().texOffs(47, 36).addBox(-4.0F, -3.4F, -2.0F, 8.0F, 3.0F, 4.0F, new CubeDeformation(0.1F))
                .texOffs(32, 0).addBox(-4.0F, -8.0F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(-0.3F))
                .texOffs(32, 27).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 10.0F, 0.0F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition Base_r1 = TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(56, 27).addBox(-2.0F, 0.75F, -1.5F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.25F, 4.5F));

        PartDefinition Base_r2 = TailSecondary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(27, 36).addBox(-2.5F, -0.45F, -2.1F, 5.0F, 7.0F, 5.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.75F, 2.5F));

        PartDefinition Base_r3 = TailTertiary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(12, 59).addBox(-2.0F, -1.2F, -1.95F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.15F)), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 1.8326F, 0.0F, 0.0F));

        PartDefinition Plantoids = Torso.addOrReplaceChild("Plantoids", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -2.0F));

        PartDefinition RightPlantoid_r1 = Plantoids.addOrReplaceChild("RightPlantoid_r1", CubeListBuilder.create().texOffs(56, 0).addBox(-4.25F, -1.7F, -0.8F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.03F))
                .texOffs(28, 62).addBox(0.25F, -1.7F, -0.8F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.2793F, 0.0F, 0.0F));

        PartDefinition Center_r1 = Plantoids.addOrReplaceChild("Center_r1", CubeListBuilder.create().texOffs(32, 27).addBox(-0.5F, -1.3F, -0.1F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.192F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 37).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 2.0F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(43, 44).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 2.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 96, 96);
    }

    @Override
    public void prepareMobModel(LightLatexWolfFemale p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LightLatexWolfFemale entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public ModelPart getArm(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }

    public ModelPart getHead() {
        return this.Head;
    }

    public ModelPart getTorso() {
        return Torso;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        RightLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        LeftLeg.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        Head.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        Torso.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        RightArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        LeftArm.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public LatexAnimator<LightLatexWolfFemale, LightLatexWolfFemaleModel> getAnimator() {
        return animator;
    }
}
