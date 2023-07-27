package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.beast.LatexSnowLeopardFemale;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LatexSnowLeopardFemaleModel extends LatexHumanoidModel<LatexSnowLeopardFemale> implements LatexHumanoidModelInterface<LatexSnowLeopardFemale, LatexSnowLeopardFemaleModel> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_snow_leopard_female"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final LatexAnimator<LatexSnowLeopardFemale, LatexSnowLeopardFemaleModel> animator;

    public LatexSnowLeopardFemaleModel(ModelPart root) {
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

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(55, 59).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-2.5F, 10.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(56, 0).addBox(-2.0F, -0.1F, -1.925F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.16F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(4, 26).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(32, 28).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(40, 37).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(32, 30).addBox(-4.0F, -3.001F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 39).addBox(-2.5F, -3.001F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(25, 39).addBox(-1.0F, -3.001F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(29, 57).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(0, 55).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(43, 57).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(2.5F, 10.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(56, 18).addBox(-2.0F, -0.1F, -1.925F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.16F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(40, 39).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(48, 18).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(0, 41).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(48, 14).addBox(-4.0F, -3.001F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(48, 12).addBox(-2.5F, -3.001F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(43, 38).addBox(-1.0F, -3.001F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(56, 28).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(16, 49).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(24, 12).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(0, 24).addBox(-1.5F, 0.0F, -5.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(24, 0).addBox(-2.0F, -2.0F, -6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.001F)), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition leftear_r1 = Head.addOrReplaceChild("leftear_r1", CubeListBuilder.create().texOffs(60, 41).addBox(-7.75F, -34.75F, -1.0F, 2.0F, 4.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 26.0F, 0.0F, 0.0F, 0.0F, 0.3054F));

        PartDefinition rightear_r1 = Head.addOrReplaceChild("rightear_r1", CubeListBuilder.create().texOffs(40, 63).addBox(5.75F, -34.75F, -1.0F, 2.0F, 4.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 26.0F, 0.0F, 0.0F, 0.0F, -0.3054F));

        PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(0, 26).addBox(-1.0F, -29.625F, -0.95F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.5F, 27.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -33.0F, -4.0F, 8.0F, 12.0F, 8.0F, new CubeDeformation(0.2F))
                .texOffs(0, 20).addBox(-4.0F, -33.0F, -4.0F, 8.0F, 11.0F, 8.0F, new CubeDeformation(0.35F)), PartPose.offset(0.0F, 26.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition WaistLower_r1 = Torso.addOrReplaceChild("WaistLower_r1", CubeListBuilder.create().texOffs(48, 12).addBox(-4.0F, -2.15F, -1.925F, 8.0F, 2.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(40, 51).addBox(-4.0F, -3.0F, -2.0F, 8.0F, 2.0F, 4.0F, new CubeDeformation(0.1F))
                .texOffs(32, 0).addBox(-4.0F, -8.0F, -2.0F, 8.0F, 8.0F, 4.0F, new CubeDeformation(-0.3F))
                .texOffs(32, 28).addBox(-4.0F, -12.005F, -2.0F, 8.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 11.5F, 0.0F));

        PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(44, 37).addBox(-2.0F, -2.8086F, -0.9483F, 4.0F, 10.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 3.5F, 8.75F, 1.4835F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(16, 39).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition NeckFluff = Torso.addOrReplaceChild("NeckFluff", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Base_r3 = NeckFluff.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(50, 65).addBox(3.75F, 0.0F, -3.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(60, 65).addBox(-2.75F, 0.0F, -3.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(60, 48).addBox(-2.0F, 0.0F, -3.25F, 6.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(56, 37).addBox(-2.0F, 0.0F, -3.0F, 6.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Plantoids = Torso.addOrReplaceChild("Plantoids", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Center_r1 = Plantoids.addOrReplaceChild("Center_r1", CubeListBuilder.create().texOffs(0, 20).addBox(-1.0F, -11.5F, -2.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftPlantoid_r1 = Plantoids.addOrReplaceChild("LeftPlantoid_r1", CubeListBuilder.create().texOffs(0, 0).addBox(0.75F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.4F))
                .texOffs(0, 4).addBox(-3.75F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.4F)), PartPose.offsetAndRotation(0.0F, 3.0F, -2.0F, -0.1047F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(28, 37).addBox(-3.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(52, 2).addBox(0.0F, 10.75F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(52, 0).addBox(-3.0F, 10.75F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 51).addBox(-3.0F, 10.75F, -0.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 49).addBox(-3.0F, 10.75F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 0.0F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 39).addBox(-1.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(32, 53).addBox(-1.0F, 10.75F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(52, 30).addBox(2.0F, 10.75F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(52, 28).addBox(2.0F, 10.75F, -0.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(52, 18).addBox(2.0F, 10.75F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 72, 72);
    }

    @Override
    public void prepareMobModel(LatexSnowLeopardFemale p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LatexSnowLeopardFemale entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
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
    public LatexAnimator<LatexSnowLeopardFemale, LatexSnowLeopardFemaleModel> getAnimator() {
        return animator;
    }

    public static class Remodel extends LatexHumanoidModel.LatexRemodel<LatexSnowLeopardFemale, Remodel> {
        private final ModelPart RightLeg;
        private final ModelPart LeftLeg;
        private final ModelPart RightArm;
        private final ModelPart LeftArm;
        private final ModelPart Head;
        private final ModelPart Torso;
        private final ModelPart Tail;
        private final LatexAnimator<LatexSnowLeopardFemale, Remodel> animator;

        public Remodel(ModelPart root) {
            super(root);
            this.RightLeg = root.getChild("RightLeg");
            this.LeftLeg = root.getChild("LeftLeg");
            this.Head = root.getChild("Head");
            this.Torso = root.getChild("Torso");
            this.Tail = Torso.getChild("Tail");
            this.RightArm = root.getChild("RightArm");
            this.LeftArm = root.getChild("LeftArm");
            animator = LatexAnimator.of(this).addPreset(AnimatorPresets.wolfLike(Head, Torso, LeftArm, RightArm, Tail, List.of(), LeftLeg, RightLeg)).hipOffset(0.0f);
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();

            PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                    .texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.2F))
                    .texOffs(0, 6).addBox(-1.5F, -1.0F, -5.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                    .texOffs(0, 16).addBox(-0.5F, -3.1F, -6.05F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                    .texOffs(24, 0).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offset(-6.0F, 24.0F, 0.0F));

            PartDefinition Base_r1 = RightEar.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(13, 75).addBox(0.0F, -2.0F, -0.5F, 2.0F, 3.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, -32.0F, -0.5F, 0.0F, 0.0F, -0.2182F));

            PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

            PartDefinition Base_r2 = LeftEar.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(0, 76).addBox(-2.0F, -2.0F, -0.5F, 2.0F, 3.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, -32.0F, -0.5F, 0.0F, 0.0F, 0.2182F));

            PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                    .texOffs(28, 28).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition Breasts = Torso.addOrReplaceChild("Breasts", CubeListBuilder.create().texOffs(30, 74).addBox(-4.25F, -2.25F, -1.0F, 4.0F, 4.0F, 2.0F, CubeDeformation.NONE)
                    .texOffs(68, 72).addBox(0.25F, -2.25F, -1.0F, 4.0F, 4.0F, 2.0F, CubeDeformation.NONE)
                    .texOffs(56, 72).addBox(0.25F, -2.25F, -1.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.2F))
                    .texOffs(72, 5).addBox(-4.25F, -2.25F, -1.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 2.5F, -2.0F, -0.4363F, 0.0F, 0.0F));

            PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 11.0F, 0.0F));

            PartDefinition Base_r3 = Tail.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, -2.8086F, -0.9483F, 4.0F, 10.0F, 4.0F, new CubeDeformation(0.2F))
                    .texOffs(52, 28).addBox(-2.0F, -2.8086F, -0.9483F, 4.0F, 10.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 3.5F, 8.75F, 1.4835F, 0.0F, 0.0F));

            PartDefinition Base_r4 = Tail.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(32, 16).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 7.0F, 3.0F, new CubeDeformation(0.2F))
                    .texOffs(68, 50).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

            PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(20, 44).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                    .texOffs(32, 0).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

            PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(36, 44).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                    .texOffs(44, 12).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offset(5.0F, 2.0F, 0.0F));

            PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.25F, 12.0F, 0.0F));

            PartDefinition leg_fur_r1 = RightLeg.addOrReplaceChild("leg_fur_r1", CubeListBuilder.create().texOffs(64, 20).addBox(-2.0F, -6.7987F, -2.9677F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.2F))
                    .texOffs(64, 62).addBox(-2.0F, -6.7987F, -2.9677F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 5.275F, 4.9F, 1.2217F, 0.0F, 0.0F));

            PartDefinition thigh_fur_r1 = RightLeg.addOrReplaceChild("thigh_fur_r1", CubeListBuilder.create().texOffs(68, 30).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.25F))
                    .texOffs(68, 40).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, -0.1F, 0.0F, -0.0873F, 0.0F, 0.0F));

            PartDefinition RightLower = RightLeg.addOrReplaceChild("RightLower", CubeListBuilder.create().texOffs(16, 70).addBox(-2.0F, 6.975F, -4.675F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.025F)), PartPose.offset(0.0F, 4.0F, 4.5F));

            PartDefinition leg_fur_r2 = RightLower.addOrReplaceChild("leg_fur_r2", CubeListBuilder.create().texOffs(52, 54).addBox(-2.0F, -0.225F, -3.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.225F))
                    .texOffs(60, 8).addBox(-2.0F, -0.225F, -3.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 0.15F, -1.0F, -0.0873F, 0.0F, 0.0F));

            PartDefinition RightLowerBeans = RightLower.addOrReplaceChild("RightLowerBeans", CubeListBuilder.create().texOffs(0, 3).addBox(1.0F, -13.025F, 0.325F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.075F))
                    .texOffs(0, 20).addBox(1.5F, -13.025F, -1.175F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F))
                    .texOffs(4, 18).addBox(0.225F, -13.025F, -0.925F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F))
                    .texOffs(4, 20).addBox(2.8F, -13.025F, -0.925F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)), PartPose.offset(-2.0F, 20.0F, -3.2F));

            PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.25F, 12.0F, 0.0F));

            PartDefinition leg_fur_r3 = LeftLeg.addOrReplaceChild("leg_fur_r3", CubeListBuilder.create().texOffs(12, 60).addBox(-2.0F, -6.7987F, -2.9677F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.2F))
                    .texOffs(28, 60).addBox(-2.0F, -6.7987F, -2.9677F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 5.275F, 4.9F, 1.2217F, 0.0F, 0.0F));

            PartDefinition thigh_fur_r2 = LeftLeg.addOrReplaceChild("thigh_fur_r2", CubeListBuilder.create().texOffs(0, 66).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.25F))
                    .texOffs(40, 66).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, -0.1F, 0.0F, -0.0873F, 0.0F, 0.0F));

            PartDefinition LeftLower = LeftLeg.addOrReplaceChild("LeftLower", CubeListBuilder.create().texOffs(64, 0).addBox(-2.0F, 6.975F, -4.675F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.025F)), PartPose.offset(0.0F, 4.0F, 4.5F));

            PartDefinition leg_fur_r4 = LeftLower.addOrReplaceChild("leg_fur_r4", CubeListBuilder.create().texOffs(48, 0).addBox(-2.0F, -0.225F, -3.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.225F))
                    .texOffs(52, 42).addBox(-2.0F, -0.225F, -3.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 0.15F, -1.0F, -0.0873F, 0.0F, 0.0F));

            PartDefinition LeftLowerBeans2 = LeftLower.addOrReplaceChild("LeftLowerBeans2", CubeListBuilder.create().texOffs(0, 0).addBox(1.0F, -13.025F, 0.325F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.075F))
                    .texOffs(0, 18).addBox(1.5F, -13.025F, -1.175F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F))
                    .texOffs(4, 16).addBox(0.225F, -13.025F, -0.925F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F))
                    .texOffs(0, 22).addBox(2.8F, -13.025F, -0.925F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)), PartPose.offset(-2.0F, 20.0F, -3.2F));

            return LayerDefinition.create(meshdefinition, 128, 128);
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
        public LatexAnimator<LatexSnowLeopardFemale, Remodel> getAnimator() {
            return animator;
        }
    }
}
