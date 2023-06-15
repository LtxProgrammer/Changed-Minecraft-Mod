package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.beast.LatexSquirrel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;

import java.util.List;

public class LatexSquirrelModel extends LatexHumanoidModel<LatexSquirrel> implements LatexHumanoidModelInterface<LatexSquirrel, LatexSquirrelModel> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_squirrel"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final LatexAnimator<LatexSquirrel, LatexSquirrelModel> animator;

    public LatexSquirrelModel(ModelPart root) {
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

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(73, 29).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(22, 43).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(26, 43).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(46, 5).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(0, 44).addBox(-1.0F, -3.001F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(22, 45).addBox(-2.5F, -3.001F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(26, 45).addBox(-4.0F, -3.001F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(72, 54).addBox(-1.995F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(60, 68).addBox(-1.99F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(70, 41).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(72, 17).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(28, 47).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(28, 49).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(50, 23).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(50, 5).addBox(-1.0F, -3.001F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(50, 19).addBox(-2.5F, -3.001F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(50, 21).addBox(-4.0F, -3.001F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(18, 72).addBox(-2.005F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(20, 61).addBox(-2.01F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(32, 69).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 14).addBox(-4.0F, -7.5F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(56, 57).addBox(-2.0F, -2.5F, -6.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(24, 19).addBox(-1.5F, -0.5F, -5.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(24, 17).addBox(-1.0F, -29.625F, -0.95F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.5F, 26.5F, 0.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create().texOffs(87, 0).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 6.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 32).addBox(-0.5F, -2.0F, -1.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(27, 53).addBox(0.5F, -3.0F, -1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 14).addBox(-0.5F, -1.0F, 0.0F, 2.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(24, 14).addBox(-0.5F, 0.0F, -1.4F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.1F, -9.5F, 0.0F, 0.0F, 0.0F, -0.1309F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create().texOffs(87, 14).addBox(-1.0F, -1.035F, -1.0F, 3.0F, 6.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 34).addBox(0.0F, -0.035F, -1.4F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 18).addBox(-1.0F, -1.035F, 0.0F, 2.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 30).addBox(-1.0F, -2.035F, -1.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(19, 53).addBox(-1.0F, -3.035F, -1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.6F, -9.525F, 0.0F, 0.0F, 0.0F, 0.1309F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(24, 52).addBox(-2.0F, -35.0F, -4.0F, 4.0F, 1.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(28, 0).addBox(-4.0F, -34.5F, -3.5F, 8.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(19, 51).addBox(2.0F, -35.0F, 0.0F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 61).addBox(2.5F, -34.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.001F))
                .texOffs(71, 0).addBox(-3.5F, -33.0F, -4.25F, 7.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 41).addBox(3.5F, -34.0F, -4.5F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(32, 36).addBox(-4.5F, -34.0F, -4.5F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(71, 74).addBox(2.5F, -31.0F, -1.0F, 2.0F, 2.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(43, 74).addBox(-4.5F, -31.0F, -1.0F, 2.0F, 2.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(60, 57).addBox(-4.5F, -34.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.001F))
                .texOffs(46, 0).addBox(-4.0F, -35.0F, 0.0F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(71, 2).addBox(-3.5F, -34.0F, -4.5F, 7.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 72).addBox(-4.0F, -34.0F, 4.0F, 8.0F, 6.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 26.5F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(32, 36).addBox(-4.0F, 0.5F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(75, 51).addBox(-3.0F, 0.5F, 1.75F, 6.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(28, 4).addBox(-4.0F, 0.5F, 1.5F, 8.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(32, 19).addBox(-4.0F, 0.5F, -2.5F, 8.0F, 3.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(56, 45).addBox(-3.0F, 3.5F, -2.5F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(76, 70).addBox(-3.0F, 0.5F, -2.75F, 6.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(61, 17).addBox(-3.0F, 2.5F, 1.5F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(22, 41).addBox(-2.0F, 2.5F, -2.75F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition RightTuft_r1 = Torso.addOrReplaceChild("RightTuft_r1", CubeListBuilder.create().texOffs(52, 76).addBox(10.5F, -23.5F, -2.5F, 1.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 25.5F, 0.0F, 0.0F, 0.0F, -0.6109F));

        PartDefinition LeftTuft_r1 = Torso.addOrReplaceChild("LeftTuft_r1", CubeListBuilder.create().texOffs(75, 63).addBox(-11.5F, -23.5F, -2.5F, 1.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 25.5F, 0.0F, 0.0F, 0.0F, 0.6109F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 11.5F, 0.0F));

        PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(54, 0).addBox(-4.5F, 3.0F, -2.5F, 6.0F, 4.0F, 5.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(1.5F, -23.35F, 13.2F, 0.5236F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(51, 47).addBox(-3.5F, 2.0F, -2.5F, 7.0F, 5.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -22.0F, 9.45F, 1.2217F, 0.0F, 0.0F));

        PartDefinition Base_r3 = Tail.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(0, 41).addBox(-6.5F, 3.0F, -2.5F, 8.0F, 4.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.5F, -17.6F, 7.4F, 2.0071F, 0.0F, 0.0F));

        PartDefinition Base_r4 = Tail.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(50, 19).addBox(-6.5F, 3.95F, -2.5F, 8.0F, 4.0F, 6.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(2.5F, -13.4F, 6.6F, 2.4871F, 0.0F, 0.0F));

        PartDefinition Base_r5 = Tail.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(0, 30).addBox(-7.5F, 2.0F, -2.5F, 9.0F, 4.0F, 7.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.0F, -11.95F, 9.3F, 2.9671F, 0.0F, 0.0F));

        PartDefinition Base_r6 = Tail.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(29, 7).addBox(-7.5F, 1.0F, -2.5F, 9.0F, 5.0F, 7.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(3.0F, -8.8F, 10.1F, -3.098F, 0.0F, 0.0F));

        PartDefinition Base_r7 = Tail.addOrReplaceChild("Base_r7", CubeListBuilder.create().texOffs(0, 0).addBox(-8.5F, 0.0F, -2.5F, 10.0F, 6.0F, 8.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(3.5F, -4.35F, 11.6F, -2.9671F, 0.0F, 0.0F));

        PartDefinition Base_r8 = Tail.addOrReplaceChild("Base_r8", CubeListBuilder.create().texOffs(25, 23).addBox(-7.5F, -1.0F, -2.5F, 9.0F, 6.0F, 7.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(3.0F, -0.35F, 10.45F, 2.9671F, 0.0F, 0.0F));

        PartDefinition Base_r9 = Tail.addOrReplaceChild("Base_r9", CubeListBuilder.create().texOffs(0, 51).addBox(-5.5F, 3.0F, -2.5F, 7.0F, 5.0F, 5.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(2.0F, 4.45F, 6.1F, 2.5307F, 0.0F, 0.0F));

        PartDefinition Base_r10 = Tail.addOrReplaceChild("Base_r10", CubeListBuilder.create().texOffs(61, 9).addBox(-3.5F, -1.0F, -1.5F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(1.0F, 2.6F, 6.5F, 1.789F, 0.0F, 0.0F));

        PartDefinition Base_r11 = Tail.addOrReplaceChild("Base_r11", CubeListBuilder.create().texOffs(73, 35).addBox(-2.5F, 1.0F, -1.5F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.5F, 1.4F, 2.1F, 1.309F, 0.0F, 0.0F));

        PartDefinition Base_r12 = Tail.addOrReplaceChild("Base_r12", CubeListBuilder.create().texOffs(75, 6).addBox(-2.5F, 0.0F, -1.0F, 4.0F, 4.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.5F, 0.0F, 0.0F, 0.9163F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(57, 29).addBox(-3.0F, -1.5F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(52, 36).addBox(0.0F, 10.25F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(27, 51).addBox(-3.0F, 10.25F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(19, 51).addBox(-3.0F, 10.25F, -0.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 51).addBox(-3.0F, 10.25F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 0.5F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(44, 57).addBox(-1.0F, -1.5F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 53).addBox(-1.0F, 10.25F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(44, 52).addBox(2.0F, 10.25F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(40, 52).addBox(2.0F, 10.25F, -0.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(52, 38).addBox(2.0F, 10.25F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 0.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void prepareMobModel(LatexSquirrel p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        animator.setupHand();
    }

    @Override
    public LatexAnimator<LatexSquirrel, LatexSquirrelModel> getAnimator() {
        return animator;
    }

    @Override
    public void setupAnim(LatexSquirrel entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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
}
