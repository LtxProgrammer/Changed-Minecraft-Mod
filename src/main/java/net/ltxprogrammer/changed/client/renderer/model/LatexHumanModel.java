package net.ltxprogrammer.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.beast.LatexHuman;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class LatexHumanModel extends LatexHumanoidModel<LatexHuman> implements LatexHumanoidModelInterface<LatexHuman, LatexHumanModel> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_human"), "main");
    public static final ModelLayerLocation LAYER_LOCATION_SLIM = new ModelLayerLocation(Changed.modResource("latex_human"), "main_slim");
    private final ModelPart RightLeg;
    private final ModelPart LeftLeg;
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;

    private final ModelPart RightPants;
    private final ModelPart LeftPants;
    private final ModelPart RightSleeve;
    private final ModelPart LeftSleeve;
    private final ModelPart Hat;
    private final ModelPart Jacket;

    private final LatexAnimator<LatexHuman, LatexHumanModel> animator;

    private static final ModelPart NULL_PART = new ModelPart(List.of(), Map.of());

    public LatexHumanModel(ModelPart root) {
        super(root);
        this.RightLeg = root.getChild("RightLeg");
        this.LeftLeg = root.getChild("LeftLeg");
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");

        RightPants = RightLeg.getChild("RightPants");
        LeftPants = LeftLeg.getChild("LeftPants");
        RightSleeve = RightArm.getChild("RightSleeve");
        LeftSleeve = LeftArm.getChild("LeftSleeve");
        Hat = Head.getChild("Hat");
        Jacket = Torso.getChild("Jacket");

        animator = LatexAnimator.of(this).hipOffset(-1.5f).legLength(10.5f)
                .addPreset(AnimatorPresets.wolfLike(
                        Head, NULL_PART, NULL_PART,
                        Torso, LeftArm, RightArm,
                        NULL_PART, List.of(),
                        LeftLeg, NULL_PART, NULL_PART, NULL_PART, RightLeg, NULL_PART, NULL_PART, NULL_PART));
    }

    public void defaultModelProperties() {
        Hat.visible = true;
        Jacket.visible = true;
        LeftPants.visible = true;
        RightPants.visible = true;
        LeftSleeve.visible = true;
        RightSleeve.visible = true;
    }

    public void setModelProperties(AbstractClientPlayer player) {
        Hat.visible = player.isModelPartShown(PlayerModelPart.HAT);
        Jacket.visible = player.isModelPartShown(PlayerModelPart.JACKET);
        LeftPants.visible = player.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
        RightPants.visible = player.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
        LeftSleeve.visible = player.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
        RightSleeve.visible = player.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
    }

    public static LayerDefinition createBodyLayer(boolean slim) {
        float armWidth = slim ? 3.0f : 4.0f;
        float rightArmOffset = slim ? 1.0f : 0.0f;

        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 12.0F, 0.0F));

        PartDefinition RightPants = RightLeg.addOrReplaceChild("RightPants", CubeListBuilder.create().texOffs(0, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(16, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 12.0F, 0.0F));

        PartDefinition LeftPants = LeftLeg.addOrReplaceChild("LeftPants", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Hat = Head.addOrReplaceChild("Hat", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(16, 16).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Jacket = Torso.addOrReplaceChild("Jacket", CubeListBuilder.create().texOffs(16, 32).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(40, 16).addBox(-3.0F + rightArmOffset, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

        PartDefinition RightSleeve = RightArm.addOrReplaceChild("RightSleeve", CubeListBuilder.create().texOffs(40, 32).addBox(-3.0F + rightArmOffset, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(32, 48).addBox(-1.0F, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 2.0F, 0.0F));

        PartDefinition LeftSleeve = LeftArm.addOrReplaceChild("LeftSleeve", CubeListBuilder.create().texOffs(48, 48).addBox(-1.0F, -2.0F, -2.0F, armWidth, 12.0F, 4.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void prepareMobModel(LatexHuman p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LatexHuman entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }

    public ModelPart getArm(HumanoidArm arm) {
        return arm == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
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
    public LatexAnimator<LatexHuman, LatexHumanModel> getAnimator() {
        return animator;
    }
}