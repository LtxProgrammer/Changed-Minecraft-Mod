package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.beast.LightLatexCentaur;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LightLatexCentaurModel extends LatexHumanoidModel<LightLatexCentaur> implements LatexHumanoidModelInterface<LightLatexCentaur, LightLatexCentaurModel>, TorsoSupplier {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("light_latex_centaur"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightLeg2;
    private final ModelPart LeftLeg2;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart LowerTorso;
    private final ModelPart Tail;
    private final LatexAnimator<LightLatexCentaur, LightLatexCentaurModel> animator;

    public LightLatexCentaurModel(ModelPart root) {
        super(root);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.RightLeg2 = root.getChild("RightLeg2");
        this.LeftLeg2 = root.getChild("LeftLeg2");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.LowerTorso = Torso.getChild("LowerTorso");
        this.Tail = LowerTorso.getChild("Tail");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        animator = LatexAnimator.of(this).addPreset(AnimatorPresets.taurLike(Head, Torso, LeftArm, RightArm, Tail, List.of(), LeftLeg, RightLeg, LowerTorso, LeftLeg2, RightLeg2))
                .forwardOffset(-7.0f);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(43, 42).addBox(-1.75F, 12.0F, -1.8F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(46, 45).addBox(-1.75F, 12.0F, -2.8F, 4.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(-3.5F, 10.0F, -7.0F));

        PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(0, 56).addBox(1.0F, -0.8114F, -0.5982F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F))
                .texOffs(0, 56).addBox(-2.0F, -0.8114F, -0.5982F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F))
                .texOffs(0, 56).addBox(-0.5F, -0.8114F, -0.5982F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F)), PartPose.offsetAndRotation(0.25F, 13.1886F, -2.7018F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(0, 18).addBox(1.0F, -0.1886F, -0.5982F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(0, 18).addBox(-0.5F, -0.1886F, -0.5982F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(0, 18).addBox(-2.0F, -0.1886F, -0.5982F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(0.25F, 13.1886F, -2.7018F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(32, 6).addBox(-2.0F, 9.8638F, 2.7342F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.01F))
                .texOffs(32, 0).addBox(-2.0F, 2.8638F, 2.7342F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.25F, 1.0348F, -2.0472F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(32, 32).addBox(-2.0F, -0.3172F, -0.0274F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.015F)), PartPose.offsetAndRotation(0.25F, 1.0348F, -2.0472F, 0.3054F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(12, 44).addBox(-1.75F, 12.0F, -1.8F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(15, 47).addBox(-1.75F, 12.0F, -2.8F, 4.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(3.0F, 10.0F, -7.0F));

        PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(0, 58).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F))
                .texOffs(0, 58).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F))
                .texOffs(0, 58).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F)), PartPose.offsetAndRotation(2.25F, 13.0F, -3.3F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(24, 6).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(24, 6).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(24, 6).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(2.25F, 16.0F, -1.8F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(16, 38).addBox(-2.0F, 9.8638F, 2.7342F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.01F))
                .texOffs(16, 32).addBox(-2.0F, 2.8638F, 2.7342F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.25F, 1.0348F, -2.0472F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(40, 11).addBox(-2.0F, -0.3172F, -0.0274F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.015F)), PartPose.offsetAndRotation(0.25F, 1.0348F, -2.0472F, 0.3054F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -9.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(24, 0).addBox(-2.0F, -4.0F, -6.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(20, 16).addBox(-1.5F, -2.0F, -5.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -2.0F, -7.0F));

        PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-1.0F, -29.625F, -0.95F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.5F, 25.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create().texOffs(0, 4).addBox(-1.5088F, -0.3841F, -1.0F, 3.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(24, 4).addBox(-0.5088F, -1.3841F, -1.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(24, 4).addBox(-0.5088F, -0.3841F, 0.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-5.0F, -6.5F, 0.0F, -0.3927F, -0.3491F, -1.6581F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create().texOffs(0, 0).addBox(-0.9912F, -1.0341F, -1.0F, 3.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(20, 18).addBox(-0.9912F, -2.0341F, -1.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(20, 18).addBox(-0.9912F, -1.0341F, 0.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.5F, -7.0F, 0.0F, -0.3927F, 0.3491F, 1.6581F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -1.0F, -9.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition LowerTorso = Torso.addOrReplaceChild("LowerTorso", CubeListBuilder.create(), PartPose.offset(0.0F, 9.0F, -7.0F));

        PartDefinition LowerTorso_r1 = LowerTorso.addOrReplaceChild("LowerTorso_r1", CubeListBuilder.create().texOffs(59, 40).addBox(-4.5F, -16.75F, -2.0F, 9.0F, 4.0F, 6.0F, new CubeDeformation(0.3F))
                .texOffs(56, 5).addBox(-4.5F, -12.75F, -2.0F, 9.0F, 15.0F, 6.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -1.5708F, 0.0F, 0.0F));

        PartDefinition Tail = LowerTorso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 15.25F));

        PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(56, 30).addBox(-1.5F, 2.1914F, -2.1983F, 3.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 2.0F, 5.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(68, 30).addBox(-1.5F, 2.0F, -1.0F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(24, 16).addBox(-3.0F, -3.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(36, 16).addBox(0.0F, 8.75F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(36, 18).addBox(-3.0F, 8.75F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(36, 18).addBox(-3.0F, 8.75F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(36, 18).addBox(-3.0F, 8.75F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offset(-5.0F, 0.0F, -7.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, -3.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(32, 14).addBox(-1.0F, 8.75F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(28, 6).addBox(2.0F, 8.75F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(28, 6).addBox(2.0F, 8.75F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(28, 6).addBox(2.0F, 8.75F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offset(5.0F, 0.0F, -7.0F));

        PartDefinition LeftLeg2 = partdefinition.addOrReplaceChild("LeftLeg2", CubeListBuilder.create().texOffs(26, 57).addBox(-2.0F, 12.0F, 0.25F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(3.25F, 10.0F, 8.0F));

        PartDefinition Toe_r5 = LeftLeg2.addOrReplaceChild("Toe_r5", CubeListBuilder.create().texOffs(20, 58).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F))
                .texOffs(20, 58).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F))
                .texOffs(20, 58).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F)), PartPose.offsetAndRotation(2.0F, 13.0F, -0.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r6 = LeftLeg2.addOrReplaceChild("Toe_r6", CubeListBuilder.create().texOffs(24, 6).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(24, 6).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(24, 6).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(2.0F, 16.0F, 1.25F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg_r2 = LeftLeg2.addOrReplaceChild("LeftLowerLeg_r2", CubeListBuilder.create().texOffs(24, 63).addBox(-2.0F, 3.5131F, 2.8162F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 2.1238F, 0.5226F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg2.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(38, 61).addBox(-2.0F, -0.098F, -4.8445F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 2.1238F, 0.5226F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r2 = LeftLeg2.addOrReplaceChild("LeftUpperLeg_r2", CubeListBuilder.create().texOffs(38, 51).addBox(-2.0F, -2.0208F, -2.9291F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 2.1238F, 0.5226F, -0.0873F, 0.0F, 0.0F));

        PartDefinition RightLeg2 = partdefinition.addOrReplaceChild("RightLeg2", CubeListBuilder.create().texOffs(56, 57).addBox(-2.0F, 12.0F, 0.25F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-3.25F, 10.0F, 8.0F));

        PartDefinition Toe_r7 = RightLeg2.addOrReplaceChild("Toe_r7", CubeListBuilder.create().texOffs(20, 56).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F))
                .texOffs(20, 56).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F))
                .texOffs(20, 56).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F)), PartPose.offsetAndRotation(2.0F, 13.0F, -0.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r8 = RightLeg2.addOrReplaceChild("Toe_r8", CubeListBuilder.create().texOffs(0, 18).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(0, 18).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(0, 18).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(2.0F, 16.0F, 1.25F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg_r2 = RightLeg2.addOrReplaceChild("RightLowerLeg_r2", CubeListBuilder.create().texOffs(54, 63).addBox(-2.0F, 3.5131F, 2.8162F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 2.1238F, 0.5226F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg2.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(68, 61).addBox(-2.0F, -0.098F, -4.8445F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 2.1238F, 0.5226F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r2 = RightLeg2.addOrReplaceChild("RightUpperLeg_r2", CubeListBuilder.create().texOffs(68, 51).addBox(-2.0F, -2.0208F, -2.9291F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 2.1238F, 0.5226F, -0.0873F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 90, 72);
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
    }

    public PoseStack getPlacementCorrectors(CorrectorType type) {
        PoseStack corrector = LatexHumanoidModelInterface.super.getPlacementCorrectors(type);
        if (type.isArm())
            corrector.translate(0.0f, 4.0f / 18.0f, 0.1f);
        else if (type == CorrectorType.HAIR)
            corrector.translate(0.0f, -1.5f / 15.0f, 0.0f);
        else if (type == CorrectorType.LOWER_HAIR)
            corrector.translate(0.0f, -2.0f / 16.0f, 0.0f);
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
        RightLeg2.render(poseStack, buffer, packedLight, packedOverlay);
        LeftLeg2.render(poseStack, buffer, packedLight, packedOverlay);
        Head.render(poseStack, buffer, packedLight, packedOverlay);
        Torso.render(poseStack, buffer, packedLight, packedOverlay);
        RightArm.render(poseStack, buffer, packedLight, packedOverlay);
        LeftArm.render(poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public LatexAnimator<LightLatexCentaur, LightLatexCentaurModel> getAnimator() {
        return animator;
    }

    @Override
    public ModelPart getTorso() {
        return LowerTorso;
    }
}
