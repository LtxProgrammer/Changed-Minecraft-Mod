package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.entity.beast.LatexKobold;
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
public class LatexKoboldModel extends AdvancedHumanoidModel<LatexKobold> implements AdvancedHumanoidModelInterface<LatexKobold, LatexKoboldModel> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_kobold"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final HumanoidAnimator<LatexKobold, LatexKoboldModel> animator;

    public LatexKoboldModel(ModelPart root) {
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
                .addPreset(AnimatorPresets.dragonLike(
                        Head, Torso, LeftArm, RightArm,
                        Tail, List.of(tailPrimary, tailSecondary, tailTertiary),
                        LeftLeg, leftLowerLeg, leftFoot, leftFoot.getChild("LeftPad"), RightLeg, rightLowerLeg, rightFoot, rightFoot.getChild("RightPad")));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.5F, 10.5F, 0.0F));

        PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(34, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(40, 20).addBox(-1.99F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(50, 30).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(32, 43).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.5F, 10.5F, 0.0F));

        PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(40, 9).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.0F, 6.375F, -3.45F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(16, 41).addBox(-2.01F, -0.125F, -2.9F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -2.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.8F, 7.175F));

        PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(32, 50).addBox(-2.0F, -8.45F, -0.725F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(0.0F, 7.075F, -4.975F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(50, 0).addBox(-2.0F, 0.0F, -2.5F, 4.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 4.325F, -4.425F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
                .texOffs(50, 46).addBox(-2.0F, -3.0F, -6.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(40, 30).addBox(-1.5F, -1.0F, -5.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition CenterHorns = Head.addOrReplaceChild("CenterHorns", CubeListBuilder.create(), PartPose.offset(0.0F, -2.5F, 0.0F));

        PartDefinition Horn_r1 = CenterHorns.addOrReplaceChild("Horn_r1", CubeListBuilder.create().texOffs(62, 46).addBox(-1.0F, -5.4F, -1.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(-0.03F)), PartPose.offsetAndRotation(0.0F, 0.9F, -0.7F, -1.2217F, 0.0F, 0.0F));

        PartDefinition Horn_r2 = CenterHorns.addOrReplaceChild("Horn_r2", CubeListBuilder.create().texOffs(42, 62).addBox(-1.0F, -6.4F, 1.8F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.02F)), PartPose.offsetAndRotation(0.0F, -3.2F, -2.6F, -1.0472F, 0.0F, 0.0F));

        PartDefinition Horn_r3 = CenterHorns.addOrReplaceChild("Horn_r3", CubeListBuilder.create().texOffs(0, 1).addBox(-1.0F, -5.2F, 1.9F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.11F)), PartPose.offsetAndRotation(0.0F, 0.1F, 1.5F, 0.2618F, 0.0F, 0.0F));

        PartDefinition Horn_r4 = CenterHorns.addOrReplaceChild("Horn_r4", CubeListBuilder.create().texOffs(56, 13).addBox(-1.0F, -5.2F, 1.9F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.07F)), PartPose.offsetAndRotation(0.0F, 0.2F, -1.5F, 0.4363F, 0.0F, 0.0F));

        PartDefinition Horn_r5 = CenterHorns.addOrReplaceChild("Horn_r5", CubeListBuilder.create().texOffs(56, 18).addBox(-1.0F, -6.1F, 1.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(-0.02F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.1F, 0.6545F, 0.0F, 0.0F));

        PartDefinition RightHorn = Head.addOrReplaceChild("RightHorn", CubeListBuilder.create(), PartPose.offsetAndRotation(-0.4F, 25.1F, -5.3F, -0.1309F, -0.0436F, 0.0F));

        PartDefinition Horn_r6 = RightHorn.addOrReplaceChild("Horn_r6", CubeListBuilder.create().texOffs(56, 23).addBox(-3.0F, -31.3F, 18.9F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.48F, -0.1745F, 0.0F));

        PartDefinition Horn_r7 = RightHorn.addOrReplaceChild("Horn_r7", CubeListBuilder.create().texOffs(56, 57).addBox(-3.0F, -35.2F, 8.2F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.15F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2182F, -0.1745F, 0.0F));

        PartDefinition Horn_r8 = RightHorn.addOrReplaceChild("Horn_r8", CubeListBuilder.create().texOffs(32, 9).addBox(-3.0F, -30.75F, -19.0F, 2.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5672F, -0.1745F, 0.0F));

        PartDefinition LeftHorn = Head.addOrReplaceChild("LeftHorn", CubeListBuilder.create(), PartPose.offsetAndRotation(0.4F, 25.1F, -5.3F, -0.1309F, 0.0436F, 0.0F));

        PartDefinition Horn_r9 = LeftHorn.addOrReplaceChild("Horn_r9", CubeListBuilder.create().texOffs(46, 57).addBox(1.0F, -31.3F, 18.9F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.48F, 0.1745F, 0.0F));

        PartDefinition Horn_r10 = LeftHorn.addOrReplaceChild("Horn_r10", CubeListBuilder.create().texOffs(12, 58).addBox(1.0F, -35.2F, 8.2F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.15F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.2182F, 0.1745F, 0.0F));

        PartDefinition Horn_r11 = LeftHorn.addOrReplaceChild("Horn_r11", CubeListBuilder.create().texOffs(60, 50).addBox(1.0F, -30.75F, -19.0F, 2.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5672F, 0.1745F, 0.0F));

        PartDefinition RightSideHorns = Head.addOrReplaceChild("RightSideHorns", CubeListBuilder.create(), PartPose.offset(0.1F, 25.0F, 0.0F));

        PartDefinition SideHorn_r1 = RightSideHorns.addOrReplaceChild("SideHorn_r1", CubeListBuilder.create().texOffs(32, 59).addBox(0.0F, -0.2F, -0.7F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.16F)), PartPose.offsetAndRotation(-3.8F, -28.275F, -0.3F, -0.6545F, -0.1309F, 0.6545F));

        PartDefinition SideHorn_r2 = RightSideHorns.addOrReplaceChild("SideHorn_r2", CubeListBuilder.create().texOffs(0, 56).addBox(-1.6F, -3.4F, 0.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.04F)), PartPose.offsetAndRotation(-2.7F, -28.0F, -0.7F, 0.1745F, -0.6109F, 0.2618F));

        PartDefinition SideHorn_r3 = RightSideHorns.addOrReplaceChild("SideHorn_r3", CubeListBuilder.create().texOffs(50, 39).addBox(-1.0F, -2.0F, -1.1F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.12F)), PartPose.offsetAndRotation(-3.9F, -27.8F, -0.2F, -0.1745F, -0.6109F, 0.5672F));

        PartDefinition LeftSideHorns = Head.addOrReplaceChild("LeftSideHorns", CubeListBuilder.create(), PartPose.offset(-0.1F, 25.0F, 0.0F));

        PartDefinition SideHorn_r4 = LeftSideHorns.addOrReplaceChild("SideHorn_r4", CubeListBuilder.create().texOffs(46, 50).addBox(-1.0F, -2.0F, -1.1F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.12F)), PartPose.offsetAndRotation(3.9F, -27.8F, -0.2F, -0.1745F, 0.6109F, -0.5672F));

        PartDefinition SideHorn_r5 = LeftSideHorns.addOrReplaceChild("SideHorn_r5", CubeListBuilder.create().texOffs(16, 51).addBox(0.6F, -3.4F, 0.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.04F)), PartPose.offsetAndRotation(2.7F, -28.0F, -0.7F, 0.1745F, 0.6109F, -0.2618F));

        PartDefinition SideHorn_r6 = LeftSideHorns.addOrReplaceChild("SideHorn_r6", CubeListBuilder.create().texOffs(22, 58).addBox(-1.0F, -0.2F, -0.7F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.16F)), PartPose.offsetAndRotation(3.8F, -28.275F, -0.3F, -0.6545F, 0.1309F, -0.6545F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -0.5F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 11.0F, 0.0F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Base_r1 = TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, -2.9F, 0.4F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 1.0F, 3.5F));

        PartDefinition Base_r2 = TailSecondary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(32, 0).addBox(-1.5F, -1.4F, -2.7F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 1.0F, 2.5F, -0.3927F, 0.0F, 0.0F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 2.5F, 5.0F));

        PartDefinition Base_r3 = TailTertiary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(16, 32).addBox(-1.5F, -13.225F, 6.6F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.32F)), PartPose.offsetAndRotation(0.0F, 10.5F, -8.5F, -0.1309F, 0.0F, 0.0F));

        PartDefinition TailQuaternary = TailTertiary.addOrReplaceChild("TailQuaternary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, 4.5F));

        PartDefinition Base_r4 = TailQuaternary.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(56, 7).addBox(-1.0F, -10.45F, 13.5F, 2.0F, 2.0F, 4.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 10.0F, -13.0F, 0.0436F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(0, 32).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 1.5F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(24, 16).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 1.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 96, 96);
    }

    @Override
    public void prepareMobModel(LatexKobold p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand(LatexKobold entity) {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LatexKobold entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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
    public HumanoidAnimator<LatexKobold, LatexKoboldModel> getAnimator(LatexKobold entity) {
        return animator;
    }
}