package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.beast.LatexCrocodile;
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
public class LatexCrocodileModel extends AdvancedHumanoidModel<LatexCrocodile> implements AdvancedHumanoidModelInterface<LatexCrocodile, LatexCrocodileModel> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_crocodile"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final HumanoidAnimator<LatexCrocodile, LatexCrocodileModel> animator;

    public LatexCrocodileModel(ModelPart root) {
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

        animator = HumanoidAnimator.of(this).hipOffset(-1.5f)
                .addPreset(AnimatorPresets.sharkLike(
                        Head, Torso, LeftArm, RightArm,
                        Tail, List.of(tailPrimary, tailSecondary, tailTertiary),
                        LeftLeg, leftLowerLeg, leftFoot, leftFoot.getChild("LeftPad"), RightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad")));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));

        PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(32, 7).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(44, 24).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(28, 50).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(44, 2).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));

        PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(16, 30).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(44, 14).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(44, 42).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(43, 35).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Base_r1 = Head.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(40, 22).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.5715F, -2.573F, 2.5307F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Head.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(40, 22).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.9715F, -1.073F, 2.5307F, 0.0F, 0.0F));

        PartDefinition Base_r3 = Head.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(40, 22).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -8.2715F, 0.627F, 2.5307F, 0.0F, 0.0F));

        PartDefinition Base_r4 = Head.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(40, 22).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.7215F, 2.377F, 2.5307F, 0.0F, 0.0F));

        PartDefinition Base_r5 = Head.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(40, 22).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -6.8215F, 3.227F, 0.9599F, 0.0F, 0.0F));

        PartDefinition Base_r6 = Head.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(40, 22).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -5.2215F, 3.527F, 0.9599F, 0.0F, 0.0F));

        PartDefinition Base_r7 = Head.addOrReplaceChild("Base_r7", CubeListBuilder.create().texOffs(40, 22).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -3.2215F, 3.527F, 0.9599F, 0.0F, 0.0F));

        PartDefinition Base_r8 = Head.addOrReplaceChild("Base_r8", CubeListBuilder.create().texOffs(40, 22).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.4715F, 3.127F, 0.9599F, 0.0F, 0.0F));

        PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(50, 53).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, -3.01F, -7.0F, 0.0F, -0.2182F, 0.0F));

        PartDefinition Snout_r2 = Head.addOrReplaceChild("Snout_r2", CubeListBuilder.create().texOffs(8, 54).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, -3.01F, -7.0F, 0.0F, 0.2182F, 0.0F));

        PartDefinition Snout_r3 = Head.addOrReplaceChild("Snout_r3", CubeListBuilder.create().texOffs(30, 18).addBox(-1.5F, -27.0F, -6.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(48, 9).addBox(-2.0F, -29.0F, -7.0F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 26.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Snout_r4 = Head.addOrReplaceChild("Snout_r4", CubeListBuilder.create().texOffs(14, 54).addBox(-1.0F, -0.5F, -0.5F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -2.2F, -5.5F, 0.48F, 0.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(20, 22).addBox(-4.0F, 5.5F, -2.0F, 8.0F, 4.0F, 4.0F, new CubeDeformation(-0.2F))
                .texOffs(24, 0).addBox(-4.0F, 9.0F, -2.0F, 8.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Base_r9 = Torso.addOrReplaceChild("Base_r9", CubeListBuilder.create().texOffs(0, 56).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.7285F, -1.323F, 0.9599F, 0.0F, 0.0F));

        PartDefinition Base_r10 = Torso.addOrReplaceChild("Base_r10", CubeListBuilder.create().texOffs(0, 56).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.9785F, -1.573F, 0.9599F, 0.0F, 0.0F));

        PartDefinition Base_r11 = Torso.addOrReplaceChild("Base_r11", CubeListBuilder.create().texOffs(0, 56).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.9785F, -1.323F, 0.9599F, 0.0F, 0.0F));

        PartDefinition Base_r12 = Torso.addOrReplaceChild("Base_r12", CubeListBuilder.create().texOffs(40, 22).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.7785F, 1.527F, 0.9599F, 0.0F, 0.0F));

        PartDefinition Base_r13 = Torso.addOrReplaceChild("Base_r13", CubeListBuilder.create().texOffs(40, 22).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.7785F, 1.777F, 0.9599F, 0.0F, 0.0F));

        PartDefinition Base_r14 = Torso.addOrReplaceChild("Base_r14", CubeListBuilder.create().texOffs(40, 22).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.7785F, 1.777F, 0.9599F, 0.0F, 0.0F));

        PartDefinition Base_r15 = Torso.addOrReplaceChild("Base_r15", CubeListBuilder.create().texOffs(40, 22).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 7.0285F, 1.677F, 0.9599F, 0.0F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 10.5F, 0.0F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offset(0.0F, -0.5F, 0.75F));

        PartDefinition Base_r16 = TailPrimary.addOrReplaceChild("Base_r16", CubeListBuilder.create().texOffs(40, 22).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.9715F, 1.927F, 2.138F, 0.0F, 0.0F));

        PartDefinition Base_r17 = TailPrimary.addOrReplaceChild("Base_r17", CubeListBuilder.create().texOffs(40, 22).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0285F, 4.427F, 2.138F, 0.0F, 0.0F));

        PartDefinition Base_r18 = TailPrimary.addOrReplaceChild("Base_r18", CubeListBuilder.create().texOffs(40, 22).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0285F, 6.677F, 2.138F, 0.0F, 0.0F));

        PartDefinition Base_r19 = TailPrimary.addOrReplaceChild("Base_r19", CubeListBuilder.create().texOffs(20, 16).addBox(-2.0F, -1.075F, 0.375F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.875F, 0.85F, 1.9199F, 0.0F, 0.0F));

        PartDefinition Base_r20 = TailPrimary.addOrReplaceChild("Base_r20", CubeListBuilder.create().texOffs(0, 26).addBox(-2.0F, 0.75F, -0.8F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, -1.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 3.25F, 7.25F));

        PartDefinition Base_r21 = TailSecondary.addOrReplaceChild("Base_r21", CubeListBuilder.create().texOffs(40, 22).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.9715F, 1.427F, 2.138F, 0.0F, 0.0F));

        PartDefinition Base_r22 = TailSecondary.addOrReplaceChild("Base_r22", CubeListBuilder.create().texOffs(40, 22).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0285F, 3.677F, 2.138F, 0.0F, 0.0F));

        PartDefinition Base_r23 = TailSecondary.addOrReplaceChild("Base_r23", CubeListBuilder.create().texOffs(0, 48).addBox(-1.5F, -1.3563F, -0.6088F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.25F, 1.0F, 1.309F, 0.0F, 0.0F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.5F, 4.5F));

        PartDefinition Base_r24 = TailTertiary.addOrReplaceChild("Base_r24", CubeListBuilder.create().texOffs(40, 22).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.4715F, 1.427F, 2.138F, 0.0F, 0.0F));

        PartDefinition Base_r25 = TailTertiary.addOrReplaceChild("Base_r25", CubeListBuilder.create().texOffs(40, 22).addBox(-0.5F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -0.2215F, 3.427F, 2.138F, 0.0F, 0.0F));

        PartDefinition Base_r26 = TailTertiary.addOrReplaceChild("Base_r26", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.3449F, -0.7203F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.25F, 0.25F, 1.4835F, 0.0F, 0.0F));

        PartDefinition Muscles = Torso.addOrReplaceChild("Muscles", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = Muscles.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(42, 51).mirror().addBox(0.3F, -1.0F, 0.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.1F)).mirror(false), PartPose.offsetAndRotation(-0.15F, 1.25F, -2.25F, -0.0436F, -0.0087F, 0.0004F));

        PartDefinition cube_r2 = Muscles.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(42, 51).addBox(-4.0F, -1.0F, 0.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-0.15F, 1.25F, -2.25F, -0.0436F, 0.0087F, -0.0004F));

        PartDefinition Abs = Muscles.addOrReplaceChild("Abs", CubeListBuilder.create(), PartPose.offset(0.0F, 4.3F, 0.2F));

        PartDefinition cube_r3 = Abs.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(28, 30).addBox(-2.25F, 1.0F, 0.2F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.175F)), PartPose.offsetAndRotation(-0.05F, 2.45F, -2.225F, 0.0F, 0.0087F, 0.0F));

        PartDefinition cube_r4 = Abs.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(12, 26).addBox(0.25F, 1.0F, 0.2F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.175F)), PartPose.offsetAndRotation(0.05F, 2.45F, -2.225F, 0.0F, -0.0087F, 0.0F));

        PartDefinition cube_r5 = Abs.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(16, 41).addBox(-3.25F, -0.5F, 0.2F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(-0.05F, 2.45F, -2.2F, 0.0F, 0.0087F, 0.0F));

        PartDefinition cube_r6 = Abs.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(16, 41).mirror().addBox(0.25F, -0.5F, 0.2F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.2F)).mirror(false), PartPose.offsetAndRotation(0.05F, 2.45F, -2.2F, 0.0F, -0.0087F, 0.0F));

        PartDefinition cube_r7 = Abs.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(16, 41).addBox(-3.25F, -2.0F, 0.2F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.225F)), PartPose.offsetAndRotation(-0.05F, 2.35F, -2.45F, 0.0F, 0.0087F, 0.0F));

        PartDefinition cube_r8 = Abs.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(16, 41).mirror().addBox(0.25F, -2.0F, 0.2F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.225F)).mirror(false), PartPose.offsetAndRotation(0.05F, 2.35F, -2.45F, 0.0F, -0.0087F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(28, 40).addBox(-3.0F, 4.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.1F))
                .texOffs(0, 38).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 1.5F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(12, 44).addBox(-1.0F, 4.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.1F))
                .texOffs(32, 30).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 1.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }


    @Override
    public void prepareMobModel(LatexCrocodile p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LatexCrocodile entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

    }

    public ModelPart getArm(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
    }

    public ModelPart getLeg(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.LeftLeg : this.RightLeg;
    }

    public ModelPart getHead() {
        return this.Head;
    }

    public ModelPart getTorso() {
        return Torso;
    }

    private double yOffset = -4.6 / 16.0;
    private float bodyScale = 1.2F;

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        poseStack.pushPose();
        this.scaleForBody(poseStack);
        RightLeg.render(poseStack, buffer, packedLight, packedOverlay);
        LeftLeg.render(poseStack, buffer, packedLight, packedOverlay);
        Torso.render(poseStack, buffer, packedLight, packedOverlay);
        RightArm.render(poseStack, buffer, packedLight, packedOverlay);
        LeftArm.render(poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();

        poseStack.pushPose();
        this.scaleForHead(poseStack);
        Head.render(poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    public void scaleForBody(PoseStack poseStack) {
        poseStack.translate(0.0, yOffset, 0.0);
        poseStack.scale(bodyScale, bodyScale, bodyScale);
    }

    @Override
    public void scaleForHead(PoseStack poseStack) {
        poseStack.translate(0.0, yOffset, 0.0);
    }

    @Override
    public HumanoidAnimator<LatexCrocodile, LatexCrocodileModel> getAnimator() {
        return animator;
    }
}
