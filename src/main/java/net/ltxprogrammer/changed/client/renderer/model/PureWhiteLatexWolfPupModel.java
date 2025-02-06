package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.animations.Limb;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.HumanoidAnimator;
import net.ltxprogrammer.changed.client.tfanimations.HelperModel;
import net.ltxprogrammer.changed.client.tfanimations.TransfurHelper;
import net.ltxprogrammer.changed.entity.beast.DarkLatexWolfPup;
import net.ltxprogrammer.changed.entity.beast.PureWhiteLatexWolfPup;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PureWhiteLatexWolfPupModel extends AdvancedHumanoidModel<PureWhiteLatexWolfPup> implements AdvancedHumanoidModelInterface<PureWhiteLatexWolfPup, PureWhiteLatexWolfPupModel> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("pure_white_latex_wolf_pup"), "main");
    private final ModelPart RightLegBack;
    private final ModelPart LeftLegBack;
    private final ModelPart RightFrontLeg;
    private final ModelPart LeftFrontLeg;
    private final ModelPart Head;
    private final ModelPart Body;
    private final ModelPart Tail;
    private final HumanoidAnimator<PureWhiteLatexWolfPup, PureWhiteLatexWolfPupModel> animator;

    public PureWhiteLatexWolfPupModel(ModelPart root) {
        super(root);
        this.Head = root.getChild("Head");
        this.Body = root.getChild("Torso");
        this.Tail = Body.getChild("Tail");
        this.RightLegBack = root.getChild("RightLeg");
        this.LeftLegBack = root.getChild("LeftLeg");
        this.RightFrontLeg = root.getChild("RightArm");
        this.LeftFrontLeg = root.getChild("LeftArm");

        var tailPrimary = Tail.getChild("TailPrimary");
        var tailSecondary = tailPrimary.getChild("TailSecondary");
        var tailTertiary = tailSecondary.getChild("TailTertiary");

        animator = HumanoidAnimator.of(this)
                .addPreset(AnimatorPresets.wolfTail(Tail, List.of(tailPrimary, tailSecondary, tailTertiary)));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.0F, 18.0F, 7.0F));

        PartDefinition RightThigh_r1 = RightLeg.addOrReplaceChild("RightThigh_r1", CubeListBuilder.create().texOffs(32, 33).addBox(0.0F, 2.075F, -2.1F, 2.0F, 4.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.5F, -4.5F, 0.5F, -0.2182F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg = RightLeg.addOrReplaceChild("RightLowerLeg", CubeListBuilder.create(), PartPose.offset(-0.5F, 0.875F, -2.95F));

        PartDefinition RightCalf_r1 = RightLowerLeg.addOrReplaceChild("RightCalf_r1", CubeListBuilder.create().texOffs(24, 40).addBox(-0.99F, -0.625F, -2.15F, 2.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -1.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition RightFoot = RightLowerLeg.addOrReplaceChild("RightFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.3F, 4.675F));

        PartDefinition RightArch_r1 = RightFoot.addOrReplaceChild("RightArch_r1", CubeListBuilder.create().texOffs(32, 40).addBox(0.0F, -5.45F, -0.725F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(-1.0F, 5.575F, -3.225F, -0.3491F, 0.0F, 0.0F));

        PartDefinition RightPad = RightFoot.addOrReplaceChild("RightPad", CubeListBuilder.create().texOffs(42, 0).addBox(-1.0F, 1.0F, -0.85F, 2.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 2.825F, -2.675F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.0F, 18.0F, 7.0F));

        PartDefinition LeftThigh_r1 = LeftLeg.addOrReplaceChild("LeftThigh_r1", CubeListBuilder.create().texOffs(22, 33).addBox(-2.0F, 2.075F, -2.1F, 2.0F, 4.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.5F, -4.5F, 0.5F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg = LeftLeg.addOrReplaceChild("LeftLowerLeg", CubeListBuilder.create(), PartPose.offset(0.5F, 0.875F, -2.95F));

        PartDefinition LeftCalf_r1 = LeftLowerLeg.addOrReplaceChild("LeftCalf_r1", CubeListBuilder.create().texOffs(8, 38).addBox(-2.01F, -0.625F, -2.15F, 2.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, -1.125F, 1.95F, 0.8727F, 0.0F, 0.0F));

        PartDefinition LeftFoot = LeftLowerLeg.addOrReplaceChild("LeftFoot", CubeListBuilder.create(), PartPose.offset(0.0F, 0.3F, 4.675F));

        PartDefinition LeftArch_r1 = LeftFoot.addOrReplaceChild("LeftArch_r1", CubeListBuilder.create().texOffs(16, 40).addBox(-2.0F, -5.45F, -0.725F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.005F)), PartPose.offsetAndRotation(1.0F, 5.575F, -3.225F, -0.3491F, 0.0F, 0.0F));

        PartDefinition LeftPad = LeftFoot.addOrReplaceChild("LeftPad", CubeListBuilder.create().texOffs(40, 40).addBox(-1.0F, 1.0F, -0.85F, 2.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 2.825F, -2.675F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -7.0F, 8.0F, 8.0F, 7.0F, CubeDeformation.NONE)
                .texOffs(30, 6).addBox(-2.0F, 1.0F, -9.0F, 4.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 12.5F, -4.0F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offset(-3.0F, -3.5F, -2.0F));

        PartDefinition RightEarPivot = RightEar.addOrReplaceChild("RightEarPivot", CubeListBuilder.create().texOffs(38, 11).addBox(-1.9F, -1.2F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(42, 3).addBox(-0.9F, -1.6F, -0.4F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(16, 38).addBox(-0.9F, -2.3F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(42, 17).addBox(0.1F, -3.1F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.5F, -1.25F, 0.0F, -0.1309F, 0.5236F, -0.3491F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offset(3.0F, -3.5F, -2.0F));

        PartDefinition LeftEarPivot = LeftEar.addOrReplaceChild("LeftEarPivot", CubeListBuilder.create().texOffs(30, 11).addBox(-1.1F, -1.2F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(42, 7).addBox(-1.1F, -1.6F, -0.4F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(42, 15).addBox(-1.1F, -2.3F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(42, 19).addBox(-1.1F, -3.1F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-0.5F, -1.25F, 0.0F, -0.1309F, -0.5236F, 0.3491F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, 14.5F, 0.0F));

        PartDefinition Torso_r1 = Torso.addOrReplaceChild("Torso_r1", CubeListBuilder.create().texOffs(0, 26).addBox(-3.0F, 3.0F, -2.0F, 6.0F, 7.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(0, 15).addBox(-3.5F, -2.0F, -2.5F, 7.0F, 5.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, -2.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(-0.5F, 0.0F, 6.6F));

        PartDefinition TailPrimary = Tail.addOrReplaceChild("TailPrimary", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.1309F, 0.0F, 0.0F));

        PartDefinition Base_r1 = TailPrimary.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(22, 26).addBox(-1.0F, 1.8F, -0.5F, 3.0F, 4.0F, 3.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.0F, 1.2654F, 0.0F, 0.0F));

        PartDefinition TailSecondary = TailPrimary.addOrReplaceChild("TailSecondary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.9F, 2.7F));

        PartDefinition Base_r2 = TailSecondary.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(26, 15).addBox(-1.5F, -2.0F, -5.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(-0.15F)), PartPose.offsetAndRotation(0.0F, -3.1F, 2.2F, 1.4835F, 0.0F, 0.0F));

        PartDefinition TailTertiary = TailSecondary.addOrReplaceChild("TailTertiary", CubeListBuilder.create(), PartPose.offset(0.0F, 0.5F, 4.7F));

        PartDefinition Base_r3 = TailTertiary.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(30, 0).addBox(-1.0F, 0.9611F, -10.5171F, 3.0F, 3.0F, 3.0F, new CubeDeformation(-0.15F)), PartPose.offsetAndRotation(0.0F, -8.5611F, -3.2829F, 1.8326F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(34, 25).addBox(-1.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(-2.0F, 17.5F, -2.5F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 38).addBox(-1.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(2.0F, 17.5F, -2.5F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void prepareMobModel(PureWhiteLatexWolfPup entity, float limbSwing, float limbSwingAmount, float partialTicks) {
        this.prepareMobModel(animator, entity, limbSwing, limbSwingAmount, partialTicks);
        
        this.Body.xRot = 0.0F;
        this.Tail.xRot = -0.05235988F;
        this.RightLegBack.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.RightLegBack.yRot = 0f;
        this.RightLegBack.zRot = 0f;
        this.LeftLegBack.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.LeftLegBack.yRot = 0f;
        this.LeftLegBack.zRot = 0f;
        this.RightFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
        this.RightFrontLeg.yRot = 0f;
        this.RightFrontLeg.zRot = 0f;
        this.LeftFrontLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.LeftFrontLeg.yRot = 0f;
        this.LeftFrontLeg.zRot = 0f;
        this.Head.setPos(0.0F, 13.0F, -3.0F);
        this.Head.yRot = 0.0F;
        this.RightLegBack.visible = true;
        this.LeftLegBack.visible = true;
        this.RightFrontLeg.visible = true;
        this.LeftFrontLeg.visible = true;
        this.Body.setPos(0.0F, 15.0F, 0.0F);
        this.Body.zRot = 0.0F;
        this.RightLegBack.setPos(-2.0F, 18.0F, 7.0F);
        this.LeftLegBack.setPos(2.0F, 18.0F, 7.0F);
        if (entity.isCrouching()) {
            //this.Body.xRot = 3.246312F;
            float f = entity.getCrouchAmount(partialTicks);
            this.Body.setPos(0.0F, 15.0F + entity.getCrouchAmount(partialTicks), 0.0F);
            this.Head.setPos(0.0F, 13.0F + f, -3.0F);
        } else if (entity.isSleeping()) {
            this.Body.zRot = (-(float)Math.PI / 2F);
            this.Body.setPos(0.0F, 20.0F, 0.0F);
            this.Tail.xRot = -2.6179938F;
            if (this.young) {
                this.Tail.xRot = -2.1816616F;
                this.Body.setPos(0.0F, 20.0F, 4.0F);
            }

            this.Head.setPos(2.0F, 15.99F, -3.0F);
            this.Head.xRot = 0.0F;
            this.Head.yRot = -2.0943952F;
            this.Head.zRot = 0.0F;
            this.RightLegBack.visible = false;
            this.LeftLegBack.visible = false;
            this.RightFrontLeg.visible = false;
            this.LeftFrontLeg.visible = false;
        } /*else if (entity.isSitting()) {
            this.Body.xRot = ((float)Math.PI / 6F);
            this.Body.setPos(0.0F, 8.0F, 3.0F);
            this.Tail.xRot = ((float)Math.PI / 4F);
            this.Tail.setPos(-4.0F, 15.0F, -2.0F);
            this.Head.setPos(0.0F, 6.5F, -0.25F);
            this.Head.xRot = 0.0F;
            this.Head.yRot = 0.0F;
            if (this.young) {
                this.Head.setPos(-1.0F, 13.0F, -3.75F);
            }

            this.RightLegBack.xRot = -1.3089969F;
            this.RightLegBack.setPos(-5.0F, 21.5F, 6.75F);
            this.LeftLegBack.xRot = -1.3089969F;
            this.LeftLegBack.setPos(-1.0F, 21.5F, 6.75F);
            this.RightFrontLeg.xRot = -0.2617994F;
            this.LeftFrontLeg.xRot = -0.2617994F;
        }*/
    }

    public void setupHand(PureWhiteLatexWolfPup entity) {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull PureWhiteLatexWolfPup entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!entity.isSleeping()) {
            this.Head.xRot = headPitch * ((float)Math.PI / 180F);
            this.Head.yRot = netHeadYaw * ((float)Math.PI / 180F);
            this.Head.zRot = 0.0F;
        }

        if (entity.isSleeping()) {
            this.Head.xRot = 0.0F;
            this.Head.yRot = -2.0943952F;
            this.Head.zRot = Mth.cos(ageInTicks * 0.027F) / 22.0F;
        }

        if (entity.isCrouching()) {
            float f = Mth.cos(ageInTicks) * 0.01F;
            this.Body.yRot = f;
            this.RightLegBack.zRot = f;
            this.LeftLegBack.zRot = f;
            this.RightFrontLeg.zRot = f / 2.0F;
            this.LeftFrontLeg.zRot = f / 2.0F;
        }

        animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public ModelPart getArm(HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? this.LeftFrontLeg : this.RightFrontLeg;
    }

    public ModelPart getLeg(HumanoidArm p_102852_) {
        return p_102852_ == HumanoidArm.LEFT ? this.LeftLegBack : this.RightLegBack;
    }

    public ModelPart getHead() {
        return this.Head;
    }

    public ModelPart getTorso() {
        return Body;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        RightLegBack.render(poseStack, buffer, packedLight, packedOverlay);
        LeftLegBack.render(poseStack, buffer, packedLight, packedOverlay);
        Head.render(poseStack, buffer, packedLight, packedOverlay);
        Body.render(poseStack, buffer, packedLight, packedOverlay);
        RightFrontLeg.render(poseStack, buffer, packedLight, packedOverlay);
        LeftFrontLeg.render(poseStack, buffer, packedLight, packedOverlay);
    }

    @Nullable
    @Override
    public HelperModel getTransfurHelperModel(Limb limb) {
        if (limb == Limb.TORSO)
            return TransfurHelper.getPupTorso();
        return super.getTransfurHelperModel(limb);
    }

    @Override
    public HumanoidAnimator<PureWhiteLatexWolfPup, PureWhiteLatexWolfPupModel> getAnimator(PureWhiteLatexWolfPup entity) {
        return animator;
    }
}
