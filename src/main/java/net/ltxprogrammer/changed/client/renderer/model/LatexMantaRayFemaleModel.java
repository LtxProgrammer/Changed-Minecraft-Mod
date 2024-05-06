package net.ltxprogrammer.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.misc.MantaRayMembraneAnimator;
import net.ltxprogrammer.changed.client.tfanimations.HelperModel;
import net.ltxprogrammer.changed.client.tfanimations.Limb;
import net.ltxprogrammer.changed.client.tfanimations.TransfurHelper;
import net.ltxprogrammer.changed.entity.beast.LatexMantaRayFemale;
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
public class LatexMantaRayFemaleModel extends AdvancedHumanoidModel<LatexMantaRayFemale> implements AdvancedHumanoidModelInterface<LatexMantaRayFemale, LatexMantaRayFemaleModel>, LeglessModel {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_manta_ray_female"), "main");
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Abdomen;
    private final ModelPart LowerAbdomen;
    private final ModelPart Tail;
    private final HumanoidAnimator<LatexMantaRayFemale, LatexMantaRayFemaleModel> animator;

    public LatexMantaRayFemaleModel(ModelPart root) {
        super(root);
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Abdomen = root.getChild("Abdomen");
        this.LowerAbdomen = Abdomen.getChild("LowerAbdomen");
        this.Tail = LowerAbdomen.getChild("Tail");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");

        var rightMembrane = Torso.getChild("RightMembrane");
        var leftMembrane = Torso.getChild("LeftMembrane");
        var rightMembrane2 = rightMembrane.getChild("RightJoint");
        var leftMembrane2 = leftMembrane.getChild("LeftJoint");

        animator = HumanoidAnimator.of(this).hipOffset(-1.5f).torsoLength(9.0f).legLength(9.5f)
                .addPreset(AnimatorPresets.leglessMantaRay(Head, Torso, LeftArm, RightArm, Abdomen, LowerAbdomen, Tail, List.of(
                        Tail.getChild("TailPrimary"),
                        Tail.getChild("TailPrimary").getChild("TailSecondary"),
                        Tail.getChild("TailPrimary").getChild("TailSecondary").getChild("TailTertiary"),
                        Tail.getChild("TailPrimary").getChild("TailSecondary").getChild("TailTertiary").getChild("TailQuaternary"),
                        Tail.getChild("TailPrimary").getChild("TailSecondary").getChild("TailTertiary").getChild("TailQuaternary").getChild("TailQuintary")
                )))
                .addAnimator(new MantaRayMembraneAnimator<>(Torso, RightArm, LeftArm, rightMembrane, leftMembrane, rightMembrane2, leftMembrane2));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(10, 40).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.5F, -3.01F, -7.0F, 0.0F, -0.2182F, 0.0F));

        PartDefinition Snout_r2 = Head.addOrReplaceChild("Snout_r2", CubeListBuilder.create().texOffs(16, 60).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.5F, -3.01F, -7.0F, 0.0F, 0.2182F, 0.0F));

        PartDefinition Snout_r3 = Head.addOrReplaceChild("Snout_r3", CubeListBuilder.create().texOffs(28, 21).addBox(-1.5F, -27.0F, -6.0F, 3.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(57, 50).addBox(-1.5F, -29.0F, -7.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 26.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Fins = Head.addOrReplaceChild("Fins", CubeListBuilder.create(), PartPose.offset(0.0F, -8.5F, 0.0F));

        PartDefinition leftear_r1 = Fins.addOrReplaceChild("leftear_r1", CubeListBuilder.create().texOffs(52, 9).addBox(-6.15F, -28.9F, -23.8F, 2.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 33.5F, 0.0F, -0.7418F, 0.2182F, 0.2618F));

        PartDefinition leftear_r2 = Fins.addOrReplaceChild("leftear_r2", CubeListBuilder.create().texOffs(0, 55).addBox(-0.873F, -5.1926F, -1.0801F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(3.1494F, 4.5304F, 0.0273F, -1.1916F, 0.7221F, 0.3993F));

        PartDefinition rightear_r1 = Fins.addOrReplaceChild("rightear_r1", CubeListBuilder.create().texOffs(10, 55).addBox(-1.127F, -5.1926F, -1.0801F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(-3.1494F, 4.5304F, 0.0273F, -1.1916F, -0.7221F, -0.3993F));

        PartDefinition rightear_r2 = Fins.addOrReplaceChild("rightear_r2", CubeListBuilder.create().texOffs(52, 34).addBox(4.15F, -28.9F, -23.8F, 2.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 33.5F, 0.0F, -0.7418F, -0.2182F, -0.2618F));

        PartDefinition Head_r1 = Fins.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(59, 32).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.0F, 3.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition Head_r2 = Fins.addOrReplaceChild("Head_r2", CubeListBuilder.create().texOffs(32, 8).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, 3.0F, 0.2618F, 0.0F, 0.0F));

        PartDefinition Head_r3 = Fins.addOrReplaceChild("Head_r3", CubeListBuilder.create().texOffs(38, 56).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.5F, -1.0F, 0.6545F, 0.0F, 0.0F));

        PartDefinition Head_r4 = Fins.addOrReplaceChild("Head_r4", CubeListBuilder.create().texOffs(26, 60).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 6.5F, -3.5F, 1.0472F, 0.0F, -2.0071F));

        PartDefinition Head_r5 = Fins.addOrReplaceChild("Head_r5", CubeListBuilder.create().texOffs(47, 60).addBox(0.0F, 0.0F, -1.0F, 1.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 6.5F, -3.5F, 1.0472F, 0.0F, 2.0071F));

        PartDefinition Head_r6 = Fins.addOrReplaceChild("Head_r6", CubeListBuilder.create().texOffs(53, 56).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.5F, -3.5F, 1.0472F, 0.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Torso_r1 = Torso.addOrReplaceChild("Torso_r1", CubeListBuilder.create().texOffs(28, 12).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 12.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition TorsoLower_r1 = Torso.addOrReplaceChild("TorsoLower_r1", CubeListBuilder.create().texOffs(0, 28).addBox(-4.0F, -8.5F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 12.5F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightMembrane = Torso.addOrReplaceChild("RightMembrane", CubeListBuilder.create(), PartPose.offset(-1.5F, 5.5F, 0.5F));

        PartDefinition RightJoint = RightMembrane.addOrReplaceChild("RightJoint", CubeListBuilder.create(), PartPose.offset(-2.5F, 0.0F, 0.0F));

        PartDefinition RightArm_r1 = RightJoint.addOrReplaceChild("RightArm_r1", CubeListBuilder.create().texOffs(32, 50).addBox(-4.0F, 1.0F, 0.0F, 4.0F, 9.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.0F, 0.0F, -0.3927F));

        PartDefinition LeftMembrane = Torso.addOrReplaceChild("LeftMembrane", CubeListBuilder.create(), PartPose.offset(1.5F, 5.5F, 0.5F));

        PartDefinition LeftJoint = LeftMembrane.addOrReplaceChild("LeftJoint", CubeListBuilder.create(), PartPose.offset(2.5F, 0.0F, 0.0F));

        PartDefinition LeftArm_r1 = LeftJoint.addOrReplaceChild("LeftArm_r1", CubeListBuilder.create().texOffs(22, 50).addBox(0.0F, 1.0F, 0.0F, 4.0F, 9.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.3927F));

        PartDefinition Plantoids = Torso.addOrReplaceChild("Plantoids", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -2.0F));

        PartDefinition RightPlantoid_r1 = Plantoids.addOrReplaceChild("RightPlantoid_r1", CubeListBuilder.create().texOffs(54, 44).addBox(-4.25F, -1.7F, -0.8F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.03F))
                .texOffs(56, 26).addBox(0.25F, -1.7F, -0.8F, 4.0F, 4.0F, 2.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.2793F, 0.0F, 0.0F));

        PartDefinition Center_r1 = Plantoids.addOrReplaceChild("Center_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-0.5F, -1.3F, -0.1F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, -0.192F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(20, 34).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-5.0F, 1.5F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(36, 34).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 1.5F, 0.0F));

        PartDefinition Abdomen = partdefinition.addOrReplaceChild("Abdomen", CubeListBuilder.create().texOffs(24, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition LowerAbdomen = Abdomen.addOrReplaceChild("LowerAbdomen", CubeListBuilder.create().texOffs(0, 16).addBox(-4.5F, -1.25F, -2.5F, 9.0F, 7.0F, 5.0F, new CubeDeformation(0.4F)), PartPose.offset(0.0F, 4.5F, 0.0F));

        PartDefinition Tail = LowerAbdomen.addOrReplaceChild("Tail", CubeListBuilder.create().texOffs(24, 24).addBox(-4.0F, -0.75F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.35F)), PartPose.offset(0.0F, 6.0F, 0.0F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create().texOffs(0, 46).addBox(-3.5F, -0.25F, -2.0F, 7.0F, 5.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, 4.75F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create().texOffs(48, 0).addBox(-3.0F, -0.25F, -2.0F, 6.0F, 5.0F, 4.0F, new CubeDeformation(-0.175F)), PartPose.offset(0.0F, 4.5F, 0.0F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create().texOffs(0, 38).addBox(-2.0F, -0.25F, -1.5F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.05F)), PartPose.offset(0.0F, 4.75F, 0.0F));

        PartDefinition TailQuaternary = TailTertiary.addOrReplaceChild("TailQuaternary", CubeListBuilder.create().texOffs(56, 19).addBox(-1.5F, 0.0F, -1.5F, 3.0F, 4.0F, 3.0F, new CubeDeformation(-0.05F)), PartPose.offset(0.0F, 2.55F, 0.0F));

        PartDefinition TailQuintary = TailQuaternary.addOrReplaceChild("TailQuintary", CubeListBuilder.create(), PartPose.offset(0.0F, 2.75F, -0.5F));

        PartDefinition Base_r1 = TailQuintary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(48, 21).addBox(-0.4617F, -8.7695F, -2.8195F, 1.0F, 10.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(62, 9).addBox(-0.4617F, -6.7695F, 0.1805F, 1.0F, 7.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.5F, 4.875F, 0.25F, -2.6993F, -1.5577F, 0.6601F));

        PartDefinition Base_r2 = TailQuintary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(34, 60).addBox(-0.5117F, -6.7695F, 0.1805F, 1.0F, 7.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(49, 47).addBox(-0.5117F, -8.7695F, -2.8195F, 1.0F, 10.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.5F, 4.875F, 0.25F, 0.4423F, 1.5577F, 2.4815F));

        PartDefinition Base_r3 = TailQuintary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.1701F, -0.3405F, 2.0F, 5.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.5834F, -0.2961F, 0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 96, 96);
    }

    @Override
    public void prepareMobModel(LatexMantaRayFemale p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LatexMantaRayFemale entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public ModelPart getArm(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }

    public ModelPart getLeg(HumanoidArm p_102852_) {
        return null;
    }

    @Override
    public ModelPart getAbdomen() {
        return Abdomen;
    }

    @Override
    public HelperModel getTransfurHelperModel(Limb limb) {
        if (limb == Limb.ABDOMEN)
            return TransfurHelper.getLegless();
        if (limb == Limb.TORSO)
            return TransfurHelper.getFeminineTorsoAlt();
        return super.getTransfurHelperModel(limb);
    }

    public ModelPart getHead() {
        return this.Head;
    }

    public ModelPart getTorso() {
        return Torso;
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
    public HumanoidAnimator<LatexMantaRayFemale, LatexMantaRayFemaleModel> getAnimator() {
        return animator;
    }
}