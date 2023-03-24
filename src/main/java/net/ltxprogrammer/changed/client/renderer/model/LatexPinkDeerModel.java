package net.ltxprogrammer.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.beast.LatexPinkDeer;
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
public class LatexPinkDeerModel extends LatexHumanoidModel<LatexPinkDeer> implements LatexHumanoidModelInterface<LatexPinkDeer, LatexPinkDeerModel> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_pink_deer"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final LatexAnimator<LatexPinkDeer, LatexPinkDeerModel> animator;

    public LatexPinkDeerModel(ModelPart root) {
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

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(53, 0).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(23, 56).addBox(-3.89F, -1.1F, -0.17F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(21, 55).addBox(-1.11F, -1.1F, -0.17F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(20, 55).addBox(-2.5F, -1.1F, -0.17F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.8727F, 0.0F, 0.0F));

        PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(22, 55).addBox(-3.89F, -3.1F, -1.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(18, 55).addBox(-2.5F, -3.1F, -1.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(21, 56).addBox(-1.11F, -3.1F, -1.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(16, 51).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(40, 16).addBox(-1.99F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(32, 43).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(52, 12).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(22, 56).addBox(-3.89F, -1.1F, -0.17F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(22, 54).addBox(-2.5F, -1.1F, -0.17F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(22, 55).addBox(-1.11F, -1.1F, -0.17F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.8727F, 0.0F, 0.0F));

        PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(21, 55).addBox(-1.11F, -3.1F, -1.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(19, 55).addBox(-2.5F, -3.1F, -1.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(23, 55).addBox(-3.89F, -3.1F, -1.6F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(48, 44).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(34, 32).addBox(-2.01F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(16, 41).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.5F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(61, 35).addBox(-2.0F, -2.5F, -6.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(20, 16).addBox(-1.5F, -0.5F, -5.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition Antler_r1 = Head.addOrReplaceChild("Antler_r1", CubeListBuilder.create().texOffs(66, 46).addBox(1.0F, -38.0F, 4.75F, 1.0F, 6.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(50, 41).addBox(1.0F, -35.0F, 3.75F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 25.5F, 0.0F, 0.1745F, -0.0873F, -0.1309F));

        PartDefinition Antler_r2 = Head.addOrReplaceChild("Antler_r2", CubeListBuilder.create().texOffs(62, 48).addBox(1.0F, -37.5F, -10.25F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 25.5F, 0.0F, -0.2618F, -0.0873F, -0.1309F));

        PartDefinition Antler_r3 = Head.addOrReplaceChild("Antler_r3", CubeListBuilder.create().texOffs(54, 39).addBox(1.0F, -34.5F, 9.0F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 25.5F, 0.0F, 0.3491F, -0.0873F, -0.1309F));

        PartDefinition Antler_r4 = Head.addOrReplaceChild("Antler_r4", CubeListBuilder.create().texOffs(66, 46).addBox(-2.0F, -38.0F, 4.75F, 1.0F, 6.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(50, 41).addBox(-2.0F, -35.0F, 3.75F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 25.5F, 0.0F, 0.1745F, 0.0873F, 0.1309F));

        PartDefinition Antler_r5 = Head.addOrReplaceChild("Antler_r5", CubeListBuilder.create().texOffs(54, 39).addBox(-2.0F, -37.5F, -10.25F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 25.5F, 0.0F, -0.2618F, 0.0873F, 0.1309F));

        PartDefinition Antler_r6 = Head.addOrReplaceChild("Antler_r6", CubeListBuilder.create().texOffs(62, 48).addBox(-2.0F, -34.5F, 9.0F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 25.5F, 0.0F, 0.3491F, 0.0873F, 0.1309F));

        PartDefinition Horn_r1 = Head.addOrReplaceChild("Horn_r1", CubeListBuilder.create().texOffs(32, 55).addBox(3.05F, -26.3F, -14.85F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.03F)), PartPose.offsetAndRotation(0.0F, 25.5F, 1.0F, -0.5236F, 0.3927F, 0.0F));

        PartDefinition Horn_r2 = Head.addOrReplaceChild("Horn_r2", CubeListBuilder.create().texOffs(32, 55).addBox(3.15F, -30.3F, -2.1F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.04F)), PartPose.offsetAndRotation(0.0F, 25.5F, 1.0F, -0.0873F, 0.3927F, 0.0F));

        PartDefinition Horn_r3 = Head.addOrReplaceChild("Horn_r3", CubeListBuilder.create().texOffs(32, 55).addBox(3.35F, -29.3F, 9.9F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.06F)), PartPose.offsetAndRotation(0.0F, 25.5F, 1.0F, 0.3054F, 0.3927F, 0.0F));

        PartDefinition Horn_r4 = Head.addOrReplaceChild("Horn_r4", CubeListBuilder.create().texOffs(32, 55).addBox(-4.05F, -26.3F, -14.85F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.03F)), PartPose.offsetAndRotation(0.0F, 25.5F, 1.0F, -0.5236F, -0.3927F, 0.0F));

        PartDefinition Horn_r5 = Head.addOrReplaceChild("Horn_r5", CubeListBuilder.create().texOffs(32, 55).addBox(-4.15F, -30.3F, -2.1F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.04F)), PartPose.offsetAndRotation(0.0F, 25.5F, 1.0F, -0.0873F, -0.3927F, 0.0F));

        PartDefinition Horn_r6 = Head.addOrReplaceChild("Horn_r6", CubeListBuilder.create().texOffs(32, 55).addBox(-4.35F, -29.3F, 9.9F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.06F)), PartPose.offsetAndRotation(0.0F, 25.5F, 1.0F, 0.3054F, -0.3927F, 0.0F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(60, 10).addBox(-2.0F, -34.9F, -4.0F, 4.0F, 1.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(63, 20).addBox(-4.0F, -34.5F, -3.5F, 8.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(76, 13).addBox(2.0F, -34.75F, 0.0F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(79, 18).addBox(2.5F, -34.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.025F))
                .texOffs(62, 8).addBox(-3.5F, -33.0F, -4.25F, 7.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(53, 0).addBox(3.5F, -34.0F, -4.5F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(65, 1).addBox(-4.5F, -34.0F, -4.5F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(91, 18).addBox(2.5F, -31.0F, -1.0F, 2.0F, 3.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(84, 9).addBox(-4.5F, -31.0F, -1.0F, 2.0F, 3.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(73, 29).addBox(-4.5F, -34.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.025F))
                .texOffs(69, 27).addBox(-4.0F, -34.75F, 0.0F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(70, 40).addBox(-4.0F, -34.0F, -4.5F, 8.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(83, 11).addBox(-1.0F, -32.25F, -4.25F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(74, 0).addBox(-4.0F, -34.0F, 4.0F, 8.0F, 7.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 26.5F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.5F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(61, 39).addBox(-2.0F, 2.5F, -2.75F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(26, 60).addBox(-3.0F, 2.5F, 1.5F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(59, 43).addBox(-3.0F, 0.5F, -2.75F, 6.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(60, 6).addBox(-3.0F, 3.5F, -2.5F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(44, 8).addBox(-4.0F, 0.5F, -2.5F, 8.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(51, 53).addBox(-4.0F, 0.5F, 1.5F, 8.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(58, 32).addBox(-3.0F, 0.5F, 1.75F, 6.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition LeftTuft_r1 = Torso.addOrReplaceChild("LeftTuft_r1", CubeListBuilder.create().texOffs(56, 20).addBox(-11.5F, -23.5F, -2.5F, 1.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 25.5F, 0.0F, 0.0F, 0.0F, 0.6109F));

        PartDefinition RightTuft_r1 = Torso.addOrReplaceChild("RightTuft_r1", CubeListBuilder.create().texOffs(54, 56).addBox(10.5F, -23.5F, -2.5F, 1.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 25.5F, 0.0F, 0.0F, 0.0F, -0.6109F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 11.5F, 0.0F));

        PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(38, 0).addBox(-1.0F, -10.35F, 13.925F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 14.0F, 0.0F, 0.0436F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(32, 7).addBox(-1.5F, -13.2F, 6.9F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 14.0F, 0.0F, -0.1309F, 0.0F, 0.0F));

        PartDefinition Base_r3 = Tail.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(16, 32).addBox(-1.5F, -1.375F, -2.6983F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(0.0F, 2.0F, 6.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition Base_r4 = Tail.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, -2.8F, 0.4F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(24, 16).addBox(-3.0F, -1.5F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(46, 33).addBox(-0.11F, 10.25F, -1.89F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(46, 31).addBox(-2.89F, 10.25F, 0.89F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(46, 2).addBox(-2.89F, 10.25F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(46, 0).addBox(-2.89F, 10.25F, -1.89F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offset(-5.0F, 0.5F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, -1.5F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 50).addBox(-0.89F, 10.25F, -1.89F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(12, 48).addBox(1.89F, 10.25F, 0.89F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(0, 48).addBox(1.89F, 10.25F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(47, 45).addBox(1.89F, 10.25F, -1.89F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offset(5.0F, 0.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void prepareMobModel(LatexPinkDeer p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LatexPinkDeer entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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
    public LatexAnimator<LatexPinkDeer, LatexPinkDeerModel> getAnimator() {
        return animator;
    }
}