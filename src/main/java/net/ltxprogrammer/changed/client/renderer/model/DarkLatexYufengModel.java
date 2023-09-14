package net.ltxprogrammer.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.beast.DarkLatexYufeng;
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
public class DarkLatexYufengModel extends LatexHumanoidModel<DarkLatexYufeng> implements LatexHumanoidModelInterface<DarkLatexYufeng, DarkLatexYufengModel> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("dark_latex_yufeng"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final ModelPart RightWing;
    private final ModelPart LeftWing;
    private final LatexAnimator<DarkLatexYufeng, DarkLatexYufengModel> animator;

    public DarkLatexYufengModel(ModelPart root) {
        super(root);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Tail = Torso.getChild("Tail");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        this.RightWing = Torso.getChild("RightWing");
        this.LeftWing = Torso.getChild("LeftWing");

        ModelPart leftWingRoot = LeftWing.getChild("leftWingRoot");
        ModelPart rightWingRoot = RightWing.getChild("rightWingRoot");
        animator = LatexAnimator.of(this).addPreset(AnimatorPresets.legacyDragonLike(Head, Torso, LeftArm, RightArm, Tail, List.of(), LeftLeg, RightLeg,
                leftWingRoot, leftWingRoot.getChild("leftSecondaries"), leftWingRoot.getChild("leftSecondaries").getChild("leftTertiaries"),
                rightWingRoot, rightWingRoot.getChild("rightSecondaries"), rightWingRoot.getChild("rightSecondaries").getChild("rightTertiaries")
        )).hipOffset(-2.0f);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(20, 18).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F))
                .texOffs(24, 18).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F))
                .texOffs(28, 32).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(12, 32).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(16, 32).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(16, 43).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(16, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(40, 11).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(44, 30).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(32, 32).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F))
                .texOffs(36, 16).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F))
                .texOffs(12, 34).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(32, 34).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(28, 34).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(16, 34).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(40, 21).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(32, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.5F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(48, 39).addBox(-1.5F, -0.5F, -6.0F, 3.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition Base_r1 = Head.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(56, 29).addBox(-1.9F, 0.15F, -0.3F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(5.5F, -4.5F, 0.0F, -1.2217F, 0.4712F, 0.6981F));

        PartDefinition Base_r2 = Head.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(32, 11).addBox(-1.4F, 0.25F, 2.3F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(5.5F, -4.5F, 0.0F, -1.4312F, 0.6458F, 0.5847F));

        PartDefinition Base_r3 = Head.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(54, 24).addBox(-0.1F, 0.15F, -0.3F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-5.5F, -4.5F, 0.0F, -1.2217F, -0.4712F, -0.6981F));

        PartDefinition Base_r4 = Head.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(54, 19).addBox(-0.6F, 0.25F, 2.3F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-5.5F, -4.5F, 0.0F, -1.4312F, -0.6458F, -0.5847F));

        PartDefinition Base_r5 = Head.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(69, 9).addBox(18.125F, -19.05F, 17.825F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(0.0F, 25.5F, 0.0F, 0.48F, 0.48F, -0.6981F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 63).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.2F))
                .texOffs(48, 61).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.5F, 0.0F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.5F, -7.5F, 0.0F, 0.0F, 0.0F, -0.2618F));

        PartDefinition Base_r6 = RightEar.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(58, 0).addBox(-0.6F, -1.5F, -3.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.5F, 0.0F, 0.0F, -0.2094F, -0.056F, 0.0059F));

        PartDefinition Base_r7 = RightEar.addOrReplaceChild("Base_r7", CubeListBuilder.create().texOffs(30, 42).addBox(-2.0F, -4.75F, -1.0F, 2.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, -0.2182F, 0.0785F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offsetAndRotation(2.5F, -7.5F, 0.0F, 0.0F, 0.0F, 0.2618F));

        PartDefinition Base_r8 = LeftEar.addOrReplaceChild("Base_r8", CubeListBuilder.create().texOffs(58, 4).addBox(-0.4F, -1.5F, -3.0F, 1.0F, 3.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.5F, 0.0F, 0.0F, -0.2094F, 0.056F, -0.0059F));

        PartDefinition Base_r9 = LeftEar.addOrReplaceChild("Base_r9", CubeListBuilder.create().texOffs(48, 0).addBox(0.0F, -4.75F, -1.0F, 2.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7854F, 0.2182F, -0.0785F));

        PartDefinition Mask = Head.addOrReplaceChild("Mask", CubeListBuilder.create().texOffs(52, 12).addBox(-1.0F, -31.0F, -5.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(58, 13).addBox(-2.0F, -33.0F, -5.0F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(56, 15).addBox(-3.0F, -32.0F, -5.0F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(70, 6).addBox(-1.0F, -34.0F, -5.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 32).addBox(-4.0F, -31.0F, -5.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 16).addBox(3.0F, -31.0F, -5.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(56, 17).addBox(-3.0F, -29.0F, -5.0F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(52, 10).addBox(-4.0F, -28.0F, -5.0F, 8.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(60, 6).addBox(-2.0F, -29.0F, -7.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 26.5F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.5F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition LeftWing = Torso.addOrReplaceChild("LeftWing", CubeListBuilder.create(), PartPose.offsetAndRotation(2.0F, 5.5F, 2.0F, 0.0F, -0.48F, 0.0F));

        PartDefinition leftWingRoot = LeftWing.addOrReplaceChild("leftWingRoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = leftWingRoot.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(68, 28).addBox(18.975F, -4.475F, 1.65F, 7.0F, 5.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.0F, 20.0F, -2.0F, 0.0F, 0.0F, -1.2654F));

        PartDefinition cube_r2 = leftWingRoot.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(56, 48).addBox(19.075F, -12.7F, 1.2F, 6.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.0F, 20.0F, -2.0F, 0.0F, 0.0F, -0.7854F));

        PartDefinition cube_r3 = leftWingRoot.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(62, 0).addBox(7.775F, -19.75F, 1.2F, 5.0F, 2.0F, 1.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(-2.0F, 20.0F, -2.0F, 0.0F, 0.0F, -0.3491F));

        PartDefinition leftSecondaries = leftWingRoot.addOrReplaceChild("leftSecondaries", CubeListBuilder.create().texOffs(0, 54).addBox(-0.8F, -0.475F, -0.3F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(7.3F, -7.0F, -0.5F, 0.0F, 0.0F, -0.5236F));

        PartDefinition cube_r4 = leftSecondaries.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(4, 1).addBox(-2.025F, -22.55F, 1.2F, 1.0F, 6.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-9.3F, 27.0F, -1.5F, 0.0F, 0.0F, 0.48F));

        PartDefinition cube_r5 = leftSecondaries.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(16, 52).addBox(15.525F, -13.85F, 1.648F, 9.0F, 6.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-9.3F, 27.0F, -1.5F, 0.0F, 0.0F, -0.7418F));

        PartDefinition cube_r6 = leftSecondaries.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(52, 51).addBox(13.4F, 10.625F, 1.651F, 10.0F, 5.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-9.3F, 27.0F, -1.5F, 0.0F, 0.0F, -1.8326F));

        PartDefinition leftTertiaries = leftSecondaries.addOrReplaceChild("leftTertiaries", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.3F, 0.0F, 0.0F, 0.0F, 0.0F, -0.9599F));

        PartDefinition cube_r7 = leftTertiaries.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(8, 54).addBox(-3.3F, -22.5F, 1.2F, 1.0F, 7.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-9.0F, 27.0F, -1.5F, 0.0F, 0.0F, 0.5236F));

        PartDefinition cube_r8 = leftTertiaries.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(16, 58).addBox(16.125F, -10.525F, 1.64F, 10.0F, 5.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-9.0F, 27.0F, -1.5F, 0.0F, 0.0F, -0.8727F));

        PartDefinition cube_r9 = leftTertiaries.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(28, 1).addBox(9.15F, -26.2F, 1.2F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(-9.0F, 27.0F, -1.5F, 0.0F, 0.0F, -0.0436F));

        PartDefinition RightWing = Torso.addOrReplaceChild("RightWing", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0F, 5.5F, 2.0F, 0.0F, 0.48F, 0.0F));

        PartDefinition rightWingRoot = RightWing.addOrReplaceChild("rightWingRoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r10 = rightWingRoot.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(64, 36).addBox(-25.975F, -4.475F, 1.65F, 7.0F, 5.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 20.0F, -2.0F, 0.0F, 0.0F, 1.2654F));

        PartDefinition cube_r11 = rightWingRoot.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(56, 45).addBox(-25.075F, -12.7F, 1.2F, 6.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 20.0F, -2.0F, 0.0F, 0.0F, 0.7854F));

        PartDefinition cube_r12 = rightWingRoot.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(62, 3).addBox(-12.775F, -19.75F, 1.2F, 5.0F, 2.0F, 1.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(2.0F, 20.0F, -2.0F, 0.0F, 0.0F, 0.3491F));

        PartDefinition rightSecondaries = rightWingRoot.addOrReplaceChild("rightSecondaries", CubeListBuilder.create().texOffs(4, 54).addBox(-0.2F, -0.475F, -0.3F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-7.3F, -7.0F, -0.5F, 0.0F, 0.0F, 0.5236F));

        PartDefinition cube_r13 = rightSecondaries.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(0, 1).addBox(1.025F, -22.55F, 1.2F, 1.0F, 6.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(9.3F, 27.0F, -1.5F, 0.0F, 0.0F, -0.48F));

        PartDefinition cube_r14 = rightSecondaries.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(56, 56).addBox(-23.4F, 10.625F, 1.651F, 10.0F, 5.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(9.3F, 27.0F, -1.5F, 0.0F, 0.0F, 1.8326F));

        PartDefinition cube_r15 = rightSecondaries.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(34, 52).addBox(-24.525F, -13.85F, 1.648F, 9.0F, 6.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(9.3F, 27.0F, -1.5F, 0.0F, 0.0F, 0.7418F));

        PartDefinition rightTertiaries = rightSecondaries.addOrReplaceChild("rightTertiaries", CubeListBuilder.create(), PartPose.offsetAndRotation(0.3F, 0.0F, 0.0F, 0.0F, 0.0F, 0.9599F));

        PartDefinition cube_r16 = rightTertiaries.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(12, 54).addBox(2.3F, -22.5F, 1.2F, 1.0F, 7.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(9.0F, 27.0F, -1.5F, 0.0F, 0.0F, -0.5236F));

        PartDefinition cube_r17 = rightTertiaries.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(24, 1).addBox(-10.15F, -26.2F, 1.2F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(9.0F, 27.0F, -1.5F, 0.0F, 0.0F, 0.0436F));

        PartDefinition cube_r18 = rightTertiaries.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(36, 58).addBox(-26.125F, -10.525F, 1.64F, 10.0F, 5.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(9.0F, 27.0F, -1.5F, 0.0F, 0.0F, 0.8727F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 11.5F, 0.0F));

        PartDefinition Base_r10 = Tail.addOrReplaceChild("Base_r10", CubeListBuilder.create().texOffs(60, 30).addBox(-1.0F, -10.35F, 13.925F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 14.0F, 0.0F, 0.0436F, 0.0F, 0.0F));

        PartDefinition Base_r11 = Tail.addOrReplaceChild("Base_r11", CubeListBuilder.create().texOffs(52, 36).addBox(-1.5F, -13.2F, 6.9F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 14.0F, 0.0F, -0.1309F, 0.0F, 0.0F));

        PartDefinition Base_r12 = Tail.addOrReplaceChild("Base_r12", CubeListBuilder.create().texOffs(62, 19).addBox(-1.5F, -1.375F, -2.6983F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(0.0F, 2.0F, 6.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition Base_r13 = Tail.addOrReplaceChild("Base_r13", CubeListBuilder.create().texOffs(40, 42).addBox(-2.0F, -2.8F, 0.4F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(24, 16).addBox(-3.0F, -1.5F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(40, 30).addBox(0.0F, 10.25F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(40, 13).addBox(-3.0F, 10.25F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(38, 11).addBox(-3.0F, 10.25F, -0.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(36, 18).addBox(-3.0F, 10.25F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 0.5F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, -1.5F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(44, 30).addBox(-1.0F, 10.25F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(44, 2).addBox(2.0F, 10.25F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(44, 0).addBox(2.0F, 10.25F, -0.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(27, 43).addBox(2.0F, 10.25F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 0.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 96, 96);
    }

    @Override
    public void prepareMobModel(DarkLatexYufeng p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull DarkLatexYufeng entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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
    public LatexAnimator<DarkLatexYufeng, DarkLatexYufengModel> getAnimator() {
        return animator;
    }

    public static class Remodel extends LatexHumanoidModel.LatexRemodel<DarkLatexYufeng, Remodel> {
        private final ModelPart RightLeg;
        private final ModelPart LeftLeg;
        private final ModelPart RightArm;
        private final ModelPart LeftArm;
        private final ModelPart Head;
        private final ModelPart Torso;
        private final ModelPart Tail;
        private final ModelPart RightWing;
        private final ModelPart LeftWing;
        private final LatexAnimator<DarkLatexYufeng, Remodel> animator;

        public Remodel(ModelPart root) {
            super(root);
            this.RightLeg = root.getChild("RightLeg");
            this.LeftLeg = root.getChild("LeftLeg");
            this.Head = root.getChild("Head");
            this.Torso = root.getChild("Torso");
            this.Tail = Torso.getChild("Tail");
            this.RightArm = root.getChild("RightArm");
            this.LeftArm = root.getChild("LeftArm");
            this.RightWing = Torso.getChild("RightWing");
            this.LeftWing = Torso.getChild("LeftWing");

            ModelPart leftWingRoot = LeftWing.getChild("WingRoot");
            ModelPart rightWingRoot = RightWing.getChild("WingRoot2");
            animator = LatexAnimator.of(this).addPreset(AnimatorPresets.dragonLikeV2(Head, Torso, LeftArm, RightArm, Tail, List.of(), LeftLeg, RightLeg,
                    leftWingRoot, leftWingRoot.getChild("bone3"), leftWingRoot.getChild("bone3").getChild("bone4"),
                    rightWingRoot, rightWingRoot.getChild("bone"), rightWingRoot.getChild("bone").getChild("bone2")
            )).hipOffset(0.0f);
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();

            PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition Base_r1 = Head.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(24, 3).addBox(-0.6F, 0.35F, 2.3F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-5.5F, -5.0F, 0.0F, -1.4312F, -0.6458F, -0.4974F));

            PartDefinition Base_r2 = Head.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(59, 59).addBox(-1.4F, 0.35F, 2.3F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(5.5F, -5.0F, 0.0F, -1.4312F, 0.6458F, 0.4974F));

            PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(58, 38).addBox(-1.5F, -27.0F, -6.0F, 3.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 26.0F, 0.0F, 0.0F, 0.0F, 0.0F));

            PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.5F, -8.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

            PartDefinition Base_r3 = RightEar.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(36, 15).addBox(-0.6209F, -1.775F, -2.9526F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.5F, 0.0F, 0.0F, -0.2094F, -0.056F, 0.0059F));

            PartDefinition Base_r4 = RightEar.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-1.3F, -1.5F, -0.975F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.7872F, 2.969F, -0.6745F, -2.087F, -0.3257F, 1.3714F));

            PartDefinition Base_r5 = RightEar.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(41, 51).addBox(-2.0088F, -5.0341F, -1.0F, 2.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7681F, -0.1841F, 0.0746F));

            PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offsetAndRotation(2.5F, -8.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

            PartDefinition Base_r6 = LeftEar.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(12, 48).addBox(-0.3791F, -1.77F, -2.9526F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.5F, 0.0F, 0.0F, -0.2094F, 0.056F, -0.0059F));

            PartDefinition Base_r7 = LeftEar.addOrReplaceChild("Base_r7", CubeListBuilder.create().texOffs(22, 60).addBox(-1.7048F, -0.3091F, -1.3212F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.0F, 3.0F, 0.0F, -1.1519F, 0.4605F, 0.4624F));

            PartDefinition Base_r8 = LeftEar.addOrReplaceChild("Base_r8", CubeListBuilder.create().texOffs(48, 32).addBox(0.0088F, -5.0341F, -1.0F, 2.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7681F, 0.1841F, -0.0746F));

            PartDefinition Mask = Head.addOrReplaceChild("Mask", CubeListBuilder.create().texOffs(22, 54).addBox(-1.0F, -33.999F, -5.0F, 2.0F, 5.0F, 1.0F, CubeDeformation.NONE)
                    .texOffs(40, 42).addBox(-2.0F, -33.0F, -5.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                    .texOffs(40, 22).addBox(1.0F, -33.0F, -5.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                    .texOffs(44, 32).addBox(2.0F, -32.0F, -5.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                    .texOffs(25, 44).addBox(-3.0F, -32.0F, -5.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                    .texOffs(0, 32).addBox(-4.0F, -31.0F, -5.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                    .texOffs(0, 16).addBox(3.0F, -31.0F, -5.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                    .texOffs(44, 2).addBox(2.0F, -29.0F, -5.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                    .texOffs(44, 0).addBox(-3.0F, -29.0F, -5.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                    .texOffs(28, 32).addBox(-4.0F, -28.0F, -5.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                    .texOffs(12, 32).addBox(2.0F, -28.0F, -5.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                    .texOffs(53, 20).addBox(-2.0F, -29.0F, -7.0F, 4.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 26.0F, 0.0F));

            PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 11.0F, 0.0F));

            PartDefinition Base_r9 = Tail.addOrReplaceChild("Base_r9", CubeListBuilder.create().texOffs(0, 57).addBox(-1.0F, 0.1914F, -0.9483F, 2.0F, 7.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 2.0F, 6.0F, 1.4835F, 0.0F, 0.0F));

            PartDefinition Base_r10 = Tail.addOrReplaceChild("Base_r10", CubeListBuilder.create().texOffs(16, 44).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

            PartDefinition RightWing = Torso.addOrReplaceChild("RightWing", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.0F, 5.0F, 2.0F, -0.0305F, 0.6102F, -0.0532F));

            PartDefinition WingRoot2 = RightWing.addOrReplaceChild("WingRoot2", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition cube_r1 = WingRoot2.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(56, 42).addBox(-5.75F, -4.5F, 0.5F, 5.0F, 7.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, -0.2182F));

            PartDefinition cube_r2 = WingRoot2.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(58, 35).addBox(-5.6742F, -1.4784F, 0.0F, 6.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, -1.0F, -1.0F, 0.0F, 0.0F, 0.7854F));

            PartDefinition cube_r3 = WingRoot2.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(32, 12).addBox(-4.0F, -1.0F, 0.0F, 5.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.3491F));

            PartDefinition bone = WingRoot2.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(46, 61).addBox(-0.5872F, -1.0038F, -0.5F, 1.0F, 7.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-7.3F, -5.8F, -0.5F, 0.0F, 0.0F, 0.5236F));

            PartDefinition cube_r4 = bone.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(0, 48).addBox(-7.25F, -8.5F, 0.45F, 6.0F, 9.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(7.3F, 5.8F, -0.5F, 0.0F, 0.0F, -0.7418F));

            PartDefinition cube_r5 = bone.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(12, 54).addBox(-8.25F, -3.75F, 0.5F, 5.0F, 10.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(6.9628F, 5.2962F, -0.5F, 0.0F, 0.0F, 0.2182F));

            PartDefinition cube_r6 = bone.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(30, 62).addBox(-0.875F, -0.5F, -0.5F, 1.0F, 6.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.4128F, 5.9962F, 0.0F, 0.0F, 0.0F, -0.48F));

            PartDefinition bone2 = bone.addOrReplaceChild("bone2", CubeListBuilder.create().texOffs(50, 61).addBox(-0.6609F, -0.9453F, -0.5F, 1.0F, 6.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.4263F, 0.4415F, 0.0F, 0.0F, 0.0F, 0.9599F));

            PartDefinition cube_r7 = bone2.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(56, 10).addBox(-7.15F, -9.175F, 0.45F, 5.0F, 10.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(7.3891F, 4.8547F, -0.5F, 0.0F, 0.0F, -0.7418F));

            PartDefinition cube_r8 = bone2.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(38, 60).addBox(-0.875F, -0.5F, -0.5F, 1.0F, 7.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.3391F, 5.0547F, 0.0F, 0.0F, 0.0F, -0.48F));

            PartDefinition LeftWing = Torso.addOrReplaceChild("LeftWing", CubeListBuilder.create(), PartPose.offsetAndRotation(2.0F, 5.0F, 2.0F, -3.0842F, 0.5656F, -3.1097F));

            PartDefinition WingRoot = LeftWing.addOrReplaceChild("WingRoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition cube_r9 = WingRoot.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(56, 25).addBox(-5.75F, -4.5F, 0.5F, 5.0F, 7.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, -0.2182F));

            PartDefinition cube_r10 = WingRoot.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(55, 32).addBox(-9.2097F, 0.6429F, 0.0F, 6.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

            PartDefinition cube_r11 = WingRoot.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(24, 0).addBox(-4.0F, -1.0F, 0.0F, 5.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.3491F));

            PartDefinition bone3 = WingRoot.addOrReplaceChild("bone3", CubeListBuilder.create().texOffs(42, 61).addBox(-0.5872F, -1.0038F, -0.5F, 1.0F, 7.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-7.3F, -5.8F, 0.5F, 0.0F, 0.0F, 0.5236F));

            PartDefinition cube_r12 = bone3.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(44, 42).addBox(-7.25F, -8.5F, 0.55F, 6.0F, 9.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(7.3F, 5.8F, -0.5F, 0.0F, 0.0F, -0.7418F));

            PartDefinition cube_r13 = bone3.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(51, 51).addBox(-8.25F, -3.75F, 0.5F, 5.0F, 10.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(6.9628F, 5.2962F, -0.5F, 0.0F, 0.0F, 0.2182F));

            PartDefinition cube_r14 = bone3.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(54, 61).addBox(-0.875F, -0.5F, -0.5F, 1.0F, 6.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.4128F, 5.9962F, 0.0F, 0.0F, 0.0F, -0.48F));

            PartDefinition bone4 = bone3.addOrReplaceChild("bone4", CubeListBuilder.create().texOffs(61, 49).addBox(-0.6609F, -0.9453F, -0.5F, 1.0F, 6.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.4263F, 0.4415F, 0.0F, 0.0F, 0.0F, 0.9599F));

            PartDefinition cube_r15 = bone4.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(28, 52).addBox(-7.15F, -9.175F, 0.55F, 5.0F, 10.0F, 0.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(7.3891F, 4.8547F, -0.5F, 0.0F, 0.0F, -0.7418F));

            PartDefinition cube_r16 = bone4.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(8, 57).addBox(-0.875F, -0.5F, -0.5F, 1.0F, 7.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.3391F, 5.0547F, 0.0F, 0.0F, 0.0F, -0.48F));

            PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(24, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 2.0F, 0.0F));

            PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 2.0F, 0.0F));

            PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.25F, 12.0F, 0.0F));

            PartDefinition leg_r1 = RightLeg.addOrReplaceChild("leg_r1", CubeListBuilder.create().texOffs(40, 12).addBox(-2.0F, -6.7987F, -2.9677F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 5.275F, 4.9F, 1.2217F, 0.0F, 0.0F));

            PartDefinition thigh_r1 = RightLeg.addOrReplaceChild("thigh_r1", CubeListBuilder.create().texOffs(28, 42).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, -0.1F, 0.0F, -0.0873F, 0.0F, 0.0F));

            PartDefinition RightLower = RightLeg.addOrReplaceChild("RightLower", CubeListBuilder.create().texOffs(48, 5).addBox(-2.0F, 6.975F, -4.675F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.025F)), PartPose.offset(0.0F, 4.0F, 4.5F));

            PartDefinition leg_r2 = RightLower.addOrReplaceChild("leg_r2", CubeListBuilder.create().texOffs(16, 32).addBox(-2.0F, -0.225F, -3.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 0.15F, -1.0F, -0.0873F, 0.0F, 0.0F));

            PartDefinition RightLowerBeans = RightLower.addOrReplaceChild("RightLowerBeans", CubeListBuilder.create().texOffs(0, 5).addBox(1.0F, -13.025F, 0.325F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.075F))
                    .texOffs(16, 34).addBox(1.5F, -13.025F, -1.175F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F))
                    .texOffs(12, 34).addBox(0.225F, -13.025F, -0.925F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F))
                    .texOffs(44, 34).addBox(2.8F, -13.025F, -0.925F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)), PartPose.offset(-2.0F, 20.0F, -3.2F));

            PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.25F, 12.0F, 0.0F));

            PartDefinition leg_r3 = LeftLeg.addOrReplaceChild("leg_r3", CubeListBuilder.create().texOffs(32, 32).addBox(-2.0F, -6.7987F, -2.9677F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 5.275F, 4.9F, 1.2217F, 0.0F, 0.0F));

            PartDefinition thigh_r2 = LeftLeg.addOrReplaceChild("thigh_r2", CubeListBuilder.create().texOffs(40, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, -0.1F, 0.0F, -0.0873F, 0.0F, 0.0F));

            PartDefinition LeftLower = LeftLeg.addOrReplaceChild("LeftLower", CubeListBuilder.create().texOffs(48, 0).addBox(-2.0F, 6.975F, -4.675F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.025F)), PartPose.offset(0.0F, 4.0F, 4.5F));

            PartDefinition leg_r4 = LeftLower.addOrReplaceChild("leg_r4", CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, -0.225F, -3.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 0.15F, -1.0F, -0.0873F, 0.0F, 0.0F));

            PartDefinition LeftLowerBeans2 = LeftLower.addOrReplaceChild("LeftLowerBeans2", CubeListBuilder.create().texOffs(20, 16).addBox(1.0F, -13.025F, 0.325F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.075F))
                    .texOffs(32, 34).addBox(1.5F, -13.025F, -1.175F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F))
                    .texOffs(28, 34).addBox(0.225F, -13.025F, -0.925F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F))
                    .texOffs(47, 1).addBox(2.8F, -13.025F, -0.925F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.075F)), PartPose.offset(-2.0F, 20.0F, -3.2F));

            return LayerDefinition.create(meshdefinition, 128, 128);
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
        public LatexAnimator<DarkLatexYufeng, Remodel> getAnimator() {
            return animator;
        }
    }
}