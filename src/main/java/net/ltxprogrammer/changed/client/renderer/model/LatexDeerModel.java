package net.ltxprogrammer.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.beast.LatexDeer;
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
public class LatexDeerModel extends LatexHumanoidModel<LatexDeer> implements LatexHumanoidModelInterface<LatexDeer, LatexDeerModel> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_deer"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final LatexAnimator<LatexDeer, LatexDeerModel> animator;

    public LatexDeerModel(ModelPart root) {
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

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(40, 0).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(32, 6).addBox(-2.5F, -1.1F, -0.17F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(32, 8).addBox(-1.11F, -1.1F, -0.17F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(40, 0).addBox(-3.89F, -1.1F, -0.17F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.8727F, 0.0F, 0.0F));

        PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(20, 16).addBox(-1.11F, -3.1F, -1.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.099F))
                .texOffs(20, 18).addBox(-2.5F, -3.1F, -1.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.099F))
                .texOffs(24, 18).addBox(-3.89F, -3.1F, -1.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.099F)), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(46, 34).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(16, 32).addBox(-1.99F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(32, 6).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(40, 16).addBox(-2.5F, -1.1F, -0.17F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(40, 2).addBox(-1.11F, -1.1F, -0.17F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(40, 18).addBox(-3.89F, -1.1F, -0.17F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.8727F, 0.0F, 0.0F));

        PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(36, 16).addBox(-1.11F, -3.1F, -1.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.099F))
                .texOffs(44, 6).addBox(-2.5F, -3.1F, -1.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.099F))
                .texOffs(48, 6).addBox(-3.89F, -3.1F, -1.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.099F)), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(32, 34).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(0, 32).addBox(-2.01F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(48, 6).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.5F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(52, 16).addBox(-2.0F, -2.5F, -6.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(24, 6).addBox(-1.5F, -0.5F, -5.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(56, 4).addBox(-1.0F, -29.625F, -0.95F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.5F, 26.5F, 0.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(32, 43).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.2F))
                .texOffs(0, 43).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.5F, 0.0F));

        PartDefinition LeftAntler = Head.addOrReplaceChild("LeftAntler", CubeListBuilder.create(), PartPose.offset(0.0F, 25.5F, 0.0F));

        PartDefinition Antler_r1 = LeftAntler.addOrReplaceChild("Antler_r1", CubeListBuilder.create().texOffs(43, 32).addBox(-2.0F, -34.5F, 12.0F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.4363F, 0.0873F, 0.1309F));

        PartDefinition Antler_r2 = LeftAntler.addOrReplaceChild("Antler_r2", CubeListBuilder.create().texOffs(60, 33).addBox(-2.0F, -38.0F, -7.25F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1745F, 0.0873F, 0.1309F));

        PartDefinition Antler_r3 = LeftAntler.addOrReplaceChild("Antler_r3", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -37.25F, 7.75F, 1.0F, 6.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 32).addBox(-2.0F, -35.0F, 6.75F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2618F, 0.0873F, 0.1309F));

        PartDefinition RightAntler = Head.addOrReplaceChild("RightAntler", CubeListBuilder.create(), PartPose.offset(0.0F, 25.5F, 0.0F));

        PartDefinition Antler_r4 = RightAntler.addOrReplaceChild("Antler_r4", CubeListBuilder.create().texOffs(0, 16).addBox(1.0F, -35.0F, 6.75F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(4, 0).addBox(1.0F, -37.25F, 7.75F, 1.0F, 6.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2618F, -0.0873F, -0.1309F));

        PartDefinition Antler_r5 = RightAntler.addOrReplaceChild("Antler_r5", CubeListBuilder.create().texOffs(60, 38).addBox(1.0F, -34.5F, 12.0F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.4363F, -0.0873F, -0.1309F));

        PartDefinition Antler_r6 = RightAntler.addOrReplaceChild("Antler_r6", CubeListBuilder.create().texOffs(60, 5).addBox(1.0F, -38.0F, -7.25F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.1745F, -0.0873F, -0.1309F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offsetAndRotation(16.25F, 17.15F, -1.0F, 0.0F, 0.0F, -0.6109F));

        PartDefinition cube_r1 = RightEar.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(24, 43).addBox(8.8F, -28.5F, -0.75F, 4.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(56, 22).addBox(8.25F, -28.0F, 1.7F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(52, 0).addBox(7.5F, -28.0F, -0.75F, 2.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(56, 20).addBox(8.25F, -28.0F, -1.2F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.5236F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offsetAndRotation(-16.25F, 17.15F, -1.0F, 0.0F, 0.0F, 0.6109F));

        PartDefinition cube_r2 = LeftEar.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(24, 47).addBox(-12.8F, -28.5F, -0.75F, 4.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(56, 24).addBox(-12.25F, -28.0F, -1.2F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(53, 29).addBox(-9.5F, -28.0F, -0.75F, 2.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(56, 26).addBox(-12.25F, -28.0F, 1.7F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.5236F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.5F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 11.5F, 0.0F));

        PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(0, 57).addBox(-1.5F, -2.55F, -0.3F, 3.0F, 4.0F, 3.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, 2.0F, 6.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(12, 57).addBox(-1.5F, 1.0F, -0.6F, 3.0F, 3.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(24, 16).addBox(-3.0F, -1.5F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(24, 0).addBox(-0.11F, 10.25F, -1.89F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(44, 8).addBox(-2.89F, 10.25F, 0.89F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(36, 18).addBox(-2.89F, 10.25F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(24, 16).addBox(-2.89F, 10.25F, -1.89F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offset(-5.0F, 0.5F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(40, 16).addBox(-1.0F, -1.5F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(24, 2).addBox(-0.89F, 10.25F, -1.89F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(36, 2).addBox(1.89F, 10.25F, 0.89F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(36, 0).addBox(1.89F, 10.25F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(48, 8).addBox(1.89F, 10.25F, -1.89F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offset(5.0F, 0.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 72, 72);
    }

    @Override
    public void prepareMobModel(LatexDeer p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LatexDeer entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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
        RightLeg.render(poseStack, buffer, packedLight, packedOverlay);
        LeftLeg.render(poseStack, buffer, packedLight, packedOverlay);
        Head.render(poseStack, buffer, packedLight, packedOverlay);
        Torso.render(poseStack, buffer, packedLight, packedOverlay);
        RightArm.render(poseStack, buffer, packedLight, packedOverlay);
        LeftArm.render(poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public LatexAnimator<LatexDeer, LatexDeerModel> getAnimator() {
        return animator;
    }
}