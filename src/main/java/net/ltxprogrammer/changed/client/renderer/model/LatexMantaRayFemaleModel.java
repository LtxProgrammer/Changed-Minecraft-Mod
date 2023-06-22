package net.ltxprogrammer.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.beast.LatexMantaRayFemale;
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
public class LatexMantaRayFemaleModel extends LatexHumanoidModel<LatexMantaRayFemale> implements LatexHumanoidModelInterface<LatexMantaRayFemale, LatexMantaRayFemaleModel> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_manta_ray_female"), "main");
    private final ModelPart RightArm;
    private final ModelPart LeftArm;
    private final ModelPart Head;
    private final ModelPart Torso;
    private final ModelPart Abdomen;
    private final ModelPart LowerAbdomen;
    private final ModelPart Tail;
    private final LatexAnimator<LatexMantaRayFemale, LatexMantaRayFemaleModel> animator;

    public LatexMantaRayFemaleModel(ModelPart root) {
        super(root);
        this.Head = root.getChild("Head");
        this.Torso = root.getChild("Torso");
        this.Abdomen = root.getChild("Abdomen");
        this.LowerAbdomen = Abdomen.getChild("LowerAbdomen");
        this.Tail = LowerAbdomen.getChild("Tail");
        this.RightArm = root.getChild("RightArm");
        this.LeftArm = root.getChild("LeftArm");
        animator = LatexAnimator.of(this).addPreset(AnimatorPresets.snakeLike(Head, Torso, LeftArm, RightArm, Abdomen, LowerAbdomen, Tail, List.of(
                Tail.getChild("Joint"),
                Tail.getChild("Joint").getChild("Joint2"),
                Tail.getChild("Joint").getChild("Joint2").getChild("Joint3"),
                Tail.getChild("Joint").getChild("Joint2").getChild("Joint3").getChild("Joint4")
        )));
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Abdomen = partdefinition.addOrReplaceChild("Abdomen", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

        PartDefinition Base_r1 = Abdomen.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(24, 14).addBox(-5.0F, 0.734F, -2.3537F, 10.0F, 2.0F, 6.0F, new CubeDeformation(0.3F))
                .texOffs(0, 0).addBox(-5.5F, 2.6799F, -2.8597F, 11.0F, 7.0F, 7.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 0.0478F, -0.6297F, 0.0F, 0.0F, 0.0F));

        PartDefinition LowerAbdomen = Abdomen.addOrReplaceChild("LowerAbdomen", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 9.0F, -1.0F, 0.8727F, 0.0F, 0.0F));

        PartDefinition Base_r2 = LowerAbdomen.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(50, 41).addBox(-4.5F, -0.6616F, -2.0281F, 9.0F, 1.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(25, 23).addBox(-5.0F, 0.3384F, -3.5281F, 10.0F, 6.0F, 7.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -1.0025F, 1.0529F, 0.0F, 0.0F, 0.0F));

        PartDefinition Tail = LowerAbdomen.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, 5.0F, 0.0F, 0.6981F, 0.0F, 0.0F));

        PartDefinition Base_r3 = Tail.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(0, 31).addBox(-4.0F, 3.8569F, -3.5836F, 8.0F, 7.0F, 5.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, -5.0025F, 1.0529F, 0.2007F, 0.0F, 0.0F));

        PartDefinition Joint = Tail.addOrReplaceChild("Joint", CubeListBuilder.create(), PartPose.offset(0.0F, 5.8854F, 2.329F));

        PartDefinition Base_r4 = Joint.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(50, 7).addBox(-3.0F, 10.8541F, -2.9527F, 6.0F, 6.0F, 4.0F, new CubeDeformation(0.35F)), PartPose.offsetAndRotation(0.0F, -10.8879F, -1.2762F, 0.2007F, 0.0F, 0.0F));

        PartDefinition Joint2 = Joint.addOrReplaceChild("Joint2", CubeListBuilder.create(), PartPose.offset(0.0F, 6.2215F, 1.5432F));

        PartDefinition Base_r5 = Joint2.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(70, 7).addBox(-3.0F, 16.8531F, -2.848F, 6.0F, 6.0F, 4.0F, new CubeDeformation(-0.15F)), PartPose.offsetAndRotation(0.0F, -17.1093F, -2.8194F, 0.2007F, 0.0F, 0.0F));

        PartDefinition Joint3 = Joint2.addOrReplaceChild("Joint3", CubeListBuilder.create(), PartPose.offset(0.0F, 5.2785F, 0.4568F));

        PartDefinition Base_r6 = Joint3.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(0, 52).addBox(-2.0F, 21.8458F, -2.4497F, 4.0F, 7.0F, 3.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, -22.3879F, -3.2762F, 0.2007F, 0.0F, 0.0F));

        PartDefinition Joint4 = Joint3.addOrReplaceChild("Joint4", CubeListBuilder.create(), PartPose.offset(0.0F, 5.5F, 1.0F));

        PartDefinition Base_r7 = Joint4.addOrReplaceChild("Base_r7", CubeListBuilder.create().texOffs(14, 52).addBox(-1.0F, 26.4872F, 7.1776F, 2.0F, 8.0F, 3.0F, new CubeDeformation(-0.05F))
                .texOffs(0, 4).addBox(-0.5F, 34.3872F, 7.1776F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, -27.8879F, -4.2762F, -0.1484F, 0.0F, 0.0F));

        PartDefinition Base_r8 = Joint4.addOrReplaceChild("Base_r8", CubeListBuilder.create().texOffs(63, 52).addBox(-0.5F, -29.2462F, 19.6296F, 1.0F, 2.0F, 3.0F, CubeDeformation.NONE)
                .texOffs(20, 63).addBox(-0.5F, -27.2462F, 22.6296F, 1.0F, 7.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(53, 19).addBox(-1.0F, -27.2462F, 19.6296F, 2.0F, 8.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -27.8879F, -4.2762F, -2.1555F, 0.0F, 0.0F));

        PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(40, 62).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.5F, -2.0F, -7.0F, 0.0F, -0.2182F, 0.0F));

        PartDefinition Snout_r2 = Head.addOrReplaceChild("Snout_r2", CubeListBuilder.create().texOffs(50, 62).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.5F, -2.0F, -7.0F, 0.0F, 0.2182F, 0.0F));

        PartDefinition Snout_r3 = Head.addOrReplaceChild("Snout_r3", CubeListBuilder.create().texOffs(44, 7).addBox(-1.5F, -27.0F, -6.0F, 3.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(60, 17).addBox(-1.5F, -29.0F, -7.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 27.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Head_r1 = Head.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(0, 14).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Fins = Head.addOrReplaceChild("Fins", CubeListBuilder.create(), PartPose.offset(0.0F, -7.5F, 0.0F));

        PartDefinition leftear_r1 = Fins.addOrReplaceChild("leftear_r1", CubeListBuilder.create().texOffs(56, 45).addBox(-6.15F, -28.9F, -23.8F, 2.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 33.5F, 0.0F, -0.7418F, 0.2182F, 0.2618F));

        PartDefinition leftear_r2 = Fins.addOrReplaceChild("leftear_r2", CubeListBuilder.create().texOffs(60, 27).addBox(-5.0F, -12.25F, -29.05F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 33.5F, 0.0F, -1.2654F, 0.3927F, 0.2618F));

        PartDefinition rightear_r1 = Fins.addOrReplaceChild("rightear_r1", CubeListBuilder.create().texOffs(0, 62).addBox(3.0F, -12.25F, -29.05F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 33.5F, 0.0F, -1.2654F, -0.3927F, -0.2618F));

        PartDefinition rightear_r2 = Fins.addOrReplaceChild("rightear_r2", CubeListBuilder.create().texOffs(56, 55).addBox(4.15F, -28.9F, -23.8F, 2.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 33.5F, 0.0F, -0.7418F, -0.2182F, -0.2618F));

        PartDefinition Head_r2 = Fins.addOrReplaceChild("Head_r2", CubeListBuilder.create().texOffs(10, 63).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.0F, 3.0F, 0.0873F, 0.0F, 0.0F));

        PartDefinition Head_r3 = Fins.addOrReplaceChild("Head_r3", CubeListBuilder.create().texOffs(36, 46).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, 3.0F, 0.2618F, 0.0F, 0.0F));

        PartDefinition Head_r4 = Fins.addOrReplaceChild("Head_r4", CubeListBuilder.create().texOffs(36, 7).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.5F, -1.0F, 0.6545F, 0.0F, 0.0F));

        PartDefinition Head_r5 = Fins.addOrReplaceChild("Head_r5", CubeListBuilder.create().texOffs(0, 14).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 6.5F, -3.5F, 1.0472F, 0.0F, -2.0071F));

        PartDefinition Head_r6 = Fins.addOrReplaceChild("Head_r6", CubeListBuilder.create().texOffs(63, 22).addBox(0.0F, 0.0F, -1.0F, 1.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 6.5F, -3.5F, 1.0472F, 0.0F, 2.0071F));

        PartDefinition Head_r7 = Fins.addOrReplaceChild("Head_r7", CubeListBuilder.create().texOffs(57, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.5F, -3.5F, 1.0472F, 0.0F, 0.0F));

        PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition LeftArm_r1 = Torso.addOrReplaceChild("LeftArm_r1", CubeListBuilder.create().texOffs(23, 61).addBox(-2.0F, 1.0F, 0.0F, 4.0F, 9.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.1745F));

        PartDefinition RightArm_r1 = Torso.addOrReplaceChild("RightArm_r1", CubeListBuilder.create().texOffs(31, 61).addBox(-2.0F, 1.0F, 0.0F, 4.0F, 9.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.0F, 2.0F, 0.0F, 0.0F, 0.0F, -0.1745F));

        PartDefinition Waist_r1 = Torso.addOrReplaceChild("Waist_r1", CubeListBuilder.create().texOffs(29, 0).addBox(-4.5F, -2.0F, -2.5F, 9.0F, 2.0F, 5.0F, new CubeDeformation(0.4F))
                .texOffs(50, 36).addBox(-4.0F, -3.0F, -2.0F, 8.0F, 1.0F, 4.0F, new CubeDeformation(0.1F))
                .texOffs(26, 36).addBox(-4.0F, -8.0F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(-0.3F))
                .texOffs(0, 43).addBox(-4.0F, -12.0F, -2.0F, 8.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Plantoids = Torso.addOrReplaceChild("Plantoids", CubeListBuilder.create(), PartPose.offset(0.0F, 0.25F, 0.0F));

        PartDefinition Center_r1 = Plantoids.addOrReplaceChild("Center_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -11.5F, -2.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.0F, 13.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftPlantoid_r1 = Plantoids.addOrReplaceChild("LeftPlantoid_r1", CubeListBuilder.create().texOffs(46, 36).addBox(0.75F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.4F))
                .texOffs(52, 0).addBox(-3.75F, -1.5F, -0.5F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.4F)), PartPose.offsetAndRotation(0.0F, 3.0F, -2.0F, -0.1047F, 0.0F, 0.0F));

        PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create(), PartPose.offset(-5.0F, 0.0F, 0.0F));

        PartDefinition Finger_r1 = RightArm.addOrReplaceChild("Finger_r1", CubeListBuilder.create().texOffs(0, 19).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F))
                .texOffs(24, 16).addBox(6.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(-6.0F, 1.75F, -3.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Finger_r2 = RightArm.addOrReplaceChild("Finger_r2", CubeListBuilder.create().texOffs(4, 19).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(-6.0F, 1.75F, -1.5F, 0.0F, 0.0F, 0.0F));

        PartDefinition Finger_r3 = RightArm.addOrReplaceChild("Finger_r3", CubeListBuilder.create().texOffs(24, 14).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(-6.0F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition RightArm_r2 = RightArm.addOrReplaceChild("RightArm_r2", CubeListBuilder.create().texOffs(24, 46).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create(), PartPose.offset(5.0F, 0.0F, 0.0F));

        PartDefinition Finger_r4 = LeftArm.addOrReplaceChild("Finger_r4", CubeListBuilder.create().texOffs(24, 18).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F))
                .texOffs(29, 0).addBox(6.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(-4.0F, 1.75F, -3.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition Finger_r5 = LeftArm.addOrReplaceChild("Finger_r5", CubeListBuilder.create().texOffs(29, 2).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(-1.0F, 1.75F, -1.5F, 0.0F, 0.0F, 0.0F));

        PartDefinition Finger_r6 = LeftArm.addOrReplaceChild("Finger_r6", CubeListBuilder.create().texOffs(0, 30).addBox(3.0F, 9.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(-1.0F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition LeftArm_r2 = LeftArm.addOrReplaceChild("LeftArm_r2", CubeListBuilder.create().texOffs(40, 46).addBox(0.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void prepareMobModel(LatexMantaRayFemale p_102861_, float p_102862_, float p_102863_, float p_102864_) {
        this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
    }

    public void setupHand() {
        animator.setupHand();
    }

    @Override
    public void setupAnim(@NotNull LatexMantaRayFemale entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
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
    public LatexAnimator<LatexMantaRayFemale, LatexMantaRayFemaleModel> getAnimator() {
        return animator;
    }

    public static class Remodel extends LatexHumanoidModel.LatexRemodel<LatexMantaRayFemale, Remodel> {
        private final ModelPart RightArm;
        private final ModelPart LeftArm;
        private final ModelPart Head;
        private final ModelPart Torso;
        private final ModelPart Abdomen;
        private final ModelPart LowerAbdomen;
        private final ModelPart Tail;
        private final LatexAnimator<LatexMantaRayFemale, Remodel> animator;

        public Remodel(ModelPart root) {
            super(root);
            this.Head = root.getChild("Head");
            this.Torso = root.getChild("Torso");
            this.Abdomen = root.getChild("Abdomen");
            this.LowerAbdomen = Abdomen.getChild("LowerAbdomen");
            this.Tail = LowerAbdomen.getChild("Tail");
            this.RightArm = root.getChild("RightArm");
            this.LeftArm = root.getChild("LeftArm");
            animator = LatexAnimator.of(this).addPreset(AnimatorPresets.snakeLike(Head, Torso, LeftArm, RightArm, Abdomen, LowerAbdomen, Tail, List.of(
                    Tail.getChild("Joint"),
                    Tail.getChild("Joint").getChild("Joint2"),
                    Tail.getChild("Joint").getChild("Joint2").getChild("Joint3"),
                    Tail.getChild("Joint").getChild("Joint2").getChild("Joint3").getChild("Joint4")
            )));
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();

            PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 15).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(63, 31).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.5F, -3.01F, -7.0F, 0.0F, -0.2182F, 0.0F));

            PartDefinition Snout_r2 = Head.addOrReplaceChild("Snout_r2", CubeListBuilder.create().texOffs(16, 64).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, -3.01F, -7.0F, 0.0F, 0.2182F, 0.0F));

            PartDefinition Snout_r3 = Head.addOrReplaceChild("Snout_r3", CubeListBuilder.create().texOffs(26, 37).addBox(-1.5F, -27.0F, -6.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(51, 0).addBox(-1.5F, -29.0F, -7.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 26.0F, 0.0F, 0.0F, 0.0F, 0.0F));

            PartDefinition Fins = Head.addOrReplaceChild("Fins", CubeListBuilder.create(), PartPose.offset(0.0F, -9.5F, 0.0F));

            PartDefinition leftear_r1 = Fins.addOrReplaceChild("leftear_r1", CubeListBuilder.create().texOffs(56, 34).addBox(-6.15F, -28.9F, -23.8F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 34.5F, 0.0F, -0.7418F, 0.2182F, 0.2618F));

            PartDefinition leftear_r2 = Fins.addOrReplaceChild("leftear_r2", CubeListBuilder.create().texOffs(61, 13).addBox(-5.0F, -12.25F, -29.05F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 34.5F, 0.0F, -1.2654F, 0.3927F, 0.2618F));

            PartDefinition rightear_r1 = Fins.addOrReplaceChild("rightear_r1", CubeListBuilder.create().texOffs(61, 63).addBox(3.0F, -12.25F, -29.05F, 2.0F, 6.0F, 3.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 34.5F, 0.0F, -1.2654F, -0.3927F, -0.2618F));

            PartDefinition rightear_r2 = Fins.addOrReplaceChild("rightear_r2", CubeListBuilder.create().texOffs(54, 56).addBox(4.15F, -28.9F, -23.8F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 34.5F, 0.0F, -0.7418F, -0.2182F, -0.2618F));

            PartDefinition Head_r1 = Fins.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(63, 0).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 3.0F, 0.0873F, 0.0F, 0.0F));

            PartDefinition Head_r2 = Fins.addOrReplaceChild("Head_r2", CubeListBuilder.create().texOffs(34, 52).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, 3.0F, 0.2618F, 0.0F, 0.0F));

            PartDefinition Head_r3 = Fins.addOrReplaceChild("Head_r3", CubeListBuilder.create().texOffs(20, 46).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.5F, -1.0F, 0.6545F, 0.0F, 0.0F));

            PartDefinition Head_r4 = Fins.addOrReplaceChild("Head_r4", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, 0.0F, -1.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 7.5F, -3.5F, 1.0472F, 0.0F, -2.0071F));

            PartDefinition Head_r5 = Fins.addOrReplaceChild("Head_r5", CubeListBuilder.create().texOffs(0, 15).addBox(0.0F, 0.0F, -1.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 7.5F, -3.5F, 1.0472F, 0.0F, 2.0071F));

            PartDefinition Head_r6 = Fins.addOrReplaceChild("Head_r6", CubeListBuilder.create().texOffs(62, 40).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.5F, -3.5F, 1.0472F, 0.0F, 0.0F));

            PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(32, 37).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 11.0F, 4.0F, new CubeDeformation(0.0F))
                    .texOffs(28, 0).addBox(-4.5F, 11.0F, -2.5F, 9.0F, 3.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

            PartDefinition RightArm_r1 = Torso.addOrReplaceChild("RightArm_r1", CubeListBuilder.create().texOffs(10, 70).addBox(-5.25F, 1.0F, 0.0F, 4.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, 0.5F, 0.0F, 0.0F, 0.0F, -0.5672F));

            PartDefinition LeftArm_r1 = Torso.addOrReplaceChild("LeftArm_r1", CubeListBuilder.create().texOffs(0, 70).addBox(1.25F, 1.0F, 0.0F, 4.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, 0.5F, 0.0F, 0.0F, 0.0F, 0.5672F));

            PartDefinition Breasts = Torso.addOrReplaceChild("Breasts", CubeListBuilder.create().texOffs(12, 31).addBox(-4.25F, -2.25F, -1.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                    .texOffs(0, 31).addBox(0.25F, -2.25F, -1.0F, 4.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.5F, -2.0F, -0.4363F, 0.0F, 0.0F));

            PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(22, 52).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 2.0F, 0.0F));

            PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(38, 52).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 2.0F, 0.0F));

            PartDefinition Abdomen = partdefinition.addOrReplaceChild("Abdomen", CubeListBuilder.create().texOffs(0, 37).addBox(-5.0F, 0.0F, -3.0F, 10.0F, 3.0F, 6.0F, new CubeDeformation(-0.05F))
                    .texOffs(25, 24).addBox(-5.0F, 1.0F, -3.5F, 10.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, 0.0F));

            PartDefinition LowerAbdomen = Abdomen.addOrReplaceChild("LowerAbdomen", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 0.0F, -4.0F, 10.0F, 7.0F, 8.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, 5.0F, 0.0F));

            PartDefinition Tail = LowerAbdomen.addOrReplaceChild("Tail", CubeListBuilder.create().texOffs(29, 8).addBox(-4.5F, 0.0F, -3.5F, 9.0F, 7.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 0.0F));

            PartDefinition Joint = Tail.addOrReplaceChild("Joint", CubeListBuilder.create().texOffs(0, 46).addBox(-3.5F, 2.0F, -3.0F, 7.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 0.0F));

            PartDefinition Joint2 = Joint.addOrReplaceChild("Joint2", CubeListBuilder.create().texOffs(51, 47).addBox(-3.0F, 2.0F, -2.5F, 6.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

            PartDefinition Joint3 = Joint2.addOrReplaceChild("Joint3", CubeListBuilder.create().texOffs(52, 22).addBox(-2.5F, 2.0F, -2.0F, 5.0F, 5.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 4.0F, 0.0F));

            PartDefinition Joint4 = Joint3.addOrReplaceChild("Joint4", CubeListBuilder.create().texOffs(54, 5).addBox(-2.0F, 2.0F, -1.5F, 4.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 5.0F, 0.0F));

            PartDefinition Base_r1 = Joint4.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(0, 56).addBox(-1.0F, 26.4872F, 7.1776F, 2.0F, 8.0F, 3.0F, new CubeDeformation(-0.05F))
                    .texOffs(0, 5).addBox(-0.5F, 34.3872F, 7.1776F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, -22.0025F, -4.6971F, -0.1484F, 0.0F, 0.0F));

            PartDefinition Base_r2 = Joint4.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(64, 56).addBox(-0.5F, -29.2462F, 19.6296F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                    .texOffs(24, 15).addBox(-0.5F, -27.2462F, 22.6296F, 1.0F, 7.0F, 1.0F, new CubeDeformation(0.0F))
                    .texOffs(10, 56).addBox(-1.0F, -27.2462F, 19.6296F, 2.0F, 8.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -22.0025F, -4.6971F, -2.1555F, 0.0F, 0.0F));

            return LayerDefinition.create(meshdefinition, 128, 128);
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
        public LatexAnimator<LatexMantaRayFemale, Remodel> getAnimator() {
            return animator;
        }
    }
}