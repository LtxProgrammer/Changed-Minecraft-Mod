package net.ltxprogrammer.changed.client.renderer.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.beast.LatexBeifeng;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class LatexBeifengModel extends LatexHumanoidModel<LatexBeifeng> implements LatexHumanoidModelInterface {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_beifeng"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final LatexHumanoidModelController controller;

    public LatexBeifengModel(ModelPart root) {
        super(root);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Tail = Torso.getChild("Tail");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        controller = LatexHumanoidModelController.Builder.of(this, Head, Torso, Tail, RightArm, LeftArm, RightLeg, LeftLeg).build();
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(44, 30).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, NO_DEFORMATION), PartPose.offset(-2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F))
                .texOffs(0, 16).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F))
                .texOffs(0, 16).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(0, 32).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(0, 32).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(29, 42).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(16, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, NO_DEFORMATION), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(40, 11).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(43, 42).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, NO_DEFORMATION), PartPose.offset(2.5F, 10.0F, 0.0F));

        PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(0, 18).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F))
                .texOffs(0, 18).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F))
                .texOffs(0, 18).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

        PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(0, 34).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(0, 34).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(0, 34).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(40, 21).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, NO_DEFORMATION), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(32, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -34.0F + 26.5F, -4.0F, 8.0F, 8.0F, 8.0F, NO_DEFORMATION)
                .texOffs(24, 0).addBox(-2.0F, -29.0F + 26.5F, -6.0F, 4.0F, 2.0F, 2.0F, NO_DEFORMATION)
                .texOffs(20, 16).addBox(-1.5F, -27.0F + 26.5F, -5.0F, 3.0F, 1.0F, 1.0F, NO_DEFORMATION), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(51, 15).addBox(-2.0F, -35.0F + 26.5F, -4.0F, 4.0F, 1.0F, 8.0F, NO_DEFORMATION)
                .texOffs(64, 26).addBox(-4.0F, -34.5F + 26.5F, -4.0F, 8.0F, 1.0F, 4.0F, new CubeDeformation(0.001F))
                .texOffs(57, 10).addBox(2.0F, -35.0F + 26.5F, 0.0F, 2.0F, 1.0F, 4.0F, NO_DEFORMATION)
                .texOffs(75, 0).addBox(2.5F, -34.0F + 26.5F, -4.25F, 2.0F, 3.0F, 9.0F, NO_DEFORMATION)
                .texOffs(68, 7).addBox(-3.5F, -33.0F + 26.5F, -4.325F, 7.0F, 1.0F, 1.0F, NO_DEFORMATION)
                .texOffs(65, 8).addBox(3.5F, -33.975F + 26.5F, -4.475F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.01F))
                .texOffs(65, 8).addBox(-4.5F, -33.975F + 26.5F, -4.475F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.01F))
                .texOffs(67, 16).addBox(2.5F, -31.0F + 26.5F, -1.0F, 2.0F, 2.0F, 5.0F, NO_DEFORMATION)
                .texOffs(76, 14).addBox(-4.5F, -31.0F + 26.5F, -1.0F, 2.0F, 2.0F, 5.0F, NO_DEFORMATION)
                .texOffs(51, 27).addBox(-4.5F, -34.0F + 26.5F, -4.25F, 2.0F, 3.0F, 9.0F, NO_DEFORMATION)
                .texOffs(65, 9).addBox(-4.0F, -35.0F + 26.5F, 0.0F, 2.0F, 1.0F, 4.0F, NO_DEFORMATION)
                .texOffs(54, 24).addBox(-4.0F, -34.0F + 26.5F, -4.5F, 8.0F, 1.0F, 1.0F, NO_DEFORMATION)
                .texOffs(66, 0).addBox(-4.0F, -34.025F + 26.5F, 4.0F, 8.0F, 6.0F, 1.0F, NO_DEFORMATION), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offsetAndRotation(2.5F, -34.0F + 26.5F, 0.0F, 0.0F, 0.0F, 0.2618F));

        PartDefinition Base_r1 = LeftEar.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(56, 5).addBox(0.3283F, 0.2707F, 0.8745F, 2.0F, 3.0F, 2.0F, NO_DEFORMATION), PartPose.offsetAndRotation(3.0F, 3.0F, 0.1F, -1.4379F, 0.6464F, 0.3107F));

        PartDefinition Base_r2 = LeftEar.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(56, 0).addBox(-1.2855F, 0.4843F, -1.6606F, 2.0F, 3.0F, 2.0F, NO_DEFORMATION), PartPose.offsetAndRotation(3.0F, 3.0F, 0.1F, -1.1519F, 0.4605F, 0.4624F));

        PartDefinition Base_r3 = LeftEar.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(16, 54).addBox(0.3291F, -3.8388F, -2.2374F, 2.0F, 7.0F, 3.0F, NO_DEFORMATION), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7681F, 0.1841F, -0.0746F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.0F, -34.0F + 26.5F, 0.0F, 0.0F, 0.0F, -0.2618F));

        PartDefinition Base_r4 = RightEar.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(0, 59).addBox(0.8362F, 0.0081F, 0.3482F, 2.0F, 3.0F, 2.0F, NO_DEFORMATION), PartPose.offsetAndRotation(-2.5F, 3.0F, 0.0F, -2.2454F, -0.186F, 1.3485F));

        PartDefinition Base_r5 = RightEar.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(8, 59).addBox(-1.6613F, 0.2908F, -1.3146F, 2.0F, 3.0F, 2.0F, NO_DEFORMATION), PartPose.offsetAndRotation(-2.5F, 3.0F, 0.0F, -2.0891F, -0.4533F, 1.2989F));

        PartDefinition Base_r6 = RightEar.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(0, 48).addBox(-1.8291F, -3.8388F, -2.2374F, 2.0F, 7.0F, 3.0F, NO_DEFORMATION), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7681F, -0.1841F, 0.0746F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -25.0F + 25.5F, -2.0F, 8.0F, 12.0F, 4.0F, NO_DEFORMATION), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, -14.0F + 25.5F, 0.0F));

        PartDefinition Base_r7 = Tail.addOrReplaceChild("Base_r7", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.1914F, -0.9483F, 2.0F, 6.0F, 2.0F, NO_DEFORMATION), PartPose.offsetAndRotation(0.0F, 2.0F, 6.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition Base_r8 = Tail.addOrReplaceChild("Base_r8", CubeListBuilder.create().texOffs(16, 43).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 7.0F, 3.0F, NO_DEFORMATION), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(24, 16).addBox(-8.0F + 5F, -26.0F + 24.5F, -2.0F, 4.0F, 12.0F, 4.0F, NO_DEFORMATION)
                .texOffs(20, 18).addBox(-5.0F + 5F, -14.25F + 24.5F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(12, 34).addBox(-8.0F + 5F, -14.25F + 24.5F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(12, 34).addBox(-8.0F + 5F, -14.25F + 24.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(12, 34).addBox(-8.0F + 5F, -14.25F + 24.5F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition Spike_r1 = RightArm.addOrReplaceChild("Spike_r1", CubeListBuilder.create().texOffs(52, 0).addBox(-0.5F, -2.5F, -2.225F, 1.0F, 5.0F, 1.0F, NO_DEFORMATION), PartPose.offsetAndRotation(-6.5789F + 5F, -20.6254F + 24.5F, 0.6151F, -2.6425F, 0.8346F, 3.1091F));

        PartDefinition Base_r9 = RightArm.addOrReplaceChild("Base_r9", CubeListBuilder.create().texOffs(52, 6).addBox(-0.6209F, -1.6159F, -2.9526F, 1.0F, 4.0F, 1.0F, NO_DEFORMATION), PartPose.offsetAndRotation(-7.5F + 5F, -24.75F + 24.5F, 4.0F, -0.3968F, -0.1769F, -0.2635F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 32).addBox(4.0F - 5F, -26.0F + 24.5F, -2.0F, 4.0F, 12.0F, 4.0F, NO_DEFORMATION)
                .texOffs(24, 18).addBox(4.0F - 5F, -14.25F + 24.5F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(12, 32).addBox(7.0F - 5F, -14.25F + 24.5F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(12, 32).addBox(7.0F - 5F, -14.25F + 24.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(12, 32).addBox(7.0F - 5F, -14.25F + 24.5F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.001F)), PartPose.offset(0.0F, 25.0F, 0.0F));

        PartDefinition Spike_r2 = LeftArm.addOrReplaceChild("Spike_r2", CubeListBuilder.create().texOffs(48, 0).addBox(0.875F, -1.5F, -0.5F, 1.0F, 5.0F, 1.0F, NO_DEFORMATION), PartPose.offsetAndRotation(7.1568F - 5F, -21.4289F + 24.5F, 1.1568F, -0.4796F, -0.6979F, 0.7102F));

        PartDefinition Base_r10 = LeftArm.addOrReplaceChild("Base_r10", CubeListBuilder.create().texOffs(48, 6).addBox(0.1209F, -1.6159F, -2.9526F, 1.0F, 4.0F, 1.0F, NO_DEFORMATION), PartPose.offsetAndRotation(7.0F - 5F, -24.75F + 24.5F, 4.0F, -0.3968F, 0.1769F, 0.2635F));

        return LayerDefinition.create(meshdefinition, 100, 64);
    }

    @Override
    public void prepareMobModel(LatexBeifeng p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(controller, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        controller.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LatexBeifeng entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        controller.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
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
    public LatexHumanoidModelController getController() {
        return controller;
    }
}