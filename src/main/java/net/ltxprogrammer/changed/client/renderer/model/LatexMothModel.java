package net.ltxprogrammer.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.beast.LatexMoth;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class LatexMothModel extends LatexHumanoidModel<LatexMoth> implements LatexHumanoidModelInterface {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_moth"), "main");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Tail;
    private final ModelPart RightWing;
    private final ModelPart LeftWing;
    private final LatexHumanoidModelController controller;

    public LatexMothModel(ModelPart root) {
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
        controller = LatexHumanoidModelController.Builder.of(this, Head, Torso, Tail, RightArm, LeftArm, RightLeg, LeftLeg).wings(RightWing, LeftWing).build();
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(52, 31).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-2.5F, 10.0F, 0.0F));

        PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(49, 50).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(36, 42).addBox(-1.99F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(12, 47).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(48, 38).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(2.5F, 10.0F, 0.0F));

        PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(48, 22).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(36, 31).addBox(-2.01F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

        PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(42, 12).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.5F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition Head_r1 = Head.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(0, 60).addBox(1.75F, -12.0F, 1.5F, 1.0F, 5.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 0.5236F, -0.2182F, 0.0F));

        PartDefinition Head_r2 = Head.addOrReplaceChild("Head_r2", CubeListBuilder.create().texOffs(6, 60).addBox(-2.75F, -12.0F, 1.5F, 1.0F, 5.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.5F, 0.0F, 0.5236F, 0.2182F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.5F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(52, 44).addBox(-4.0F, 0.5F, 1.5F, 8.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(59, 21).addBox(-3.0F, 4.1F, -3.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.2F))
                .texOffs(46, 0).addBox(-4.0F, 0.7F, -3.0F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.2F))
                .texOffs(20, 16).addBox(-2.0F, 5.5F, -2.5F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.2F))
                .texOffs(12, 57).addBox(-3.0F, 2.5F, 1.5F, 6.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(58, 18).addBox(-3.0F, 0.5F, -3.75F, 6.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(20, 18).addBox(-2.0F, 2.5F, -3.75F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(58, 15).addBox(-3.0F, 0.7F, 1.75F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition lefttuft_r1 = Torso.addOrReplaceChild("lefttuft_r1", CubeListBuilder.create().texOffs(28, 48).addBox(-11.5F, -23.5F, -3.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, 25.5F, 0.0F, 0.0F, 0.0F, 0.6109F));

        PartDefinition righttuft_r1 = Torso.addOrReplaceChild("righttuft_r1", CubeListBuilder.create().texOffs(0, 48).addBox(10.5F, -23.5F, -3.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(0.0F, 25.5F, 0.0F, 0.0F, 0.0F, -0.6109F));

        PartDefinition LeftWing = Torso.addOrReplaceChild("LeftWing", CubeListBuilder.create(), PartPose.offset(2.0F, 5.5F, 0.0F));

        PartDefinition WingBase_r1 = LeftWing.addOrReplaceChild("WingBase_r1", CubeListBuilder.create().texOffs(54, 6).addBox(-5.25F, -18.75F, 1.25F, 6.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(12, 32).addBox(-5.25F, -19.75F, 1.25F, 5.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.0F, 21.0F, 0.0F, -0.1745F, -0.6109F, 0.48F));

        PartDefinition WingBase_r2 = LeftWing.addOrReplaceChild("WingBase_r2", CubeListBuilder.create().texOffs(52, 47).addBox(5.75F, -18.25F, -6.0F, 8.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(32, 6).addBox(4.75F, -23.25F, -6.0F, 10.0F, 5.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.0F, 21.0F, 0.0F, -0.1745F, -0.6109F, -0.2182F));

        PartDefinition RightWing = Torso.addOrReplaceChild("RightWing", CubeListBuilder.create(), PartPose.offset(-2.0F, 5.5F, 0.0F));

        PartDefinition WingBase_r3 = RightWing.addOrReplaceChild("WingBase_r3", CubeListBuilder.create().texOffs(32, 12).addBox(-0.75F, -18.75F, 1.25F, 6.0F, 3.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(26, 58).addBox(0.25F, -19.75F, 1.25F, 5.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 21.0F, 0.0F, -0.1745F, 0.6109F, -0.48F));

        PartDefinition WingBase_r4 = RightWing.addOrReplaceChild("WingBase_r4", CubeListBuilder.create().texOffs(46, 4).addBox(-13.75F, -18.25F, -6.0F, 8.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(24, 0).addBox(-14.75F, -23.25F, -6.0F, 10.0F, 5.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 21.0F, 0.0F, -0.1745F, 0.6109F, 0.2182F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 11.5F, 0.0F));

        PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(24, 16).addBox(-3.0F, -3.0F, -3.1983F, 6.0F, 9.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 6.0F, 1.4835F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(37, 53).addBox(-1.5F, 1.0F, -1.0F, 3.0F, 3.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.4399F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(20, 31).addBox(-3.0F, -1.5F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(54, 10).addBox(-1.0F, -2.25F, -2.25F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.2F))
                .texOffs(0, 18).addBox(0.0F, 10.25F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 16).addBox(-3.0F, 10.25F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(4, 6).addBox(-3.0F, 10.25F, -0.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 6).addBox(-3.0F, 10.25F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 0.5F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, -1.5F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 55).addBox(-1.0F, -2.25F, -2.25F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.2F))
                .texOffs(0, 32).addBox(-1.0F, 10.25F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(28, 6).addBox(2.0F, 10.25F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(24, 20).addBox(2.0F, 10.25F, -0.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(24, 6).addBox(2.0F, 10.25F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 0.5F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void prepareMobModel(LatexMoth p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(controller, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        controller.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LatexMoth entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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