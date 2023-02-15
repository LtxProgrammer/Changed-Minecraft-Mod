package net.ltxprogrammer.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.beast.DarkLatexYufeng;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class DarkLatexYufengModel extends LatexHumanoidModel<DarkLatexYufeng> implements LatexHumanoidModelInterface {
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
    private final LatexHumanoidModelController controller;

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
        controller = LatexHumanoidModelController.Builder.of(this, Head, Torso, Tail, RightArm, LeftArm, RightLeg, LeftLeg).wings(RightWing, LeftWing).build();
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
                .texOffs(24, 0).addBox(-1.5F, -1.5F, -6.0F, 3.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition Base_r1 = Head.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(32, 11).addBox(-1.4F, 0.35F, 2.3F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(5.5F, -4.5F, 0.0F, -1.4312F, 0.6458F, 0.4974F));

        PartDefinition Base_r2 = Head.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(54, 19).addBox(-0.6F, 0.35F, 2.3F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-5.5F, -4.5F, 0.0F, -1.4312F, -0.6458F, -0.4974F));

        PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offsetAndRotation(-2.5F, -7.5F, 0.0F, 0.0F, 0.0F, -0.2618F));

        PartDefinition Base_r3 = RightEar.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(58, 0).addBox(-0.6209F, -1.775F, -2.9526F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-0.5F, 0.0F, 0.0F, -0.2094F, -0.056F, 0.0059F));

        PartDefinition Base_r4 = RightEar.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(54, 24).addBox(-1.3F, -1.5F, -0.975F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.7872F, 2.969F, -0.6745F, -2.087F, -0.3257F, 1.3714F));

        PartDefinition Base_r5 = RightEar.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(47, 49).addBox(-2.0088F, -5.0341F, -1.0F, 2.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7681F, -0.1841F, 0.0746F));

        PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offsetAndRotation(2.5F, -7.5F, 0.0F, 0.0F, 0.0F, 0.2618F));

        PartDefinition Base_r6 = LeftEar.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(58, 5).addBox(-0.3791F, -1.77F, -2.9526F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.5F, 0.0F, 0.0F, -0.2094F, 0.056F, -0.0059F));

        PartDefinition Base_r7 = LeftEar.addOrReplaceChild("Base_r7", CubeListBuilder.create().texOffs(56, 29).addBox(-1.7048F, -0.3091F, -1.3212F, 2.0F, 3.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.0F, 3.0F, 0.0F, -1.1519F, 0.4605F, 0.4624F));

        PartDefinition Base_r8 = LeftEar.addOrReplaceChild("Base_r8", CubeListBuilder.create().texOffs(48, 0).addBox(0.0088F, -5.0341F, -1.0F, 2.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7681F, 0.1841F, -0.0746F));

        PartDefinition Mask = Head.addOrReplaceChild("Mask", CubeListBuilder.create().texOffs(57, 49).addBox(-1.0F, -33.999F, -5.0F, 2.0F, 5.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 32).addBox(-2.0F, -33.0F, -5.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(28, 4).addBox(1.0F, -33.0F, -5.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(12, 48).addBox(2.0F, -32.0F, -5.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 48).addBox(-3.0F, -32.0F, -5.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(24, 4).addBox(-4.0F, -31.0F, -5.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 16).addBox(3.0F, -31.0F, -5.0F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(47, 1).addBox(2.0F, -29.0F, -5.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(44, 32).addBox(-3.0F, -29.0F, -5.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(20, 16).addBox(-4.0F, -28.0F, -5.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 5).addBox(2.0F, -28.0F, -5.0F, 2.0F, 1.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(52, 10).addBox(-2.0F, -29.0F, -7.0F, 4.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 26.5F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.5F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -1.5F, 0.0F));

        PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 11.5F, 0.0F));

        PartDefinition Base_r9 = Tail.addOrReplaceChild("Base_r9", CubeListBuilder.create().texOffs(67, 53).addBox(-1.0F, -10.35F, 13.925F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 14.0F, 0.0F, 0.0436F, 0.0F, 0.0F));

        PartDefinition Base_r10 = Tail.addOrReplaceChild("Base_r10", CubeListBuilder.create().texOffs(65, 14).addBox(-1.5F, -13.2F, 6.9F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 14.0F, 0.0F, -0.1309F, 0.0F, 0.0F));

        PartDefinition Base_r11 = Tail.addOrReplaceChild("Base_r11", CubeListBuilder.create().texOffs(67, 0).addBox(-1.5F, -1.375F, -2.6983F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(0.0F, 2.0F, 6.0F, -0.3927F, 0.0F, 0.0F));

        PartDefinition Base_r12 = Tail.addOrReplaceChild("Base_r12", CubeListBuilder.create().texOffs(45, 60).addBox(-2.0F, -2.8F, 0.4F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition LeftWing = Torso.addOrReplaceChild("LeftWing", CubeListBuilder.create(), PartPose.offset(2.0F, 5.0F, 2.0F));

        PartDefinition Base_r13 = LeftWing.addOrReplaceChild("Base_r13", CubeListBuilder.create().texOffs(39, 42).addBox(8.0F, -27.0F, 0.0F, 3.0F, 16.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(20, 52).addBox(7.0F, -26.0F, 0.0F, 1.0F, 12.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(57, 55).addBox(5.0F, -24.0F, 0.0F, 1.0F, 5.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(8, 54).addBox(6.0F, -25.0F, 0.0F, 1.0F, 9.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(56, 36).addBox(2.0F, -24.0F, 0.0F, 3.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.0F, 21.5F, -2.0F, 0.0F, -0.6545F, 0.0F));

        PartDefinition RightWing = Torso.addOrReplaceChild("RightWing", CubeListBuilder.create(), PartPose.offset(-2.0F, 5.0F, 2.0F));

        PartDefinition Base_r14 = RightWing.addOrReplaceChild("Base_r14", CubeListBuilder.create().texOffs(31, 42).addBox(8.0F, -27.0F, -1.0F, 3.0F, 16.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 52).addBox(7.0F, -26.0F, -1.0F, 1.0F, 12.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(24, 52).addBox(6.0F, -25.0F, -1.0F, 1.0F, 9.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(12, 54).addBox(5.0F, -24.0F, -1.0F, 1.0F, 5.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 0).addBox(2.0F, -24.0F, -1.0F, 3.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 21.5F, -2.0F, 0.0F, -2.4871F, 0.0F));

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

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void prepareMobModel(DarkLatexYufeng p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(controller, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        controller.setupHand();
    }

    @Override
    public void setupAnim(@NotNull DarkLatexYufeng entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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