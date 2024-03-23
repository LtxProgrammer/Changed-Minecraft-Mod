package net.ltxprogrammer.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.beast.LatexSiren;
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
public class LatexSirenModel extends AdvancedHumanoidModel<LatexSiren> implements AdvancedHumanoidModelInterface<LatexSiren, LatexSirenModel>, LeglessModel {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_siren"), "main");
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Abdomen;
    private final ModelPart LowerAbdomen;
    private final ModelPart Tail;
    private final HumanoidAnimator<LatexSiren, LatexSirenModel> animator;

    public LatexSirenModel(ModelPart root) {
        super(root);
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Abdomen = root.getChild("Abdomen");
        this.LowerAbdomen = Abdomen.getChild("LowerAbdomen");
        this.Tail = LowerAbdomen.getChild("Tail");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");

        animator = HumanoidAnimator.of(this).hipOffset(-1.5f).torsoLength(9.0f).legLength(9.5f)
                .addPreset(AnimatorPresets.leglessShark(Head, Torso, LeftArm, RightArm, Abdomen, LowerAbdomen, Tail, List.of(
                        Tail.getChild("TailPrimary"),
                        Tail.getChild("TailPrimary").getChild("TailSecondary"),
                        Tail.getChild("TailPrimary").getChild("TailSecondary").getChild("TailTertiary"),
                        Tail.getChild("TailPrimary").getChild("TailSecondary").getChild("TailTertiary").getChild("TailQuaternary"),
                        Tail.getChild("TailPrimary").getChild("TailSecondary").getChild("TailTertiary").getChild("TailQuaternary").getChild("TailQuintary")
                )));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(24, 11).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(44, 60).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.5F, -3.01F, -7.0F, 0.0F, -0.2182F, 0.0F));

        PartDefinition Snout_r2 = Head.addOrReplaceChild("Snout_r2", CubeListBuilder.create().texOffs(60, 48).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.5F, -3.01F, -7.0F, 0.0F, 0.2182F, 0.0F));

        PartDefinition Snout_r3 = Head.addOrReplaceChild("Snout_r3", CubeListBuilder.create().texOffs(24, 0).addBox(-1.5F, -27.0F, -6.0F, 3.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(32, 27).addBox(-1.5F, -29.0F, -7.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0101F)), PartPose.offsetAndRotation(0.0F, 26.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Fin_r1 = Head.addOrReplaceChild("Fin_r1", CubeListBuilder.create().texOffs(0, 62).addBox(-1.175F, -0.4F, -1.3F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(4.0F, -3.0F, -2.0F, 0.5672F, 0.3927F, 0.6545F));

        PartDefinition Fin_r2 = Head.addOrReplaceChild("Fin_r2", CubeListBuilder.create().texOffs(10, 62).addBox(-0.825F, -0.4F, -1.3F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(-4.0F, -3.0F, -2.0F, 0.5672F, -0.3927F, -0.6545F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -33.0F, -4.0F, 8.0F, 11.0F, 8.0F, new CubeDeformation(0.2F))
                .texOffs(0, 19).addBox(-4.0F, -33.0F, -4.0F, 8.0F, 10.0F, 8.0F, new CubeDeformation(0.35F)), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition BackFin_r1 = Torso.addOrReplaceChild("BackFin_r1", CubeListBuilder.create().texOffs(24, 3).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.0F, -3.0F, 1.0F, 2.0F, 6.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 8.25F, 2.0F, 0.7854F, 0.0F, 0.0F));

        PartDefinition TorsoLower_r1 = Torso.addOrReplaceChild("TorsoLower_r1", CubeListBuilder.create().texOffs(0, 37).addBox(-4.0F, -8.5F, -2.0F, 8.0F, 5.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 12.5F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Torso_r1 = Torso.addOrReplaceChild("Torso_r1", CubeListBuilder.create().texOffs(20, 44).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Plantoids = Torso.addOrReplaceChild("Plantoids", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -2.0F));

        PartDefinition RightPlantoid_r1 = Plantoids.addOrReplaceChild("RightPlantoid_r1", CubeListBuilder.create().texOffs(56, 18).addBox(-4.25F, -1.7F, -0.8F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.03F))
                .texOffs(56, 42).addBox(0.25F, -1.7F, -0.8F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.2793F, 0.0F, 0.0F));

        PartDefinition Center_r1 = Plantoids.addOrReplaceChild("Center_r1", CubeListBuilder.create().texOffs(20, 37).addBox(-0.5F, -1.3F, -0.1F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.192F, 0.0F, 0.0F));

        PartDefinition Abdomen = partdefinition.addOrReplaceChild("Abdomen", CubeListBuilder.create().texOffs(48, 10).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition LowerAbdomen = Abdomen.addOrReplaceChild("LowerAbdomen", CubeListBuilder.create().texOffs(27, 32).addBox(-4.5F, -1.25F, -2.5F, 9.0F, 7.0F, 5.0F, new CubeDeformation(0.06F)), PartPose.offset(0.0F, 4.25F, 0.0F));

        PartDefinition Tail = LowerAbdomen.addOrReplaceChild("Tail", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -0.75F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 5.5F, 0.0F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create().texOffs(50, 27).addBox(-3.5F, -0.25F, -2.0F, 7.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.5F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create().texOffs(16, 53).addBox(-3.0F, -0.25F, -2.0F, 6.0F, 5.0F, 4.0F, new CubeDeformation(-0.25F)), PartPose.offset(0.0F, 4.25F, 0.0F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create().texOffs(55, 36).addBox(-2.0F, -0.25F, -1.5F, 4.0F, 3.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.5F, 0.0F));

        PartDefinition TailQuaternary = TailTertiary.addOrReplaceChild("TailQuaternary", CubeListBuilder.create().texOffs(56, 0).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 4.0F, 3.0F, new CubeDeformation(-0.05F)), PartPose.offset(0.0F, 2.55F, 0.0F));

        PartDefinition TailQuintary = TailQuaternary.addOrReplaceChild("TailQuintary", CubeListBuilder.create(), PartPose.offset(0.0F, 2.5F, -0.5F));

        PartDefinition Base_r1 = TailQuintary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(32, 0).addBox(-0.5F, 9.4311F, 0.1673F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F))
                .texOffs(57, 57).addBox(-0.5F, 1.5311F, 0.1673F, 1.0F, 8.0F, 3.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 0.5834F, -0.2961F, -0.3491F, 0.0F, 0.0F));

        PartDefinition Base_r2 = TailQuintary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(16, 46).addBox(-0.5F, -10.3457F, 2.9744F, 1.0F, 7.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(36, 53).addBox(-0.5F, -12.3457F, -0.0256F, 1.0F, 10.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.5834F, -0.2961F, -2.3562F, 0.0F, 0.0F));

        PartDefinition Base_r3 = TailQuintary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(0, 19).addBox(-1.0F, 0.1701F, -0.3405F, 2.0F, 5.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.5834F, -0.2961F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 46).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 1.5F, 0.0F));

        PartDefinition ArmFin_r1 = RightArm.addOrReplaceChild("ArmFin_r1", CubeListBuilder.create().texOffs(44, 27).addBox(-0.5F, -1.6F, -1.1F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(24, 62).addBox(-0.5F, -2.6F, -2.1F, 1.0F, 5.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.0F, 2.9F, 1.2F, -2.618F, 0.829F, 3.098F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(44, 44).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 1.5F, 0.0F));

        PartDefinition ArmFin_r2 = LeftArm.addOrReplaceChild("ArmFin_r2", CubeListBuilder.create().texOffs(0, 37).addBox(-0.5F, -1.6F, -1.1F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(20, 62).addBox(-0.5F, -2.6F, -2.1F, 1.0F, 5.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 2.9F, 1.2F, -2.618F, -0.829F, -3.098F));

        return LayerDefinition.create(meshdefinition, 96, 96);
    }

    @Override
    public void prepareMobModel(LatexSiren p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LatexSiren entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public PoseStack getPlacementCorrectors(CorrectorType type) {
        PoseStack corrector = AdvancedHumanoidModelInterface.super.getPlacementCorrectors(type);
        if (type == CorrectorType.HAIR)
            corrector.translate(0.0f, 0.5f / 15.0f, 0.0f);
        else if (type == CorrectorType.LOWER_HAIR)
            corrector.translate(0.0f, -0.5f / 16.0f, -0.025f);
        return corrector;
    }

    public ModelPart getArm(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }

    @Override
    public ModelPart getLeg(HumanoidArm leg) {
        return null;
    }

    public ModelPart getHead() {
        return this.Head;
    }

    public ModelPart getTorso() {
        return Torso;
    }

    @Override
    public ModelPart getAbdomen() {
        return Abdomen;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        Abdomen.render(poseStack, buffer, packedLight, packedOverlay);
        Head.render(poseStack, buffer, packedLight, packedOverlay);
        Torso.render(poseStack, buffer, packedLight, packedOverlay);
        RightArm.render(poseStack, buffer, packedLight, packedOverlay);
        LeftArm.render(poseStack, buffer, packedLight, packedOverlay);
    }

    @Override
    public HumanoidAnimator<LatexSiren, LatexSirenModel> getAnimator() {
        return animator;
    }
}