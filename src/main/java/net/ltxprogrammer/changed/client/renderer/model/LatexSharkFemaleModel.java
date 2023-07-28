package net.ltxprogrammer.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.beast.LatexSharkFemale;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class LatexSharkFemaleModel extends LatexHumanoidModel<LatexSharkFemale> implements LatexHumanoidModelInterface<LatexSharkFemale, LatexSharkFemaleModel> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_shark_female"), "main");
	private final ModelPart RightLeg;
	private final ModelPart LeftLeg;
	private final ModelPart RightArm;
	private final ModelPart LeftArm;
	private final ModelPart Head;
	private final ModelPart Torso;
	private final ModelPart Tail;
	private final LatexAnimator<LatexSharkFemale, LatexSharkFemaleModel> animator;

	public LatexSharkFemaleModel(ModelPart root) {
		super(root);
		this.RightLeg = root.getChild("RightLeg");
		this.LeftLeg = root.getChild("LeftLeg");
		this.Head = root.getChild("Head");
		this.Torso = root.getChild("Torso");
		this.Tail = Torso.getChild("Tail");
		this.RightArm = root.getChild("RightArm");
		this.LeftArm = root.getChild("LeftArm");
		animator = LatexAnimator.of(this).addPreset(AnimatorPresets.sharkLike(Head, Torso, LeftArm, RightArm, Tail, List.of(), LeftLeg, RightLeg))
				.hipOffset(-5.0f).legLength(16.0f).armLength(16.0f).torsoLength(18.0f);
	}


	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.75F, 7.0F, 0.0F));

		PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, -0.995F, 0.01F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
				.texOffs(0, 18).addBox(-2.5F, -0.995F, 0.01F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
				.texOffs(24, 0).addBox(-1.0F, -0.995F, 0.01F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(1.8F, 15.875F, -3.225F, -0.6109F, 0.0F, 0.0F));

		PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(24, 2).addBox(-2.5F, -3.0F, -1.49F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
				.texOffs(16, 27).addBox(-1.0F, -3.0F, -1.49F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
				.texOffs(28, 20).addBox(-4.0F, -3.0F, -1.49F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(1.8F, 18.95F, -1.75F, 0.0F, 0.0F, 0.0F));

		PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(56, 40).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-0.2F, 7.55F, -1.4F, 0.5847F, 0.0F, 0.0F));

		PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(68, 29).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.06F)), PartPose.offsetAndRotation(-0.2F, 17.2F, -1.25F, -0.5236F, 0.0F, 0.0F));

		PartDefinition RightFoot_r1 = RightLeg.addOrReplaceChild("RightFoot_r1", CubeListBuilder.create().texOffs(68, 38).addBox(-4.0F, -4.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0605F)), PartPose.offsetAndRotation(1.8F, 18.95F, -0.75F, 0.0F, 0.0F, 0.0F));

		PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(46, 63).addBox(-2.0F, 1.8F, -2.1F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(-0.2F, 1.5F, 0.1F, -0.2356F, 0.0F, 0.0F));

		PartDefinition RightUpperLeg_r2 = RightLeg.addOrReplaceChild("RightUpperLeg_r2", CubeListBuilder.create().texOffs(48, 31).addBox(-3.0F, -2.0F, -2.0F, 5.0F, 4.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.3F, 1.7F, -0.6F, -0.2182F, 0.0F, 0.0F));

		PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.75F, 7.0F, 0.0F));

		PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(32, 20).addBox(-2.5F, -0.995F, 0.01F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
				.texOffs(16, 33).addBox(-1.0F, -0.995F, 0.01F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
				.texOffs(0, 38).addBox(-4.0F, -0.995F, 0.01F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(2.2F, 15.875F, -3.225F, -0.6109F, 0.0F, 0.0F));

		PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(20, 33).addBox(-1.0F, -3.0F, -1.49F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
				.texOffs(0, 36).addBox(-2.5F, -3.0F, -1.49F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
				.texOffs(36, 20).addBox(-4.0F, -3.0F, -1.49F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(2.2F, 18.95F, -1.75F, 0.0F, 0.0F, 0.0F));

		PartDefinition LeftFoot_r1 = LeftLeg.addOrReplaceChild("LeftFoot_r1", CubeListBuilder.create().texOffs(0, 69).addBox(-4.0F, -4.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0605F)), PartPose.offsetAndRotation(2.2F, 18.95F, -0.75F, 0.0F, 0.0F, 0.0F));

		PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(69, 48).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.06F)), PartPose.offsetAndRotation(0.2F, 17.2F, -1.25F, -0.5236F, 0.0F, 0.0F));

		PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(62, 59).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.2F, 7.55F, -1.4F, 0.5847F, 0.0F, 0.0F));

		PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(12, 64).addBox(-2.0F, 1.8F, -2.1F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.2F, 1.5F, 0.1F, -0.2356F, 0.0F, 0.0F));

		PartDefinition LeftUpperLeg_r2 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r2", CubeListBuilder.create().texOffs(49, 2).addBox(-3.0F, -2.0F, -2.0F, 5.0F, 4.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.7F, 1.7F, -0.6F, -0.2182F, 0.0F, 0.0F));

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(16, 27).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.5F, -2.7F, -6.975F, 0.0F, -0.2182F, 0.0F));

		PartDefinition Snout_r2 = Head.addOrReplaceChild("Snout_r2", CubeListBuilder.create().texOffs(6, 78).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.5F, -2.7F, -6.975F, 0.0F, 0.2182F, 0.0F));

		PartDefinition Snout_r3 = Head.addOrReplaceChild("Snout_r3", CubeListBuilder.create().texOffs(32, 7).addBox(-1.5F, -27.0F, -6.0F, 3.0F, 1.0F, 2.0F, CubeDeformation.NONE)
				.texOffs(15, 36).addBox(-1.5F, -29.0F, -7.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 26.3F, 0.0F, 0.0F, 0.0F, 0.0F));

		PartDefinition Head_r1 = Head.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.3F, 0.0F, 0.0F, 0.0F, 0.0F));

		PartDefinition Fins = Head.addOrReplaceChild("Fins", CubeListBuilder.create(), PartPose.offset(0.0F, 26.2F, 0.0F));

		PartDefinition Base_r1 = Fins.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(74, 7).addBox(-0.25F, -1.0F, 0.0F, 5.0F, 3.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-4.0F, -32.25F, -1.0F, -1.1342F, -0.8439F, -2.3458F));

		PartDefinition Base_r2 = Fins.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(74, 0).addBox(-0.25F, -2.0F, 0.0F, 5.0F, 3.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(4.0F, -32.25F, -1.0F, 1.1342F, -0.8439F, -0.7958F));

		PartDefinition Base_r3 = Fins.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(79, 26).addBox(-2.1F, -1.25F, -0.5F, 3.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.5F, -27.55F, -1.6F, 0.5236F, 0.9599F, 0.0F));

		PartDefinition Base_r4 = Fins.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(72, 44).addBox(-2.2F, -1.25F, -0.5F, 3.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.5F, -27.975F, -2.0F, -0.5236F, 0.9599F, -3.1416F));

		PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create(), PartPose.offset(0.0F, -0.7F, 0.0F));

		PartDefinition Base_r5 = Hair.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(54, 14).addBox(-4.5F, -8.0F, -5.0F, 9.0F, 2.0F, 1.0F, CubeDeformation.NONE)
				.texOffs(70, 79).addBox(-4.5F, -6.0F, -5.0F, 2.0F, 8.0F, 1.0F, CubeDeformation.NONE)
				.texOffs(0, 75).addBox(2.0F, -8.75F, 3.75F, 2.0F, 12.0F, 1.0F, CubeDeformation.NONE)
				.texOffs(28, 64).addBox(-2.0F, -9.0F, 4.0F, 4.0F, 13.0F, 1.0F, CubeDeformation.NONE)
				.texOffs(20, 39).addBox(-5.0F, -8.0F, -4.0F, 1.0F, 4.0F, 8.0F, CubeDeformation.NONE)
				.texOffs(38, 39).addBox(4.0F, -8.0F, -4.0F, 1.0F, 4.0F, 8.0F, CubeDeformation.NONE)
				.texOffs(42, 12).addBox(-4.0F, -8.75F, -4.0F, 2.0F, 1.0F, 8.0F, CubeDeformation.NONE)
				.texOffs(45, 22).addBox(2.0F, -8.75F, -4.0F, 2.0F, 1.0F, 8.0F, CubeDeformation.NONE)
				.texOffs(0, 27).addBox(-2.0F, -9.0F, -4.0F, 4.0F, 1.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));

		PartDefinition Base_r6 = Hair.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(50, 72).addBox(-4.0F, -9.75F, 3.75F, 2.0F, 12.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 2.0F, 0.0F, 0.0F, 0.0F, 0.0F));

		PartDefinition Base_r7 = Hair.addOrReplaceChild("Base_r7", CubeListBuilder.create().texOffs(71, 70).addBox(-1.0F, -2.5F, -2.0F, 2.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.25F, -1.7F, 2.0F, 0.0F, 0.0F, -0.1047F));

		PartDefinition Base_r8 = Hair.addOrReplaceChild("Base_r8", CubeListBuilder.create().texOffs(12, 73).addBox(-1.0F, -2.5F, -2.0F, 2.0F, 5.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.25F, -1.7F, 2.0F, 0.0F, 0.0F, 0.1047F));

		PartDefinition Base_r9 = Hair.addOrReplaceChild("Base_r9", CubeListBuilder.create().texOffs(54, 11).addBox(-5.0F, -0.75F, 2.0F, 9.0F, 1.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.5F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));

		PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, -8.0F, 0.0F));

		PartDefinition BackFin_r1 = Torso.addOrReplaceChild("BackFin_r1", CubeListBuilder.create().texOffs(38, 68).addBox(-1.0F, 0.0F, -4.0F, 2.0F, 6.0F, 4.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 3.45F, 2.1F, 0.5236F, 0.0F, 0.0F));

		PartDefinition Torso_r1 = Torso.addOrReplaceChild("Torso_r1", CubeListBuilder.create().texOffs(24, 0).addBox(-5.0F, -1.5F, -2.5F, 10.0F, 2.0F, 5.0F, new CubeDeformation(-0.35F)), PartPose.offsetAndRotation(0.0F, 16.2F, 0.2F, 0.0F, 0.0F, 0.0F));

		PartDefinition Torso_r2 = Torso.addOrReplaceChild("Torso_r2", CubeListBuilder.create().texOffs(23, 22).addBox(-5.0F, -1.5F, -2.5F, 10.0F, 3.0F, 5.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 14.2F, 0.2F, 0.0F, 0.0F, 0.0F));

		PartDefinition Torso_r3 = Torso.addOrReplaceChild("Torso_r3", CubeListBuilder.create().texOffs(24, 30).addBox(-4.0F, -2.5F, -2.0F, 8.0F, 5.0F, 4.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 11.9F, 0.1F, 0.0F, 0.0F, 0.0F));

		PartDefinition Torso_r4 = Torso.addOrReplaceChild("Torso_r4", CubeListBuilder.create().texOffs(0, 48).addBox(-4.0F, -2.0F, -1.5F, 8.0F, 4.0F, 3.0F, new CubeDeformation(0.45F)), PartPose.offsetAndRotation(0.0F, 7.6F, 0.1F, 0.0F, 0.0F, 0.0F));

		PartDefinition Torso_r5 = Torso.addOrReplaceChild("Torso_r5", CubeListBuilder.create().texOffs(0, 16).addBox(-5.0F, -3.5F, -2.0F, 10.0F, 7.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 3.9F, 0.0F, 0.0F, 0.0F, 0.0F));

		PartDefinition watermelons = Torso.addOrReplaceChild("watermelons", CubeListBuilder.create(), PartPose.offset(-0.2F, 30.05F, 0.35F));

		PartDefinition Center_r1 = watermelons.addOrReplaceChild("Center_r1", CubeListBuilder.create().texOffs(30, 39).addBox(-1.0F, -12.5F, -2.5F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(0.2F, -16.05F, -0.35F, -0.0175F, 0.0F, 0.0F));

		PartDefinition watermelon_r1 = watermelons.addOrReplaceChild("watermelon_r1", CubeListBuilder.create().texOffs(74, 12).addBox(-2.5F, -2.5F, -0.5F, 5.0F, 5.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.65F, -26.6F, -3.0F, -0.1571F, 0.0F, 0.0F));

		PartDefinition watermelon_r2 = watermelons.addOrReplaceChild("watermelon_r2", CubeListBuilder.create().texOffs(74, 19).addBox(-2.5F, -2.5F, -0.5F, 5.0F, 5.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.05F, -26.6F, -3.0F, -0.1571F, 0.0F, 0.0F));

		PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 13.0F, 0.0F));

		PartDefinition TailFin_r1 = Tail.addOrReplaceChild("TailFin_r1", CubeListBuilder.create().texOffs(40, 78).addBox(0.0F, 0.0F, -0.75F, 1.0F, 7.0F, 3.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-0.5F, 3.45F, 6.8F, 1.5272F, 0.0F, 0.0F));

		Tail.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(16, 29).addBox(-0.5F, 5.3462F, -1.8296F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F))
				.texOffs(24, 78).addBox(-0.5F, -2.5538F, -1.8296F, 1.0F, 8.0F, 3.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 8.45F, 25.3F, 1.309F, 0.0F, 0.0F));

		Tail.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(20, 41).addBox(-0.5F, -8.1668F, -2.1179F, 1.0F, 2.0F, 3.0F, CubeDeformation.NONE)
				.texOffs(44, 7).addBox(-0.5F, -6.1668F, 0.8821F, 1.0F, 7.0F, 1.0F, CubeDeformation.NONE)
				.texOffs(32, 78).addBox(-0.5F, -6.1668F, -2.1179F, 1.0F, 8.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 7.575F, 25.2F, -0.6981F, 0.0F, 0.0F));

		Tail.addOrReplaceChild("Base_r7", CubeListBuilder.create().texOffs(48, 40).addBox(-1.0F, -0.15F, -0.8F, 2.0F, 5.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 8.8F, 21.3F, 1.5708F, 0.0F, 0.0F));

		Tail.addOrReplaceChild("Base_r8", CubeListBuilder.create().texOffs(59, 70).addBox(-1.5F, -1.3563F, -1.8F, 3.0F, 7.0F, 3.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 7.85F, 15.8F, 1.4835F, 0.0F, 0.0F));

		Tail.addOrReplaceChild("Base_r9", CubeListBuilder.create().texOffs(0, 36).addBox(-3.0F, -1.0F, -1.0F, 5.0F, 7.0F, 5.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.5F, 2.9F, 2.9F, 0.8727F, 0.0F, 0.0F));

		Tail.addOrReplaceChild("Base_r10", CubeListBuilder.create().texOffs(26, 10).addBox(-4.0F, 2.0F, -1.0F, 6.0F, 4.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, 0.6F, -2.6F, 1.0908F, 0.0F, 0.0F));

		Tail.addOrReplaceChild("Base_r11", CubeListBuilder.create().texOffs(58, 17).addBox(-2.0F, 1.0F, -1.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0F, 5.9F, 6.3F, 1.2217F, 0.0F, 0.0F));

		PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create(), PartPose.offset(-5.0F, -6.0F, 0.0F));

		PartDefinition Finger_r1 = RightArm.addOrReplaceChild("Finger_r1", CubeListBuilder.create().texOffs(40, 7).addBox(2.85F, 14.1F, 0.85F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
				.texOffs(40, 43).addBox(6.15F, 14.1F, 0.85F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-7.1F, 0.85F, -3.0F, 0.0F, 0.0F, 0.0F));

		PartDefinition Finger_r2 = RightArm.addOrReplaceChild("Finger_r2", CubeListBuilder.create().texOffs(40, 39).addBox(2.8499F, 14.1F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-7.1F, 0.85F, -1.5F, 0.0F, 0.0F, 0.0F));

		PartDefinition Finger_r3 = RightArm.addOrReplaceChild("Finger_r3", CubeListBuilder.create().texOffs(40, 41).addBox(2.85F, 14.1F, 1.15F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-7.1F, 0.85F, 0.0F, 0.0F, 0.0F, 0.0F));

		PartDefinition Spike_r1 = RightArm.addOrReplaceChild("Spike_r1", CubeListBuilder.create().texOffs(79, 64).addBox(-1.975F, -2.5F, -0.5F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-3.4F, 5.175F, 1.2F, -0.48F, 0.6981F, -0.7069F));

		PartDefinition RightArm_r1 = RightArm.addOrReplaceChild("RightArm_r1", CubeListBuilder.create().texOffs(50, 51).addBox(-4.1F, 6.5F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.25F))
				.texOffs(18, 51).addBox(-4.1F, -1.9F, -2.0F, 4.0F, 9.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.2F, 0.0F, 0.0F, 0.0F, 0.0F));

		PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create(), PartPose.offset(5.0F, -6.0F, 0.0F));

		PartDefinition Finger_r4 = LeftArm.addOrReplaceChild("Finger_r4", CubeListBuilder.create().texOffs(44, 31).addBox(-0.55F, -0.6F, -0.55F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.5F, 15.75F, -1.6F, 0.0F, 0.0F, 0.0F));

		PartDefinition Finger_r5 = LeftArm.addOrReplaceChild("Finger_r5", CubeListBuilder.create().texOffs(47, 32).addBox(2.95F, 13.9F, 0.95F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.3F, 1.25F, -3.1F, 0.0F, 0.0F, 0.0F));

		PartDefinition Finger_r6 = LeftArm.addOrReplaceChild("Finger_r6", CubeListBuilder.create().texOffs(48, 21).addBox(2.9501F, 13.9F, 1.025F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.3F, 1.25F, -1.5F, 0.0F, 0.0F, 0.0F));

		PartDefinition Spike_r2 = LeftArm.addOrReplaceChild("Spike_r2", CubeListBuilder.create().texOffs(78, 57).addBox(-2.5F, -2.5F, -0.5F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(3.8F, 5.4F, 1.6F, -0.48F, -0.6981F, 0.7069F));

		PartDefinition Finger_r7 = LeftArm.addOrReplaceChild("Finger_r7", CubeListBuilder.create().texOffs(48, 23).addBox(2.95F, 13.9F, 1.05F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.3F, 1.25F, 0.1F, 0.0F, 0.0F, 0.0F));

		PartDefinition LeftArm_r1 = LeftArm.addOrReplaceChild("LeftArm_r1", CubeListBuilder.create().texOffs(0, 55).addBox(0.0F, 6.6F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.25F))
				.texOffs(34, 51).addBox(0.0F, -2.0F, -2.0F, 4.0F, 9.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.1F, 0.3F, 0.0F, 0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}


	@Override
	public void prepareMobModel(LatexSharkFemale p_102861_, float p_102862_, float p_102863_, float p_102864_) {
		this.prepareMobModel(animator, p_102861_, p_102862_, p_102863_, p_102864_);
	}
	public PoseStack getPlacementCorrectors(CorrectorType type) {
		PoseStack corrector = LatexHumanoidModelInterface.super.getPlacementCorrectors(type);
		if (type.isArm())
			corrector.translate(0.0f, 7.0f / 14.0f, 0.0f);
		return corrector;
	}

	public void setupHand() {
		animator.setupHand();
	}

	@Override
	public LatexAnimator<LatexSharkFemale, LatexSharkFemaleModel> getAnimator() {
		return animator;
	}

	@Override
	public void setupAnim(LatexSharkFemale entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		animator.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
	}

	public ModelPart getArm(HumanoidArm p_102852_) {
		return p_102852_ == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
	}

	public ModelPart getHead() {
		return this.Head;
	}

	public ModelPart getTorso() {
		return Torso;
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

	public static class Remodel extends LatexHumanoidModel.LatexRemodel<LatexSharkFemale, Remodel> {
		private final ModelPart RightLeg;
		private final ModelPart LeftLeg;
		private final ModelPart RightArm;
		private final ModelPart LeftArm;
		private final ModelPart Head;
		private final ModelPart Torso;
		private final ModelPart Tail;
		private final LatexAnimator<LatexSharkFemale, Remodel> animator;

		public Remodel(ModelPart root) {
			super(root);
			this.RightLeg = root.getChild("RightLeg");
			this.LeftLeg = root.getChild("LeftLeg");
			this.Head = root.getChild("Head");
			this.Torso = root.getChild("Torso");
			this.Tail = Torso.getChild("Tail");
			this.RightArm = root.getChild("RightArm");
			this.LeftArm = root.getChild("LeftArm");
			animator = LatexAnimator.of(this).addPreset(AnimatorPresets.sharkLike(Head, Torso, LeftArm, RightArm, Tail, List.of(), LeftLeg, RightLeg))
					.hipOffset(-2.0f).legLength(14.0f).armLength(14.0f).torsoLength(14.0f);
			animator.torsoWidth = 6.0f;
		}


		public static LayerDefinition createBodyLayer() {
			MeshDefinition meshdefinition = new MeshDefinition();
			PartDefinition partdefinition = meshdefinition.getRoot();

			PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 19).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -4.0F, 0.0F));

			PartDefinition Base_r1 = Head.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(57, 18).addBox(-0.25F, -2.0F, 0.0F, 5.0F, 3.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(3.75F, -5.625F, -1.0F, 1.1342F, -0.8439F, -0.7958F));

			PartDefinition Base_r2 = Head.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(57, 43).addBox(-0.25F, -1.0F, 0.0F, 5.0F, 3.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-3.75F, -5.625F, -1.0F, -1.1342F, -0.8439F, -2.3458F));

			PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(62, 23).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.5F, -3.01F, -7.0F, 0.0F, -0.2182F, 0.0F));

			PartDefinition Snout_r2 = Head.addOrReplaceChild("Snout_r2", CubeListBuilder.create().texOffs(41, 62).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.5F, -3.01F, -7.0F, 0.0F, 0.2182F, 0.0F));

			PartDefinition Snout_r3 = Head.addOrReplaceChild("Snout_r3", CubeListBuilder.create().texOffs(32, 27).addBox(-1.5F, -27.0F, -6.0F, 3.0F, 1.0F, 2.0F, CubeDeformation.NONE)
					.texOffs(15, 35).addBox(-1.5F, -29.0F, -7.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 26.0F, 0.0F, 0.0F, 0.0F, 0.0F));

			PartDefinition Base_r3 = Head.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(62, 0).addBox(-2.1808F, -1.0F, -0.5736F, 3.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.5F, -1.4F, -1.0F, -0.5236F, 0.9599F, -3.1416F));

			PartDefinition Base_r4 = Head.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(25, 0).addBox(-2.1808F, -1.0F, -0.5736F, 3.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.5F, -1.4F, -1.0F, 0.5236F, 0.9599F, 0.0F));

			PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, 0.0F, -2.5F, 10.0F, 14.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -4.0F, 0.0F));

			PartDefinition BackFin_r1 = Torso.addOrReplaceChild("BackFin_r1", CubeListBuilder.create().texOffs(45, 0).addBox(-1.0F, 2.0F, -3.0F, 2.0F, 3.0F, 1.0F, CubeDeformation.NONE)
					.texOffs(33, 62).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 7.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 2.5F, 2.5F, 0.5236F, 0.0F, 0.0F));

			PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 12.75F, 0.25F));

			PartDefinition Base_r5 = Tail.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(0, 35).addBox(-0.5F, 5.3462F, -1.8296F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F))
					.texOffs(25, 59).addBox(-0.5F, -2.5538F, -1.8296F, 1.0F, 8.0F, 3.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 5.25F, 17.0F, 1.1345F, 0.0F, 0.0F));

			PartDefinition Base_r6 = Tail.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(57, 64).addBox(-0.5F, -6.1668F, 0.8821F, 1.0F, 7.0F, 1.0F, CubeDeformation.NONE)
					.texOffs(17, 59).addBox(-0.5F, -8.1668F, -2.1179F, 1.0F, 10.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, 17.0F, -0.8727F, 0.0F, 0.0F));

			PartDefinition TailFin_r1 = Tail.addOrReplaceChild("TailFin_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 4.0F, -0.75F, 1.0F, 4.0F, 1.0F, CubeDeformation.NONE)
					.texOffs(12, 61).addBox(-4.0F, 0.0F, 0.25F, 1.0F, 9.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.5F, 1.75F, 2.0F, 1.789F, 0.0F, 0.0F));

			PartDefinition Base_r7 = Tail.addOrReplaceChild("Base_r7", CubeListBuilder.create().texOffs(0, 19).addBox(-1.0F, -0.3449F, -0.7203F, 2.0F, 5.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 5.0F, 13.0F, 1.4835F, 0.0F, 0.0F));

			PartDefinition Base_r8 = Tail.addOrReplaceChild("Base_r8", CubeListBuilder.create().texOffs(0, 61).addBox(-1.5F, -1.3563F, -0.6088F, 3.0F, 5.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.5F, 9.25F, 1.309F, 0.0F, 0.0F));

			PartDefinition Base_r9 = Tail.addOrReplaceChild("Base_r9", CubeListBuilder.create().texOffs(61, 30).addBox(-2.0F, -1.075F, -0.625F, 4.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.9599F, 0.0F, 0.0F));

			PartDefinition Base_r10 = Tail.addOrReplaceChild("Base_r10", CubeListBuilder.create().texOffs(50, 0).addBox(-2.0F, 0.75F, -0.8F, 4.0F, 8.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

			PartDefinition Breasts = Torso.addOrReplaceChild("Breasts", CubeListBuilder.create().texOffs(53, 56).addBox(-5.25F, 0.0158F, 0.0565F, 5.0F, 5.0F, 3.0F, new CubeDeformation(-0.05F))
					.texOffs(24, 19).addBox(0.25F, 0.0158F, 0.0565F, 5.0F, 5.0F, 3.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 0.0F, -2.5F, -0.4363F, 0.0F, 0.0F));

			PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(30, 0).addBox(-4.0F, -2.0F, -3.0F, 5.0F, 14.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(-6.0F, -2.0F, 0.5F));

			PartDefinition Spike_r1 = RightArm.addOrReplaceChild("Spike_r1", CubeListBuilder.create().texOffs(40, 19).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 4.0F, 1.0F, CubeDeformation.NONE)
					.texOffs(62, 48).addBox(-1.0F, -2.5F, -2.0F, 2.0F, 6.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.0789F, 4.3746F, 1.1151F, -2.6425F, 0.8346F, 3.1091F));

			PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(27, 30).addBox(-1.0F, -2.0F, -3.0F, 5.0F, 14.0F, 5.0F, CubeDeformation.NONE), PartPose.offset(6.0F, -2.0F, 0.5F));

			PartDefinition Spike_r2 = LeftArm.addOrReplaceChild("Spike_r2", CubeListBuilder.create().texOffs(51, 64).addBox(-0.125F, -0.5F, -1.0F, 1.0F, 4.0F, 2.0F, CubeDeformation.NONE)
					.texOffs(20, 40).addBox(0.875F, -1.5F, -1.0F, 1.0F, 6.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.6568F, 3.5711F, 1.6568F, -0.4796F, -0.6979F, 0.7102F));

			PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create(), PartPose.offset(-2.25F, 10.0F, 0.0F));

			PartDefinition leg_r1 = RightLeg.addOrReplaceChild("leg_r1", CubeListBuilder.create().texOffs(20, 49).addBox(-3.0F, -6.7987F, -2.8677F, 5.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 6.275F, 4.65F, 1.2217F, 0.0F, 0.0F));

			PartDefinition thigh_r1 = RightLeg.addOrReplaceChild("thigh_r1", CubeListBuilder.create().texOffs(0, 49).addBox(-3.0F, -1.0F, -3.0F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.9F, 0.5F, -0.0873F, 0.0F, 0.0F));

			PartDefinition RightLower = RightLeg.addOrReplaceChild("RightLower", CubeListBuilder.create().texOffs(33, 56).addBox(-3.0F, 6.975F, -5.675F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.025F)), PartPose.offset(0.0F, 6.0F, 4.5F));

			PartDefinition leg_r2 = RightLower.addOrReplaceChild("leg_r2", CubeListBuilder.create().texOffs(42, 19).addBox(-3.0F, -1.225F, -4.0F, 5.0F, 9.0F, 5.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 0.15F, -1.0F, -0.0873F, 0.0F, 0.0F));

			PartDefinition RightLowerBeans = RightLower.addOrReplaceChild("RightLowerBeans", CubeListBuilder.create(), PartPose.offset(-2.0F, 20.0F, -3.2F));

			PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.25F, 10.0F, 0.0F));

			PartDefinition leg_r3 = LeftLeg.addOrReplaceChild("leg_r3", CubeListBuilder.create().texOffs(47, 33).addBox(-2.0F, -6.7987F, -2.8677F, 5.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 6.275F, 4.65F, 1.2217F, 0.0F, 0.0F));

			PartDefinition thigh_r2 = LeftLeg.addOrReplaceChild("thigh_r2", CubeListBuilder.create().texOffs(42, 44).addBox(-2.0F, -1.0F, -3.0F, 5.0F, 7.0F, 5.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(0.0F, 0.9F, 0.5F, -0.0873F, 0.0F, 0.0F));

			PartDefinition LeftLower = LeftLeg.addOrReplaceChild("LeftLower", CubeListBuilder.create().texOffs(50, 12).addBox(-2.0F, 6.975F, -5.675F, 5.0F, 1.0F, 5.0F, new CubeDeformation(0.025F)), PartPose.offset(0.0F, 6.0F, 4.5F));

			PartDefinition leg_r4 = LeftLower.addOrReplaceChild("leg_r4", CubeListBuilder.create().texOffs(0, 35).addBox(-2.0F, -1.225F, -4.0F, 5.0F, 9.0F, 5.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 0.15F, -1.0F, -0.0873F, 0.0F, 0.0F));

			PartDefinition LeftLowerBeans2 = LeftLower.addOrReplaceChild("LeftLowerBeans2", CubeListBuilder.create(), PartPose.offset(-2.0F, 20.0F, -3.2F));

			return LayerDefinition.create(meshdefinition, 128, 128);
		}

		@Override
		public PoseStack getPlacementCorrectors(CorrectorType type) {
			PoseStack corrector = super.getPlacementCorrectors(type);
			if (type.isArm())
				corrector.translate(0.0f, 4.0f / 16.0f, 0.0f);
			return corrector;
		}

		@Override
		public LatexAnimator<LatexSharkFemale, Remodel> getAnimator() {
			return animator;
		}

		public ModelPart getArm(HumanoidArm p_102852_) {
			return p_102852_ == HumanoidArm.LEFT ? this.LeftArm : this.RightArm;
		}

		public ModelPart getHead() {
			return this.Head;
		}

		public ModelPart getTorso() {
			return Torso;
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
	}
}