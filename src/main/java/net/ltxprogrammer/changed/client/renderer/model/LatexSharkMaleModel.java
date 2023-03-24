package net.ltxprogrammer.changed.client.renderer.model;
// Made with Blockbench 4.1.5
// Exported for Minecraft version 1.17 with Mojang mappings
// Paste this class into your mod and generate all required imports

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.animate.AnimatorPresets;
import net.ltxprogrammer.changed.client.renderer.animate.LatexAnimator;
import net.ltxprogrammer.changed.entity.beast.LatexSharkMale;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class LatexSharkMaleModel extends LatexHumanoidModel<LatexSharkMale> implements LatexHumanoidModelInterface<LatexSharkMale, LatexSharkMaleModel> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Changed.modResource("latex_shark_male"), "main");
	private final ModelPart RightLeg;
	private final ModelPart LeftLeg;
	private final ModelPart RightArm;
	private final ModelPart LeftArm;
	private final ModelPart Head;
	private final ModelPart Torso;
	private final ModelPart Tail;
	private final LatexAnimator<LatexSharkMale, LatexSharkMaleModel> animator;

	public LatexSharkMaleModel(ModelPart root) {
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

		PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(20, 32).addBox(-4.0F, -0.995F, 0.01F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
				.texOffs(20, 34).addBox(-2.5F, -0.995F, 0.01F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
				.texOffs(36, 8).addBox(-1.0F, -0.995F, 0.01F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(1.8F, 15.875F, -3.225F, -0.6109F, 0.0F, 0.0F));

		PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(0, 40).addBox(-1.0F, -3.0F, -1.49F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
				.texOffs(20, 36).addBox(-2.5F, -3.0F, -1.49F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
				.texOffs(40, 8).addBox(-4.0F, -3.0F, -1.49F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(1.8F, 18.95F, -1.75F, 0.0F, 0.0F, 0.0F));

		PartDefinition RightFoot_r1 = RightLeg.addOrReplaceChild("RightFoot_r1", CubeListBuilder.create().texOffs(24, 59).addBox(-4.0F, -4.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(1.8F, 18.95F, -0.75F, 0.0F, 0.0F, 0.0F));

		PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(50, 28).addBox(-2.0F, -7.05F, -0.85F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.06F)), PartPose.offsetAndRotation(-0.2F, 16.75F, -1.15F, -0.5236F, 0.0F, 0.0F));

		PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(16, 47).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(-0.2F, 6.85F, -1.3F, 0.5061F, 0.0F, 0.0F));

		PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(50, 4).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.35F)), PartPose.offsetAndRotation(-0.2F, 1.4F, -0.2F, -0.2182F, 0.0F, 0.0F));

		PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create(), PartPose.offset(2.75F, 7.0F, 0.0F));

		PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(0, 18).addBox(-2.5F, -0.995F, 0.01F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
				.texOffs(24, 0).addBox(-1.0F, -0.995F, 0.01F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
				.texOffs(32, 8).addBox(-4.0F, -0.995F, 0.01F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(2.2F, 15.875F, -3.225F, -0.6109F, 0.0F, 0.0F));

		PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(24, 2).addBox(-1.0F, -3.0F, -1.49F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
				.texOffs(0, 28).addBox(-2.5F, -3.0F, -1.49F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
				.texOffs(0, 30).addBox(-4.0F, -3.0F, -1.49F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(2.2F, 18.95F, -1.75F, 0.0F, 0.0F, 0.0F));

		PartDefinition LeftFoot_r1 = LeftLeg.addOrReplaceChild("LeftFoot_r1", CubeListBuilder.create().texOffs(52, 37).addBox(-4.0F, -4.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(2.2F, 18.95F, -0.75F, 0.0F, 0.0F, 0.0F));

		PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(48, 55).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.06F)), PartPose.offsetAndRotation(0.2F, 17.2F, -1.25F, -0.5236F, 0.0F, 0.0F));

		PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(32, 47).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(0.2F, 6.85F, -1.3F, 0.5061F, 0.0F, 0.0F));

		PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(0, 52).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.35F)), PartPose.offsetAndRotation(0.2F, 1.4F, -0.2F, -0.2182F, 0.0F, 0.0F));

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create(), PartPose.offset(0.0F, -9.0F, 0.0F));

		PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(66, 6).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.5F, -2.3F, -6.975F, 0.0F, -0.2182F, 0.0F));

		PartDefinition Snout_r2 = Head.addOrReplaceChild("Snout_r2", CubeListBuilder.create().texOffs(32, 66).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.5F, -2.3F, -6.975F, 0.0F, 0.2182F, 0.0F));

		PartDefinition Snout_r3 = Head.addOrReplaceChild("Snout_r3", CubeListBuilder.create().texOffs(66, 12).addBox(-1.5F, -27.0F, -6.0F, 3.0F, 1.0F, 2.0F, CubeDeformation.NONE)
				.texOffs(24, 65).addBox(-1.5F, -29.0F, -7.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 26.7F, 0.0F, 0.0F, 0.0F, 0.0F));

		PartDefinition Head_r1 = Head.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.7F, 0.0F, 0.0F, 0.0F, 0.0F));

		PartDefinition Fins = Head.addOrReplaceChild("Fins", CubeListBuilder.create(), PartPose.offset(0.0F, 27.0F, 0.0F));

		PartDefinition Base_r1 = Fins.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(8, 69).addBox(-2.1F, -1.25F, -0.5F, 3.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-3.5F, -27.95F, -1.6F, 0.5236F, 0.9599F, 0.0F));

		PartDefinition Base_r2 = Fins.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(68, 58).addBox(-2.2F, -1.25F, -0.5F, 3.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.5F, -28.375F, -2.0F, -0.5236F, 0.9599F, -3.1416F));

		PartDefinition Base_r3 = Fins.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(49, 0).addBox(-1.25F, -1.0F, 0.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(4.6F, -32.35F, -0.4F, 0.576F, -1.1519F, -0.2618F));

		PartDefinition Base_r4 = Fins.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(18, 70).addBox(-0.25F, -1.0F, 0.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(3.9F, -32.25F, -2.0F, 0.576F, -0.8901F, -0.2618F));

		PartDefinition Base_r5 = Fins.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(30, 20).addBox(-1.25F, -1.0F, 0.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-4.6F, -32.35F, 0.0F, -0.576F, -1.1519F, -2.8798F));

		PartDefinition Base_r6 = Fins.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(68, 67).addBox(-0.25F, -1.0F, 0.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(-3.9F, -32.25F, -2.0F, -0.576F, -0.8901F, -2.8798F));

		PartDefinition Topheadfin = Fins.addOrReplaceChild("Topheadfin", CubeListBuilder.create(), PartPose.offsetAndRotation(0.0F, -33.2F, 1.0F, 0.6109F, 0.0F, 0.0F));

		PartDefinition headfin1_r1 = Topheadfin.addOrReplaceChild("headfin1_r1", CubeListBuilder.create().texOffs(60, 51).addBox(-0.8F, -0.8F, -2.1F, 3.0F, 3.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -0.5F, 3.1F, -0.3387F, 0.3139F, 0.7311F));

		PartDefinition headfin2_r1 = Topheadfin.addOrReplaceChild("headfin2_r1", CubeListBuilder.create().texOffs(58, 60).addBox(-1.5F, -1.5F, -2.0F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, -0.5F, 0.0F, 0.0F, 0.0F, 0.7854F));

		PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create(), PartPose.offset(0.0F, 27.0F, 0.0F));

		PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create(), PartPose.offset(0.0F, -9.0F, 0.0F));

		PartDefinition BackFin_r1 = Torso.addOrReplaceChild("BackFin_r1", CubeListBuilder.create().texOffs(12, 59).addBox(-1.0F, 0.0F, -4.0F, 2.0F, 6.0F, 4.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0F, 3.45F, 2.5F, 0.5236F, 0.0F, 0.0F));

		PartDefinition Torso_r1 = Torso.addOrReplaceChild("Torso_r1", CubeListBuilder.create().texOffs(24, 0).addBox(-5.0F, -22.0F, -3.1F, 10.0F, 3.0F, 5.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 35.7F, 0.8F, 0.0F, 0.0F, 0.0F));

		PartDefinition Torso_r2 = Torso.addOrReplaceChild("Torso_r2", CubeListBuilder.create().texOffs(0, 16).addBox(-5.0F, -32.0F, -2.7F, 10.0F, 7.0F, 5.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 32.7F, 0.2F, 0.0F, 0.0F, 0.0F));

		PartDefinition Torso_r3 = Torso.addOrReplaceChild("Torso_r3", CubeListBuilder.create().texOffs(26, 24).addBox(-3.9F, -24.9F, -2.1F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.35F)), PartPose.offsetAndRotation(-0.1F, 32.7F, 0.3F, 0.0F, 0.0F, 0.0F));

		PartDefinition buffness = Torso.addOrReplaceChild("buffness", CubeListBuilder.create(), PartPose.offset(-0.2F, 31.05F, 0.05F));

		PartDefinition StrenghCube_r1 = buffness.addOrReplaceChild("StrenghCube_r1", CubeListBuilder.create().texOffs(62, 0).addBox(-2.5F, -2.5F, -0.5F, 5.0F, 5.0F, 1.0F, new CubeDeformation(0.2F))
				.texOffs(64, 34).addBox(3.2F, -2.5F, -0.5F, 5.0F, 5.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(-2.65F, -27.3F, -2.8F, 0.0F, 0.0F, 0.0F));

		PartDefinition StrenghCube_r2 = buffness.addOrReplaceChild("StrenghCube_r2", CubeListBuilder.create().texOffs(58, 67).addBox(-1.5F, -1.0F, -1.1F, 3.0F, 2.0F, 2.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(-1.175F, -17.9F, -2.0F, 0.0F, 0.0F, 0.0F));

		PartDefinition StrenghCube_r3 = buffness.addOrReplaceChild("StrenghCube_r3", CubeListBuilder.create().texOffs(15, 28).addBox(-1.5F, -1.0F, -0.5F, 3.0F, 2.0F, 2.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(1.575F, -17.9F, -2.55F, 0.0F, 0.0F, 0.0F));

		PartDefinition StrenghCube_r4 = buffness.addOrReplaceChild("StrenghCube_r4", CubeListBuilder.create().texOffs(32, 34).addBox(-1.5F, -0.9F, -0.5F, 3.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.875F, -20.1F, -2.4F, 0.0F, 0.0F, 0.0F));

		PartDefinition StrenghCube_r5 = buffness.addOrReplaceChild("StrenghCube_r5", CubeListBuilder.create().texOffs(28, 47).addBox(-1.5F, -0.9F, -0.5F, 3.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-1.475F, -20.1F, -2.4F, 0.0F, 0.0F, 0.0F));

		PartDefinition StrenghCube_r6 = buffness.addOrReplaceChild("StrenghCube_r6", CubeListBuilder.create().texOffs(20, 59).addBox(-1.5F, -1.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.3F))
				.texOffs(68, 40).addBox(2.4F, -1.0F, -0.5F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.3F)), PartPose.offsetAndRotation(-1.75F, -22.7F, -2.3F, 0.0F, 0.0F, 0.0F));

		PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 13.0F, 0.0F));

		PartDefinition TailFin_r1 = Tail.addOrReplaceChild("TailFin_r1", CubeListBuilder.create().texOffs(64, 24).addBox(0.0F, 0.0F, -0.75F, 1.0F, 7.0F, 3.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-0.5F, 3.55F, 7.0F, 1.5272F, 0.0F, 0.0F));

		Tail.addOrReplaceChild("Base_r7", CubeListBuilder.create().texOffs(0, 16).addBox(-0.5F, 5.3462F, -1.8296F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.05F))
				.texOffs(40, 59).addBox(-0.5F, -2.5538F, -1.8296F, 1.0F, 8.0F, 3.0F, new CubeDeformation(-0.05F)), PartPose.offsetAndRotation(0.0F, 8.55F, 24.3F, 1.309F, 0.0F, 0.0F));

		Tail.addOrReplaceChild("Base_r8", CubeListBuilder.create().texOffs(39, 70).addBox(-0.5F, -8.1668F, -2.1179F, 1.0F, 2.0F, 3.0F, CubeDeformation.NONE)
				.texOffs(16, 40).addBox(-0.5F, -6.1668F, 0.8821F, 1.0F, 7.0F, 1.0F, CubeDeformation.NONE)
				.texOffs(0, 63).addBox(-0.5F, -6.1668F, -2.1179F, 1.0F, 8.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 7.675F, 24.2F, -0.6981F, 0.0F, 0.0F));

		Tail.addOrReplaceChild("Base_r9", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.15F, -0.8F, 2.0F, 5.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 8.9F, 20.3F, 1.5708F, 0.0F, 0.0F));

		Tail.addOrReplaceChild("Base_r10", CubeListBuilder.create().texOffs(62, 15).addBox(-1.5F, -1.3563F, -1.8F, 3.0F, 6.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 7.95F, 15.8F, 1.4835F, 0.0F, 0.0F));

		Tail.addOrReplaceChild("Base_r11", CubeListBuilder.create().texOffs(0, 28).addBox(-3.0F, -1.0F, -1.0F, 5.0F, 7.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.5F, 3.0F, 2.9F, 0.8727F, 0.0F, 0.0F));

		Tail.addOrReplaceChild("Base_r12", CubeListBuilder.create().texOffs(26, 10).addBox(-4.0F, 2.0F, -1.0F, 6.0F, 4.0F, 6.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.0F, 0.7F, -2.6F, 1.0908F, 0.0F, 0.0F));

		Tail.addOrReplaceChild("Base_r13", CubeListBuilder.create().texOffs(48, 43).addBox(-2.0F, 1.0F, -1.0F, 4.0F, 8.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 6.0F, 6.3F, 1.2217F, 0.0F, 0.0F));

		PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create(), PartPose.offset(-5.0F, -8.0F, 0.0F));

		PartDefinition Spike_r1 = RightArm.addOrReplaceChild("Spike_r1", CubeListBuilder.create().texOffs(64, 43).addBox(-1.975F, -2.5F, -0.5F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(-3.9F, 5.975F, 1.6F, -0.48F, 0.6981F, -0.7069F));

		PartDefinition Finger_r1 = RightArm.addOrReplaceChild("Finger_r1", CubeListBuilder.create().texOffs(12, 40).addBox(2.6F, 14.2F, 0.7F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.149F))
				.texOffs(12, 42).addBox(6.2F, 14.2F, 0.7F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.149F)), PartPose.offsetAndRotation(-7.2F, 2.45F, -3.0F, 0.0F, 0.0F, 0.0F));

		PartDefinition Finger_r2 = RightArm.addOrReplaceChild("Finger_r2", CubeListBuilder.create().texOffs(40, 20).addBox(2.6F, 14.2F, 0.95F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.149F)), PartPose.offsetAndRotation(-7.2F, 2.45F, -1.5F, 0.0F, 0.0F, 0.0F));

		PartDefinition Finger_r3 = RightArm.addOrReplaceChild("Finger_r3", CubeListBuilder.create().texOffs(0, 42).addBox(2.6F, 14.2F, 1.3F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.149F)), PartPose.offsetAndRotation(-7.2F, 2.45F, 0.0F, 0.0F, 0.0F, 0.0F));

		PartDefinition RightArm_r1 = RightArm.addOrReplaceChild("RightArm_r1", CubeListBuilder.create().texOffs(0, 40).addBox(-4.1F, 6.5F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.45F))
				.texOffs(20, 34).addBox(-4.1F, -1.9F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(-0.2F, 1.7F, 0.0F, 0.0F, 0.0F, 0.0F));

		PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create(), PartPose.offset(5.0F, -8.0F, 0.0F));

		PartDefinition Finger_r4 = LeftArm.addOrReplaceChild("Finger_r4", CubeListBuilder.create().texOffs(42, 22).addBox(-0.6F, -0.5F, -0.7F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.149F)), PartPose.offsetAndRotation(0.6F, 17.35F, -1.6F, 0.0F, 0.0F, 0.0F));

		PartDefinition Finger_r5 = LeftArm.addOrReplaceChild("Finger_r5", CubeListBuilder.create().texOffs(44, 8).addBox(3.2F, 14.0F, 0.8F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.149F)), PartPose.offsetAndRotation(0.4F, 2.85F, -3.1F, 0.0F, 0.0F, 0.0F));

		PartDefinition Finger_r6 = LeftArm.addOrReplaceChild("Finger_r6", CubeListBuilder.create().texOffs(44, 10).addBox(3.2F, 14.0F, 1.025F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.149F)), PartPose.offsetAndRotation(0.4F, 2.85F, -1.5F, 0.0F, 0.0F, 0.0F));

		PartDefinition Finger_r7 = LeftArm.addOrReplaceChild("Finger_r7", CubeListBuilder.create().texOffs(44, 12).addBox(3.2F, 14.0F, 1.2F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.149F)), PartPose.offsetAndRotation(0.4F, 2.85F, 0.1F, 0.0F, 0.0F, 0.0F));

		PartDefinition Spike_r2 = LeftArm.addOrReplaceChild("Spike_r2", CubeListBuilder.create().texOffs(48, 64).addBox(-2.5F, -2.5F, -0.5F, 4.0F, 6.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(4.2F, 6.2F, 1.9F, -0.48F, -0.6981F, 0.7069F));

		PartDefinition LeftArm_r1 = LeftArm.addOrReplaceChild("LeftArm_r1", CubeListBuilder.create().texOffs(46, 16).addBox(0.0F, 6.6F, -2.0F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.45F))
				.texOffs(36, 34).addBox(0.0F, -2.0F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.3F, 1.8F, 0.0F, 0.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void prepareMobModel(LatexSharkMale p_102861_, float p_102862_, float p_102863_, float p_102864_) {
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
	public LatexAnimator<LatexSharkMale, LatexSharkMaleModel> getAnimator() {
		return animator;
	}

	@Override
	public void setupAnim(LatexSharkMale entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
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
		RightLeg.render(poseStack, buffer, packedLight, packedOverlay);
		LeftLeg.render(poseStack, buffer, packedLight, packedOverlay);
		Head.render(poseStack, buffer, packedLight, packedOverlay);
		Torso.render(poseStack, buffer, packedLight, packedOverlay);
		RightArm.render(poseStack, buffer, packedLight, packedOverlay);
		LeftArm.render(poseStack, buffer, packedLight, packedOverlay);
	}
}