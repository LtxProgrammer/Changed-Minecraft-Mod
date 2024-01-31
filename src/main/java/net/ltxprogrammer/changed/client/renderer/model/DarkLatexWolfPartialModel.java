package net.ltxprogrammer.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.CubeListBuilderExtender;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.beast.DarkLatexWolfMale;
import net.ltxprogrammer.changed.entity.beast.DarkLatexWolfPartial;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class DarkLatexWolfPartialModel extends LatexHumanoidModel<DarkLatexWolfPartial> implements LatexHumanoidModelInterface<DarkLatexWolfPartial, DarkLatexWolfPartialModel> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION_HUMAN = new ModelLayerLocation(Changed.modResource("dark_latex_wolf_partial"), "main");
    public static final ModelLayerLocation LAYER_LOCATION_HUMAN_SLIM = new ModelLayerLocation(Changed.modResource("dark_latex_wolf_partial"), "main_slim");
    public static final ModelLayerLocation LAYER_LOCATION_LATEX = new ModelLayerLocation(Changed.modResource("dark_latex_wolf_partial"), "latex");
    public static final ModelLayerLocation LAYER_LOCATION_LATEX_SLIM = new ModelLayerLocation(Changed.modResource("dark_latex_wolf_partial"), "latex_slim");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;

    private final ModelPart RightPants;
    private final ModelPart LeftPants;
    private final ModelPart RightSleeve;
    private final ModelPart LeftSleeve;
    private final ModelPart Hat;
    private final ModelPart Jacket;

    private final LatexAnimator<DarkLatexWolfPartial, DarkLatexWolfPartialModel> animator;

    private static final ModelPart NULL_PART = new ModelPart(List.of(), Map.of());

    public DarkLatexWolfPartialModel(ModelPart root, boolean latexLayer) {
        super(root);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Tail = latexLayer ? Torso.getChild("Tail") : NULL_PART;
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");

        ModelPart tailPrimary;
        ModelPart tailSecondary;
        ModelPart tailTertiary;

        ModelPart leftEar, rightEar;

        ModelPart leftLowerLeg;
        ModelPart leftFoot;
        ModelPart leftPad;
        ModelPart rightLowerLeg;
        ModelPart rightFoot;
        ModelPart rightPad;

        if (latexLayer) {
            RightPants = NULL_PART;
            LeftPants = NULL_PART;
            RightSleeve = NULL_PART;
            LeftSleeve = NULL_PART;
            Hat = NULL_PART;
            Jacket = NULL_PART;

            tailPrimary = Tail.getChild("TailPrimary");
            tailSecondary = tailPrimary.getChild("TailSecondary");
            tailTertiary = tailSecondary.getChild("TailTertiary");

            leftEar = Head.getChild("LeftEar");
            rightEar = Head.getChild("RightEar");

            leftLowerLeg = LeftLeg.getChild("LeftLowerLeg");
            leftFoot = leftLowerLeg.getChild("LeftFoot");
            leftPad = leftFoot.getChild("LeftPad");
            rightLowerLeg = RightLeg.getChild("RightLowerLeg");
            rightFoot = rightLowerLeg.getChild("RightFoot");
            rightPad = rightFoot.getChild("RightPad");
        } else {
            RightPants = RightLeg.getChild("RightPants");
            LeftPants = LeftLeg.getChild("LeftPants");
            RightSleeve = RightArm.getChild("RightSleeve");
            LeftSleeve = LeftArm.getChild("LeftSleeve");
            Hat = Head.getChild("Hat");
            Jacket = Torso.getChild("Jacket");

            tailPrimary = NULL_PART;
            tailSecondary = NULL_PART;
            tailTertiary = NULL_PART;

            leftEar = NULL_PART;
            rightEar = NULL_PART;

            leftLowerLeg = NULL_PART;
            leftFoot = NULL_PART;
            leftPad = NULL_PART;
            rightLowerLeg = NULL_PART;
            rightFoot = NULL_PART;
            rightPad = NULL_PART;
        }

        animator = LatexAnimator.of(this).hipOffset(-1.5f)
                .addPreset(AnimatorPresets.wolfLike(
                        Head, leftEar, rightEar,
                        Torso, LeftArm, RightArm,
                        Tail, List.of(tailPrimary, tailSecondary, tailTertiary),
                        LeftLeg, leftLowerLeg, leftFoot, leftPad, RightLeg, rightLowerLeg, rightFoot, rightPad));
    }

    public static DarkLatexWolfPartialModel human(ModelPart root) {
        return new DarkLatexWolfPartialModel(root, false);
    }

    public static DarkLatexWolfPartialModel latex(ModelPart root) {
        return new DarkLatexWolfPartialModel(root, true);
    }

    public void defaultModelProperties() {
        Hat.visible = true;
        Jacket.visible = true;
        LeftPants.visible = true;
        RightPants.visible = true;
        LeftSleeve.visible = true;
        RightSleeve.visible = true;
    }

    public void setModelProperties(AbstractClientPlayer player) {
        Hat.visible = player.isModelPartShown(PlayerModelPart.HAT);
        Jacket.visible = player.isModelPartShown(PlayerModelPart.JACKET);
        LeftPants.visible = player.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
        RightPants.visible = player.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
        LeftSleeve.visible = player.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
        RightSleeve.visible = player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
    }

    public static LayerDefinition createHumanLayer(boolean slim) {
        float armWidth = slim ? 3.0f : 4.0f;
        float rightArmOffset = slim ? 1.0f : 0.0f;

        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));

        PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightPants = RightLeg.addOrReplaceChild("RightPants", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        final var rightPantCubes = ((CubeListBuilderExtender)CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.5F))).removeLastFaces(Direction.DOWN);

        PartDefinition RightThighLayer_r1 = RightPants.addOrReplaceChild("RightThighLayer_r1", rightPantCubes, PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));

        PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftPants = LeftLeg.addOrReplaceChild("LeftPants", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        final var leftPantCubes = ((CubeListBuilderExtender)CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.5F))).removeLastFaces(Direction.DOWN);

        PartDefinition LeftThighLayer_r1 = LeftPants.addOrReplaceChild("LeftThighLayer_r1", leftPantCubes, PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Hat = Head.addOrReplaceChild("Hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Jacket = Torso.addOrReplaceChild("Jacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F + rightArmOffset, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 1.5F, 0.0F));

        final var rightSleeveCube = ((CubeListBuilderExtender)CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F + rightArmOffset, -2.0F, -2.0F, armWidth, 9.0F, 4.0F, new CubeDeformation(0.5F))).removeLastFaces(Direction.DOWN);

        PartDefinition RightSleeve = RightArm.addOrReplaceChild("RightSleeve", rightSleeveCube, PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 1.5F, 0.0F));

        final var leftSleeveCube = ((CubeListBuilderExtender)CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, armWidth, 9.0F, 4.0F, new CubeDeformation(0.5F))).removeLastFaces(Direction.DOWN);

        PartDefinition LeftSleeve = LeftArm.addOrReplaceChild("LeftSleeve", leftSleeveCube, PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static LayerDefinition createLatexLayer(boolean slim) {
        float armWidth = slim ? 3.0f : 4.0f;
        float rightArmOffset = slim ? 1.0f : 0.0f;

        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));

        PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(12, 24).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(28, 20).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(30, 40).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(32, 11).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));

        PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(28, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(28, 30).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(0, 42).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(0, 35).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offset(-3.0F, -7.5F, 0.0F));

        PartDefinition RightEarPivot = RightEar.addOrReplaceChild("RightEarPivot", CubeListBuilder.create().texOffs(20, 4).addBox(-1.9F, -1.2F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(23, 0).addBox(-0.9F, -1.6F, -0.4F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(6, 28).addBox(-0.9F, -2.3F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(0, 0).addBox(0.1F, -3.1F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.5F, -1.25F, 0.0F, -0.1309F, 0.5236F, -0.3491F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offset(3.0F, -7.5F, 0.0F));

        PartDefinition LeftEarPivot = LeftEar.addOrReplaceChild("LeftEarPivot", CubeListBuilder.create().texOffs(15, 0).addBox(-1.1F, -1.2F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(0, 28).addBox(-1.1F, -1.6F, -0.4F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(6, 30).addBox(-1.1F, -2.3F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(0, 2).addBox(-1.1F, -3.1F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-0.5F, -1.25F, 0.0F, -0.1309F, -0.5236F, 0.3491F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 10.5F, 0.0F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition Base_r1 = TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(14, 38).addBox(-2.0F, 0.75F, -1.5F, 4.0F, 5.0F, 4.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.25F, 4.5F));

        PartDefinition Base_r2 = TailSecondary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(0, 0).addBox(-2.5F, -0.45F, -2.1F, 5.0F, 7.0F, 5.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.75F, 2.5F));

        PartDefinition Base_r3 = TailTertiary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(40, 26).addBox(-2.0F, -1.2F, -1.95F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.15F)), PartPose.offsetAndRotation(0.0F, 0.0F, 4.0F, 1.8326F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(16, 8).addBox(-3.0F + rightArmOffset, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 1.5F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 12).addBox(-1.0F, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 1.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void prepareMobModel(DarkLatexWolfPartial p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull DarkLatexWolfPartial entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public ModelPart getArm(HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
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
    public LatexAnimator<DarkLatexWolfPartial, DarkLatexWolfPartialModel> getAnimator() {
        return animator;
    }
}