package net.ltxprogrammer.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.beast.LatexMermaidShark;
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
public class LatexMermaidSharkModel extends LatexHumanoidModel<LatexMermaidShark> implements LatexHumanoidModelInterface {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_mermaid_shark"), "main");
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Abdomen;
    private final ModelPart LowerAbdomen;
    private final ModelPart Tail;
    private final LatexHumanoidModelController controller;

    public LatexMermaidSharkModel(ModelPart root) {
        super(root);
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Abdomen = root.getChild("Abdomen");
        this.LowerAbdomen = Abdomen.getChild("LowerAbdomen");
        this.Tail = LowerAbdomen.getChild("Tail");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        controller = LatexHumanoidModelController.Builder.of(this, Head, Torso, Tail, RightArm, LeftArm, new ModelPart(List.of(), Map.of()), new ModelPart(List.of(), Map.of())).noLegs(Abdomen, LowerAbdomen)
                .tailJoints(List.of(Tail.getChild("Joint"), Tail.getChild("Joint").getChild("Joint2"))).legLengthOffset(0.0F).tailAidsInSwim().build();
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Abdomen = partdefinition.addOrReplaceChild("Abdomen", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition TailFin_r1 = Abdomen.addOrReplaceChild("TailFin_r1", CubeListBuilder.create().texOffs(32, 57).addBox(-1.0F, 2.125F, -2.7429F, 2.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0478F, -0.1297F, 1.0036F, 0.0F, 0.0F));

        PartDefinition Base_r1 = Abdomen.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(24, 0).addBox(-4.5F, -0.3201F, -1.9097F, 9.0F, 3.0F, 5.0F, new CubeDeformation(0.35F))
                .texOffs(0, 16).addBox(-5.0F, 2.6799F, -2.8597F, 10.0F, 6.0F, 7.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 0.0478F, -0.6297F, 0.0F, 0.0F, 0.0F));

        PartDefinition Base_r2 = Abdomen.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(52, 32).addBox(-7.0F, -0.3201F, -0.8597F, 8.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, 4.0478F, -1.6297F, 0.0F, 0.0F, -0.5236F));

        PartDefinition Base_r3 = Abdomen.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(51, 6).addBox(-1.0F, -0.3201F, -0.8597F, 8.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, 4.0478F, -1.6297F, 0.0F, 0.0F, 0.5236F));

        PartDefinition LowerAbdomen = Abdomen.addOrReplaceChild("LowerAbdomen", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 9.0F, -1.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Base_r4 = LowerAbdomen.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(27, 10).addBox(-4.5F, -0.6616F, -3.0281F, 9.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(27, 10).addBox(-4.5F, 0.3384F, -3.0281F, 9.0F, 5.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -1.0025F, 1.0529F, 0.0F, 0.0F, 0.0F));

        PartDefinition Tail = LowerAbdomen.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 4.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Base_r5 = Tail.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(19, 41).addBox(-3.5F, -9.132F, -4.9203F, 7.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 8.5418F, 4.7984F, 0.1745F, 0.0F, 0.0F));

        PartDefinition Joint = Tail.addOrReplaceChild("Joint", CubeListBuilder.create(), PartPose.offset(0.0F, 4.0F, 1.5F));

        PartDefinition Base_r6 = Joint.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(71, 0).addBox(-2.5F, -5.132F, -4.4203F, 5.0F, 5.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(0.0F, 4.5418F, 3.2984F, 0.1745F, 0.0F, 0.0F));

        PartDefinition Joint2 = Joint.addOrReplaceChild("Joint2", CubeListBuilder.create(), PartPose.offset(0.0F, 5.0F, 1.0F));

        PartDefinition Base_r7 = Joint2.addOrReplaceChild("Base_r7", CubeListBuilder.create().texOffs(58, 59).addBox(-1.0F, -0.1437F, -4.1254F, 2.0F, 8.0F, 3.0F, new CubeDeformation(-0.05F))
                .texOffs(0, 5).addBox(-0.5F, 7.7563F, -4.1254F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 2.0418F, 2.7984F, -0.1745F, 0.0F, 0.0F));

        PartDefinition Base_r8 = Joint2.addOrReplaceChild("Base_r8", CubeListBuilder.create().texOffs(0, 37).addBox(-0.5F, -5.7475F, 3.2707F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(13, 61).addBox(-1.0F, -5.7475F, 0.2707F, 2.0F, 8.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-0.5F, -7.7475F, 0.2707F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0418F, 2.7984F, -2.1817F, 0.0F, 0.0F));

        PartDefinition Base_r9 = Joint2.addOrReplaceChild("Base_r9", CubeListBuilder.create().texOffs(57, 12).addBox(-2.0F, -2.2952F, -3.9279F, 4.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0418F, 2.5484F, 0.1745F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(52, 66).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -2.0F, -7.0F, 0.0F, -0.2182F, 0.0F));

        PartDefinition Snout_r2 = Head.addOrReplaceChild("Snout_r2", CubeListBuilder.create().texOffs(64, 66).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -2.0F, -7.0F, 0.0F, 0.2182F, 0.0F));

        PartDefinition Snout_r3 = Head.addOrReplaceChild("Snout_r3", CubeListBuilder.create().texOffs(16, 37).addBox(-1.5F, -27.0F, -6.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(44, 64).addBox(-1.5F, -29.0F, -7.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 27.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Head_r1 = Head.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Fins = Head.addOrReplaceChild("Fins", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -7.5F, 0.0F, -0.5236F, 0.0F, 0.0F));

        PartDefinition Head_r2 = Fins.addOrReplaceChild("Head_r2", CubeListBuilder.create().texOffs(23, 66).addBox(-0.5F, -7.0F, 0.0F, 2.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(59, 45).addBox(-0.5F, -4.0F, 0.0F, 2.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 2.0F, -2.0F, -0.0873F, 0.0F, 0.0F));

        PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 37).addBox(-2.0F, -33.0F, 3.0F, 4.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(34, 21).addBox(-4.0F, -32.5F, 4.0F, 8.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(34, 21).addBox(-4.0F, -32.4F, 2.75F, 8.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 62).addBox(2.0F, -33.0F, 7.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(16, 50).addBox(2.5F, -32.0F, 3.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.025F))
                .texOffs(34, 25).addBox(-3.5F, -31.0F, 2.75F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 29).addBox(3.5F, -32.0F, 2.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(24, 0).addBox(-4.5F, -32.0F, 2.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 57).addBox(2.5F, -29.0F, 6.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(28, 50).addBox(-4.5F, -29.0F, 6.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(48, 21).addBox(-4.5F, -32.0F, 3.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.025F))
                .texOffs(60, 20).addBox(-4.0F, -33.0F, 7.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(32, 8).addBox(-4.0F, -32.0F, 2.5F, 8.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F))
                .texOffs(55, 38).addBox(-4.0F, -32.0F, 11.0F, 8.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 25.0F, -7.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition Torso_r1 = Torso.addOrReplaceChild("Torso_r1", CubeListBuilder.create().texOffs(0, 29).addBox(-4.0F, -5.0F, -3.05F, 9.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, 14.0F, 0.5F, 0.0F, 0.0F, 0.0F));

        PartDefinition Torso_r2 = Torso.addOrReplaceChild("Torso_r2", CubeListBuilder.create().texOffs(28, 29).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create(), PartPose.offset(-5.0F, 0.0F, 0.0F));

        PartDefinition Finger_r1 = RightArm.addOrReplaceChild("Finger_r1", CubeListBuilder.create().texOffs(4, 5).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(23, 29).addBox(6.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, 1.75F, -3.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Finger_r2 = RightArm.addOrReplaceChild("Finger_r2", CubeListBuilder.create().texOffs(24, 3).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, 1.75F, -1.5F, 0.0F, 0.0F, 0.0F));

        PartDefinition Finger_r3 = RightArm.addOrReplaceChild("Finger_r3", CubeListBuilder.create().texOffs(27, 21).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-6.0F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Spike_r1 = RightArm.addOrReplaceChild("Spike_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-0.5F, -2.5F, -2.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0789F, 4.8746F, 1.1151F, -2.6425F, 0.8346F, 3.1091F));

        PartDefinition RightArm_r1 = RightArm.addOrReplaceChild("RightArm_r1", CubeListBuilder.create().texOffs(43, 41).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create(), PartPose.offset(5.0F, 0.0F, 0.0F));

        PartDefinition Finger_r4 = LeftArm.addOrReplaceChild("Finger_r4", CubeListBuilder.create().texOffs(27, 29).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(31, 21).addBox(6.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 1.75F, -3.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Finger_r5 = LeftArm.addOrReplaceChild("Finger_r5", CubeListBuilder.create().texOffs(23, 31).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.75F, -1.5F, 0.0F, 0.0F, 0.0F));

        PartDefinition Finger_r6 = LeftArm.addOrReplaceChild("Finger_r6", CubeListBuilder.create().texOffs(27, 31).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Spike_r2 = LeftArm.addOrReplaceChild("Spike_r2", CubeListBuilder.create().texOffs(16, 50).addBox(-0.125F, -1.5F, -0.5F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.6568F, 4.0711F, 1.6568F, -0.4796F, -0.6979F, 0.7102F));

        PartDefinition LeftArm_r1 = LeftArm.addOrReplaceChild("LeftArm_r1", CubeListBuilder.create().texOffs(0, 46).addBox(0.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void prepareMobModel(LatexMermaidShark p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(controller, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        controller.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LatexMermaidShark entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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