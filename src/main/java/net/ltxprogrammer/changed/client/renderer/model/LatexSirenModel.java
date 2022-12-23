package net.ltxprogrammer.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.beast.LatexMantaRayFemale;
import net.ltxprogrammer.changed.entity.beast.LatexSiren;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class LatexSirenModel extends LatexHumanoidModel<LatexSiren> implements LatexHumanoidModelInterface {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_siren"), "main");
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Abdomen;
    private final ModelPart LowerAbdomen;
    private final ModelPart Tail;
    private final LatexHumanoidModelController controller;

    public LatexSirenModel(ModelPart root) {
        super(root);
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Abdomen = root.getChild("Abdomen");
        this.LowerAbdomen = Abdomen.getChild("LowerAbdomen");
        this.Tail = LowerAbdomen.getChild("Tail");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        controller = LatexHumanoidModelController.Builder.of(this, Head, Torso, Tail, RightArm, LeftArm, new ModelPart(List.of(), Map.of()), new ModelPart(List.of(), Map.of())).noLegs(Abdomen, LowerAbdomen).tailJoints(List.of(Tail.getChild("Joint"), Tail.getChild("Joint").getChild("Joint2"))).legLengthOffset(0.0F).tailAidsInSwim().build();
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Abdomen = partdefinition.addOrReplaceChild("Abdomen", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Base_r1 = Abdomen.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(24, 14).addBox(-5.0F, 0.734F, -2.3537F, 10.0F, 2.0F, 6.0F, new CubeDeformation(0.3F))
                .texOffs(0, 0).addBox(-5.5F, 2.6799F, -2.8597F, 11.0F, 7.0F, 7.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 0.0478F, -0.6297F, 0.0F, 0.0F, 0.0F));

        PartDefinition LowerAbdomen = Abdomen.addOrReplaceChild("LowerAbdomen", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 9.0F, -1.0F, 0.8727F, 0.0F, 0.0F));

        PartDefinition Base_r2 = LowerAbdomen.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(56, 57).addBox(-4.5F, -0.6616F, -2.0281F, 9.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(25, 23).addBox(-5.0F, 0.3384F, -3.5281F, 10.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0025F, 1.0529F, 0.0F, 0.0F, 0.0F));

        PartDefinition Tail = LowerAbdomen.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, 0.6981F, 0.0F, 0.0F));

        PartDefinition Base_r3 = Tail.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(0, 31).addBox(-4.0F, 3.8569F, -3.5836F, 8.0F, 7.0F, 5.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, -5.0025F, 1.0529F, 0.2007F, 0.0F, 0.0F));

        PartDefinition Joint = Tail.addOrReplaceChild("Joint", CubeListBuilder.create(), PartPose.offset(0.0F, 5.8854F, 2.329F));

        PartDefinition Base_r4 = Joint.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(52, 18).addBox(-3.0F, 16.8531F, -2.848F, 6.0F, 6.0F, 4.0F, new CubeDeformation(-0.15F))
                .texOffs(56, 47).addBox(-3.0F, 10.8541F, -2.9527F, 6.0F, 6.0F, 4.0F, new CubeDeformation(0.35F)), PartPose.offsetAndRotation(0.0F, -10.8879F, -1.2762F, 0.2007F, 0.0F, 0.0F));

        PartDefinition Joint2 = Joint.addOrReplaceChild("Joint2", CubeListBuilder.create(), PartPose.offset(0.0F, 11.4715F, 2.5432F));

        PartDefinition Base_r5 = Joint2.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(36, 46).addBox(-0.5F, 34.3872F, 7.1776F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.05F))
                .texOffs(0, 64).addBox(-1.0F, 26.4872F, 7.1776F, 2.0F, 8.0F, 3.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, -22.3593F, -3.8194F, -0.1484F, 0.0F, 0.0F));

        PartDefinition Base_r6 = Joint2.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(0, 14).addBox(-0.5F, -29.2462F, 19.6296F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(48, 63).addBox(-0.5F, -27.2462F, 22.6296F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(10, 65).addBox(-1.0F, -27.2462F, 19.6296F, 2.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -22.3593F, -3.8194F, -2.1555F, 0.0F, 0.0F));

        PartDefinition Base_r7 = Joint2.addOrReplaceChild("Base_r7", CubeListBuilder.create().texOffs(53, 61).addBox(-2.0F, 21.8458F, -2.4497F, 4.0F, 7.0F, 3.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, -22.3593F, -3.8194F, 0.2007F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(69, 24).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -2.0F, -7.0F, 0.0F, -0.2182F, 0.0F));

        PartDefinition Snout_r2 = Head.addOrReplaceChild("Snout_r2", CubeListBuilder.create().texOffs(63, 69).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -2.0F, -7.0F, 0.0F, 0.2182F, 0.0F));

        PartDefinition Snout_r3 = Head.addOrReplaceChild("Snout_r3", CubeListBuilder.create().texOffs(70, 12).addBox(-1.5F, -27.0F, -6.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(68, 17).addBox(-1.5F, -29.0F, -7.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 27.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Base_r8 = Head.addOrReplaceChild("Base_r8", CubeListBuilder.create().texOffs(10, 52).addBox(-2.1808F, -1.0F, -0.5736F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, -0.75F, -2.0F, 0.5236F, 0.9599F, 0.0F));

        PartDefinition Head_r1 = Head.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(50, 72).addBox(-1.0F, -0.75F, -1.0F, 2.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -7.0F, -1.0F, 0.6545F, 0.0F, 0.0F));

        PartDefinition Head_r2 = Head.addOrReplaceChild("Head_r2", CubeListBuilder.create().texOffs(0, 14).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 52).addBox(3.725F, -6.25F, -4.5F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 14).addBox(-4.725F, -6.25F, -4.5F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Base_r9 = Hair.addOrReplaceChild("Base_r9", CubeListBuilder.create().texOffs(46, 36).addBox(-1.15F, -5.5F, -4.675F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 19).addBox(-1.15F, -6.5F, -4.675F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(58, 39).addBox(-4.5F, -8.0F, -4.8F, 9.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(20, 65).addBox(-4.0F, -8.75F, 3.75F, 2.0F, 11.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(67, 0).addBox(2.0F, -8.75F, 3.75F, 2.0F, 11.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(38, 63).addBox(-2.0F, -9.0F, 4.0F, 4.0F, 12.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(63, 28).addBox(3.75F, -4.0F, 0.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(67, 61).addBox(-4.75F, -4.0F, 0.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(49, 0).addBox(-5.0F, -8.0F, -4.0F, 1.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 52).addBox(4.0F, -8.0F, -4.0F, 1.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(51, 28).addBox(-4.0F, -8.75F, -4.0F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(10, 56).addBox(2.0F, -8.75F, -4.0F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(58, 42).addBox(-4.0F, -0.75F, 2.0F, 8.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(42, 38).addBox(-2.0F, -9.0F, -4.0F, 4.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Fins = Head.addOrReplaceChild("Fins", CubeListBuilder.create(), PartPose.offset(0.0F, -7.5F, 0.0F));

        PartDefinition Base_r10 = Fins.addOrReplaceChild("Base_r10", CubeListBuilder.create().texOffs(69, 69).addBox(-2.1808F, -1.0F, -0.5736F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, 6.75F, -2.0F, -0.5236F, 0.9599F, -3.1416F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition Waist_r1 = Torso.addOrReplaceChild("Waist_r1", CubeListBuilder.create().texOffs(29, 0).addBox(-4.5F, -2.0F, -2.5F, 9.0F, 2.0F, 5.0F, new CubeDeformation(0.4F))
                .texOffs(50, 12).addBox(-4.0F, -3.0F, -2.0F, 8.0F, 1.0F, 4.0F, new CubeDeformation(0.1F))
                .texOffs(26, 36).addBox(-4.0F, -8.0F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(-0.2F))
                .texOffs(0, 43).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition TailFin_r1 = Torso.addOrReplaceChild("TailFin_r1", CubeListBuilder.create().texOffs(26, 62).addBox(-1.0F, -1.875F, 2.2571F, 2.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 12.0478F, -0.1297F, 1.0036F, 0.0F, 0.0F));

        PartDefinition Plantoids = Torso.addOrReplaceChild("Plantoids", CubeListBuilder.create(), PartPose.offset(0.0F, 0.25F, 0.0F));

        PartDefinition Center_r1 = Plantoids.addOrReplaceChild("Center_r1", CubeListBuilder.create().texOffs(20, 43).addBox(-1.0F, -11.5F, -2.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftPlantoid_r1 = Plantoids.addOrReplaceChild("LeftPlantoid_r1", CubeListBuilder.create().texOffs(52, 47).addBox(0.75F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.4F))
                .texOffs(10, 56).addBox(-3.75F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.4F)), PartPose.offsetAndRotation(0.0F, 3.0F, -2.0F, -0.1047F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create(), PartPose.offset(-5.0F, 0.0F, 0.0F));

        PartDefinition Finger_r1 = RightArm.addOrReplaceChild("Finger_r1", CubeListBuilder.create().texOffs(29, 0).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(21, 30).addBox(6.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, 1.75F, -3.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Finger_r2 = RightArm.addOrReplaceChild("Finger_r2", CubeListBuilder.create().texOffs(29, 2).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, 1.75F, -1.5F, 0.0F, 0.0F, 0.0F));

        PartDefinition Finger_r3 = RightArm.addOrReplaceChild("Finger_r3", CubeListBuilder.create().texOffs(0, 30).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Spike_r1 = RightArm.addOrReplaceChild("Spike_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-0.575F, -2.8F, -2.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0789F, 4.8746F, 1.1151F, -2.6425F, 0.8346F, 3.1091F));

        PartDefinition RightArm_r1 = RightArm.addOrReplaceChild("RightArm_r1", CubeListBuilder.create().texOffs(24, 46).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create(), PartPose.offset(5.0F, 0.0F, 0.0F));

        PartDefinition Finger_r4 = LeftArm.addOrReplaceChild("Finger_r4", CubeListBuilder.create().texOffs(0, 32).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(21, 32).addBox(6.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 1.75F, -3.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Finger_r5 = LeftArm.addOrReplaceChild("Finger_r5", CubeListBuilder.create().texOffs(0, 34).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.75F, -1.5F, 0.0F, 0.0F, 0.0F));

        PartDefinition Finger_r6 = LeftArm.addOrReplaceChild("Finger_r6", CubeListBuilder.create().texOffs(21, 34).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Spike_r2 = LeftArm.addOrReplaceChild("Spike_r2", CubeListBuilder.create().texOffs(59, 0).addBox(-0.25F, -1.425F, -0.425F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.6568F, 4.0711F, 1.6568F, -0.4796F, -0.6979F, 0.7102F));

        PartDefinition LeftArm_r1 = LeftArm.addOrReplaceChild("LeftArm_r1", CubeListBuilder.create().texOffs(40, 47).addBox(0.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void prepareMobModel(LatexSiren p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(controller, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        controller.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LatexSiren entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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
        Abdomen.render(poseStack, buffer, packedLight, packedOverlay);
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