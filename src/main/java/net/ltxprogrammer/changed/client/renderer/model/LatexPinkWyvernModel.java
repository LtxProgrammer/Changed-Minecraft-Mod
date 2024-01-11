package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.arm.ArmBobAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.arm.ArmRideAnimator;
import net.ltxprogrammer.changed.client.renderer.animate.arm.ArmSwimAnimator;
import net.ltxprogrammer.changed.entity.beast.LatexPinkWyvern;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class LatexPinkWyvernModel extends LatexHumanoidModel<LatexPinkWyvern> implements LatexHumanoidModelInterface<LatexPinkWyvern, LatexPinkWyvernModel> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_pink_wyvern"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final LatexAnimator<LatexPinkWyvern, LatexPinkWyvernModel> animator;

    public LatexPinkWyvernModel(ModelPart root) {
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
                .addPreset(AnimatorPresets.dragonLike(
                        Head, Torso, LeftArm, RightArm,
                        Tail, List.of(tailPrimary, tailSecondary, tailTertiary),
                        LeftLeg, leftLowerLeg, leftFoot, leftFoot.getChild("LeftPad"), RightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad")));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));

        PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(48, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(46, 49).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(58, 32).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(27, 53).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));

        PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(48, 22).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(42, 59).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(24, 0).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(37, 0).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 0).addBox(-1.5F, -1.0F, -5.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(0, 2).addBox(-1.0F, -29.625F, -0.95F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.5F, 26.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.2F))
                .texOffs(24, 8).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.3F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition RightEar_r1 = RightEar.addOrReplaceChild("RightEar_r1", CubeListBuilder.create().texOffs(29, 60).addBox(4.225F, -27.2F, -25.5F, 2.0F, 8.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(64, 48).addBox(4.225F, -26.3F, -22.5F, 2.0F, 7.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.8727F, -0.2182F, -0.2618F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition LeftEar_r1 = LeftEar.addOrReplaceChild("LeftEar_r1", CubeListBuilder.create().texOffs(56, 60).addBox(-6.225F, -27.2F, -25.5F, 2.0F, 8.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(64, 24).addBox(-6.225F, -26.3F, -22.5F, 2.0F, 7.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.8727F, 0.2182F, 0.2618F));

        PartDefinition RightHorns = Head.addOrReplaceChild("RightHorns", CubeListBuilder.create(), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition Horn_r1 = RightHorns.addOrReplaceChild("Horn_r1", CubeListBuilder.create().texOffs(48, 11).addBox(-3.2F, -19.85F, 20.55F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7418F, -0.5236F, 0.0F));

        PartDefinition Horn_r2 = RightHorns.addOrReplaceChild("Horn_r2", CubeListBuilder.create().texOffs(43, 22).addBox(-3.69F, -23.75F, 17.75F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.6109F, -0.5672F, 0.0F));

        PartDefinition Horn_r3 = RightHorns.addOrReplaceChild("Horn_r3", CubeListBuilder.create().texOffs(32, 44).addBox(-3.95F, -29.3F, 11.2F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3491F, -0.6109F, 0.0F));

        PartDefinition Horn_r4 = RightHorns.addOrReplaceChild("Horn_r4", CubeListBuilder.create().texOffs(64, 0).addBox(-3.0F, -35.3F, 8.2F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2182F, -0.1745F, 0.0F));

        PartDefinition Horn_r5 = RightHorns.addOrReplaceChild("Horn_r5", CubeListBuilder.create().texOffs(60, 19).addBox(-3.0F, -31.4F, 19.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.48F, -0.1745F, 0.0F));

        PartDefinition Horn_r6 = RightHorns.addOrReplaceChild("Horn_r6", CubeListBuilder.create().texOffs(8, 64).addBox(-3.0F, -30.9F, -19.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5672F, -0.1745F, 0.0F));

        PartDefinition LeftHorns = Head.addOrReplaceChild("LeftHorns", CubeListBuilder.create(), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition Horn_r7 = LeftHorns.addOrReplaceChild("Horn_r7", CubeListBuilder.create().texOffs(32, 47).addBox(2.2F, -19.85F, 20.55F, 1.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.7418F, 0.5236F, 0.0F));

        PartDefinition Horn_r8 = LeftHorns.addOrReplaceChild("Horn_r8", CubeListBuilder.create().texOffs(44, 47).addBox(2.69F, -23.75F, 17.75F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.6109F, 0.5672F, 0.0F));

        PartDefinition Horn_r9 = LeftHorns.addOrReplaceChild("Horn_r9", CubeListBuilder.create().texOffs(40, 5).addBox(2.95F, -29.3F, 11.2F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.3491F, 0.6109F, 0.0F));

        PartDefinition Horn_r10 = LeftHorns.addOrReplaceChild("Horn_r10", CubeListBuilder.create().texOffs(64, 5).addBox(1.0F, -35.3F, 8.2F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2182F, 0.1745F, 0.0F));

        PartDefinition Horn_r11 = LeftHorns.addOrReplaceChild("Horn_r11", CubeListBuilder.create().texOffs(0, 61).addBox(1.0F, -31.4F, 19.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.48F, 0.1745F, 0.0F));

        PartDefinition Horn_r12 = LeftHorns.addOrReplaceChild("Horn_r12", CubeListBuilder.create().texOffs(16, 64).addBox(1.0F, -30.9F, -19.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5672F, 0.1745F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(28, 28).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 11.0F, 0.0F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Base_r1 = TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(56, 11).addBox(-2.0F, -2.9F, 0.4F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 3.5F));

        PartDefinition Base_r2 = TailSecondary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(46, 38).addBox(-1.5F, -1.4F, -2.7F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 1.0F, 2.5F, -0.3927F, 0.0F, 0.0F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 2.5F, 5.0F));

        PartDefinition Base_r3 = TailTertiary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(32, 44).addBox(-1.5F, -13.225F, 6.6F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.32F)), PartPose.offsetAndRotation(0.0F, 10.5F, -8.5F, -0.1309F, 0.0F, 0.0F));

        PartDefinition TailQuaternary = TailTertiary.addOrReplaceChild("TailQuaternary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, 4.5F));

        PartDefinition Base_r4 = TailQuaternary.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(32, 22).addBox(8.5F, -9.45F, 14.7F, 3.0F, 1.0F, 5.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(-10.0F, 9.5F, -13.0F, 0.0436F, 0.0F, 0.0F));

        PartDefinition Base_r5 = TailQuaternary.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(16, 34).addBox(-1.0F, -10.45F, 13.5F, 2.0F, 2.0F, 4.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 10.0F, -13.0F, 0.0436F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 1.5F, 0.0F));

        PartDefinition Wing_r1 = RightArm.addOrReplaceChild("Wing_r1", CubeListBuilder.create().texOffs(9, 56).addBox(-6.5F, -13.89F, 7.68F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(4.0F, 23.0F, 0.0F, 0.3491F, -0.2182F, 0.0F));

        PartDefinition Wing_r3 = RightArm.addOrReplaceChild("Wing_r3", CubeListBuilder.create().texOffs(58, 41).addBox(-6.5F, -12.0F, 13.0F, 1.0F, 1.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 23.0F, 0.0F, 0.6981F, -0.2182F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(16, 40).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 1.5F, 0.0F));

        PartDefinition Wing_r5 = LeftArm.addOrReplaceChild("Wing_r5", CubeListBuilder.create().texOffs(55, 52).addBox(5.5F, -13.89F, 7.68F, 1.0F, 1.0F, 7.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-4.0F, 23.0F, 0.0F, 0.3491F, 0.2182F, 0.0F));

        PartDefinition Wing_r7 = LeftArm.addOrReplaceChild("Wing_r7", CubeListBuilder.create().texOffs(18, 56).addBox(5.5F, -12.0F, 13.0F, 1.0F, 1.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 23.0F, 0.0F, 0.6981F, 0.2182F, 0.0F));

        PartDefinition Wing_r2 = RightArm.addOrReplaceChild("Wing_r2", CubeListBuilder.create().texOffs(4, 2).addBox(-6.0F, -9.1F, 17.0F, 0.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 17).addBox(-6.0F, -10.1F, 14.0F, 0.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(24, 16).addBox(-6.0F, -12.1F, 12.0F, 0.0F, 2.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 23.0F, 0.0F, 0.6545F, -0.2182F, 0.0F));

        PartDefinition Wing_r4 = RightArm.addOrReplaceChild("Wing_r4", CubeListBuilder.create().texOffs(0, 12).addBox(-6.0F, -10.75F, 17.0F, 0.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 23.0F, 0.0F, 0.9599F, -0.2182F, 0.0F));

        PartDefinition Wing_r6 = LeftArm.addOrReplaceChild("Wing_r6", CubeListBuilder.create().texOffs(3, 1).addBox(6.0F, -9.1F, 17.0F, 0.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 16).addBox(6.0F, -10.1F, 14.0F, 0.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(12, 26).addBox(6.0F, -12.1F, 12.0F, 0.0F, 2.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 23.0F, 0.0F, 0.6545F, 0.2182F, 0.0F));

        PartDefinition Wing_r8 = LeftArm.addOrReplaceChild("Wing_r8", CubeListBuilder.create().texOffs(0, 0).addBox(6.0F, -10.75F, 17.0F, 0.0F, 4.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 23.0F, 0.0F, 0.9599F, 0.2182F, 0.0F));

        return LayerDefinition.create(meshdefinition, 96, 96);
    }

    @Override
    public void prepareMobModel(LatexPinkWyvern p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LatexPinkWyvern entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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
    public LatexAnimator<LatexPinkWyvern, LatexPinkWyvernModel> getAnimator() {
        return animator;
    }
}
