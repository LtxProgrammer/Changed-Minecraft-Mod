package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.beast.DarkLatexPup;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class DarkLatexPupModel extends LatexHumanoidModel<DarkLatexPup> implements LatexHumanoidModelInterface<DarkLatexPup, DarkLatexPupModel> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("dark_latex_pup"), "main");
    private final ModelPart RightLegBack;
    private final ModelPart LeftLegBack;
    private final ModelPart RightFrontLeg;
    private final ModelPart LeftFrontLeg;
    private final ModelPart Head;
    private final ModelPart Body;
    private final ModelPart Tail;
    private final LatexAnimator<DarkLatexPup, DarkLatexPupModel> animator;

    public DarkLatexPupModel(ModelPart root) {
        super(root);
        this.Head = root.getChild("Head");
        this.Body = root.getChild("Body");
        this.Tail = Body.getChild("Tail");
        this.RightLegBack = root.getChild("RightHindLeg");
        this.LeftLegBack = root.getChild("LeftHindLeg");
        this.RightFrontLeg = root.getChild("RightFrontLeg");
        this.LeftFrontLeg = root.getChild("LeftFrontLeg");

        animator = LatexAnimator.of(this);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F, CubeDeformation.NONE)
                .texOffs(38, 10).addBox(-1.5F, 2.0F, -8.0F, 3.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 13.0F, -3.0F));

        PartDefinition Mask = Head.addOrReplaceChild("Mask", CubeListBuilder.create().texOffs(32, 20).addBox(-2.0F, -14.0F, -14.0F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 19).addBox(-1.0F, -15.0F, -14.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(22, 3).addBox(-1.0F, -12.0F, -14.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 27).addBox(-4.0F, -12.0F, -14.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(19, 17).addBox(3.0F, -9.0F, -14.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(19, 14).addBox(3.0F, -12.0F, -14.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(9, 27).addBox(-2.0F, -10.0F, -16.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(0, 30).addBox(-4.0F, -9.0F, -14.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(28, 7).addBox(-3.0F, -10.0F, -14.0F, 6.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(28, 17).addBox(-3.0F, -13.0F, -14.0F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 11.0F, 7.0F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create().texOffs(27, 39).addBox(-1.0F, -1.0F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(0, 14).addBox(-1.0F, -1.601F, -0.4F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(27, 34).addBox(-1.0F, -2.1F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(35, 10).addBox(-1.0F, -2.9F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(2.5F, -4.175F, -2.0F, 0.0F, 0.0F, 0.2618F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create().texOffs(19, 39).addBox(-1.495F, -1.1F, -10.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(0, 0).addBox(-0.495F, -1.7F, -9.4F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.04F))
                .texOffs(6, 34).addBox(-0.495F, -2.2F, -10.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
                .texOffs(35, 0).addBox(0.505F, -3.0F, -10.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-2.975F, -3.95F, 7.0F, 0.0F, 0.0F, -0.2618F));

        PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(21, 22).addBox(-3.5F, -3.5F, -3.0F, 7.0F, 7.0F, 5.0F, CubeDeformation.NONE)
                .texOffs(0, 14).addBox(-3.0F, -3.0F, 2.0F, 6.0F, 6.0F, 7.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 15.0F, 0.0F));

        PartDefinition Tail = Body.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 8.0F));

        PartDefinition cube_r1 = Tail.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(19, 14).addBox(-1.0F, -10.575F, 6.9F, 2.0F, 2.0F, 5.0F, new CubeDeformation(-0.005F)), PartPose.offsetAndRotation(0.0F, 11.0F, -4.0F, -0.1745F, 0.0F, 0.0F));

        PartDefinition cube_r2 = Tail.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 27).addBox(-1.0F, -12.25F, -1.0F, 2.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 11.0F, -4.0F, -0.48F, 0.0F, 0.0F));

        PartDefinition RightFrontLeg = partdefinition.addOrReplaceChild("RightFrontLeg", CubeListBuilder.create().texOffs(0, 34).addBox(-0.75F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(-2.0F, 18.0F, -1.0F));

        PartDefinition LeftFrontLeg = partdefinition.addOrReplaceChild("LeftFrontLeg", CubeListBuilder.create().texOffs(12, 32).addBox(-1.25F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(2.0F, 18.0F, -1.0F));

        PartDefinition RightHindLeg = partdefinition.addOrReplaceChild("RightHindLeg", CubeListBuilder.create().texOffs(37, 34).addBox(-1.25F, 5.0F, -0.725F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.001F)), PartPose.offset(-2.0F, 18.0F, 7.0F));

        PartDefinition cube_r3 = RightHindLeg.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(38, 0).addBox(-3.25F, -4.975F, 2.2F, 2.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 6.0F, -3.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition cube_r4 = RightHindLeg.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(20, 34).addBox(-3.25F, -6.0F, -0.5F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.005F)), PartPose.offsetAndRotation(2.0F, 6.0F, -3.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition cube_r5 = RightHindLeg.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(28, 10).addBox(-3.25F, -8.0F, -0.5F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(2.0F, 6.0F, -3.0F, -0.2618F, 0.0F, 0.0F));

        PartDefinition LeftHindLeg = partdefinition.addOrReplaceChild("LeftHindLeg", CubeListBuilder.create().texOffs(22, 0).addBox(-0.75F, 5.0F, -0.725F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.001F)), PartPose.offset(2.0F, 18.0F, 7.0F));

        PartDefinition cube_r6 = LeftHindLeg.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(28, 0).addBox(1.25F, -8.0F, -0.5F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-2.0F, 6.0F, -3.0F, -0.2618F, 0.0F, 0.0F));

        PartDefinition cube_r7 = LeftHindLeg.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(38, 37).addBox(1.25F, -4.975F, 2.2F, 2.0F, 4.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.0F, 6.0F, -3.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition cube_r8 = LeftHindLeg.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(30, 34).addBox(1.25F, -6.0F, -0.5F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.005F)), PartPose.offsetAndRotation(-2.0F, 6.0F, -3.0F, -0.5236F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void prepareMobModel(DarkLatexPup entity, float p_102665_, float p_102666_, float partialTicks) {
        this.prepareMobModel(animator, entity, p_102665_, p_102666_, partialTicks);
        
        this.Body.xRot = 0.0F;
        this.Tail.xRot = -0.05235988F;
        this.RightLegBack.xRot = Mth.cos(p_102665_ * 0.6662F) * 1.4F * p_102666_;
        this.LeftLegBack.xRot = Mth.cos(p_102665_ * 0.6662F + (float)Math.PI) * 1.4F * p_102666_;
        this.RightFrontLeg.xRot = Mth.cos(p_102665_ * 0.6662F + (float)Math.PI) * 1.4F * p_102666_;
        this.LeftFrontLeg.xRot = Mth.cos(p_102665_ * 0.6662F) * 1.4F * p_102666_;
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

    public void setupHand() {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull DarkLatexPup entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float HeadPitch) {
        if (!entity.isSleeping() && !entity.isCrouching()) {
            this.Head.xRot = HeadPitch * ((float)Math.PI / 180F);
            this.Head.yRot = netHeadYaw * ((float)Math.PI / 180F);
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
    }

    public ModelPart getArm(HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? this.LeftFrontLeg : this.RightFrontLeg;
    }

    public ModelPart getHead() {
        return this.Head;
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

    @Override
    public LatexAnimator<DarkLatexPup, DarkLatexPupModel> getAnimator() {
        return animator;
    }
}
