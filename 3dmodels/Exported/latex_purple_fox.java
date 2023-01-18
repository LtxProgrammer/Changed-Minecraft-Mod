// Made with Blockbench 4.5.2
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports


public class custom_model<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "custom_model"), "main");
	private final ModelPart RightLeg;
	private final ModelPart LeftLeg;
	private final ModelPart Head;
	private final ModelPart Torso;
	private final ModelPart RightArm;
	private final ModelPart LeftArm;

	public custom_model(ModelPart root) {
		this.RightLeg = root.getChild("RightLeg");
		this.LeftLeg = root.getChild("LeftLeg");
		this.Head = root.getChild("Head");
		this.Torso = root.getChild("Torso");
		this.RightArm = root.getChild("RightArm");
		this.LeftArm = root.getChild("LeftArm");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(14, 55).addBox(-1.99F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-2.5F, 10.0F, 0.0F));

		PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(28, 44).addBox(-2.49F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(32, 44).addBox(-0.99F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(44, 47).addBox(-3.99F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

		PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(24, 46).addBox(-0.99F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(28, 46).addBox(-2.49F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(40, 47).addBox(-3.99F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

		PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(52, 35).addBox(-1.99F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

		PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(44, 13).addBox(-1.99F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

		PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(44, 47).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(52, 7).addBox(-2.01F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(2.5F, 10.0F, 0.0F));

		PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(0, 48).addBox(-2.485F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(4, 48).addBox(-0.985F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(48, 38).addBox(-3.985F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.975F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

		PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(8, 48).addBox(-0.985F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(28, 48).addBox(-2.485F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(48, 36).addBox(-3.985F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(1.975F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

		PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(0, 52).addBox(-2.01F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

		PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(12, 44).addBox(-2.01F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

		PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(28, 47).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
		.texOffs(12, 32).addBox(-2.0F, -2.0F, -6.0F, 4.0F, 2.0F, 2.0F, CubeDeformation.NONE)
		.texOffs(20, 17).addBox(-1.5F, 0.0F, -5.0F, 3.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(24, 44).addBox(-1.0F, -29.625F, -0.95F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.5F, 27.0F, 0.0F, 0.1745F, 0.0F, 0.0F));

		PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create().texOffs(36, 17).addBox(-1.5F, -1.0F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
		.texOffs(0, 32).addBox(-0.3F, -1.4F, -1.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
		.texOffs(32, 57).addBox(-0.5F, -1.6F, -0.2F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.05F))
		.texOffs(34, 38).addBox(-0.5F, -2.1F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
		.texOffs(24, 19).addBox(0.5F, -3.2F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-3.4F, -7.3F, 0.0F, 0.0F, 0.0F, -0.2618F));

		PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create().texOffs(16, 36).addBox(-1.0F, -1.0F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
		.texOffs(0, 16).addBox(-0.2F, -1.4F, -1.5F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
		.texOffs(44, 28).addBox(-1.0F, -1.6F, -0.1F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.05F))
		.texOffs(34, 36).addBox(-1.0F, -2.1F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
		.texOffs(44, 13).addBox(-1.0F, -3.2F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(2.9F, -7.3F, 0.0F, 0.0F, 0.0F, 0.2618F));

		PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(24, 8).addBox(-2.0F, -35.0F, -4.0F, 4.0F, 1.0F, 8.0F, CubeDeformation.NONE)
		.texOffs(44, 24).addBox(-4.0F, -34.5F, -3.5F, 8.0F, 1.0F, 3.0F, CubeDeformation.NONE)
		.texOffs(60, 16).addBox(2.0F, -35.0F, 0.0F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE)
		.texOffs(16, 33).addBox(2.5F, -34.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.025F))
		.texOffs(18, 61).addBox(3.7F, -32.0F, -2.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.025F))
		.texOffs(0, 0).addBox(3.5F, -31.0F, 1.5F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.025F))
		.texOffs(12, 61).addBox(-4.7F, -32.0F, -2.0F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.025F))
		.texOffs(28, 34).addBox(-4.5F, -31.0F, 2.5F, 1.0F, 4.0F, 2.0F, new CubeDeformation(0.025F))
		.texOffs(56, 5).addBox(-3.5F, -33.025F, -4.25F, 7.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(40, 21).addBox(3.5F, -34.0F, -4.5F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(40, 13).addBox(-4.5F, -34.0F, -4.5F, 1.0F, 2.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(56, 44).addBox(2.5F, -31.0F, -1.0F, 2.0F, 2.0F, 5.0F, CubeDeformation.NONE)
		.texOffs(55, 52).addBox(-4.5F, -31.0F, -1.0F, 2.0F, 2.0F, 5.0F, CubeDeformation.NONE)
		.texOffs(32, 25).addBox(-4.5F, -34.0F, -4.0F, 2.0F, 3.0F, 8.0F, new CubeDeformation(0.025F))
		.texOffs(54, 59).addBox(-4.0F, -35.0F, 0.0F, 2.0F, 1.0F, 4.0F, CubeDeformation.NONE)
		.texOffs(56, 3).addBox(-4.0F, -34.025F, -4.5F, 8.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(52, 28).addBox(-4.0F, -34.0F, 4.0F, 8.0F, 6.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(24, 4).addBox(-3.5F, -28.8F, 3.7F, 7.0F, 2.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 27.0F, 0.0F));

		PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 1.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE)
		.texOffs(56, 13).addBox(-3.0F, 1.0F, 1.75F, 6.0F, 2.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(52, 0).addBox(-4.0F, 1.0F, 1.5F, 8.0F, 2.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(24, 0).addBox(-4.0F, 1.0F, -2.5F, 8.0F, 3.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(60, 21).addBox(-3.0F, 4.0F, -2.5F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(44, 59).addBox(-3.0F, 1.0F, -2.75F, 6.0F, 2.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(0, 61).addBox(-3.0F, 3.0F, 1.5F, 6.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(40, 11).addBox(-2.0F, 3.0F, -2.75F, 4.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition RightTuft_r1 = Torso.addOrReplaceChild("RightTuft_r1", CubeListBuilder.create().texOffs(37, 57).addBox(10.5F, -23.5F, -2.5F, 1.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 26.0F, 0.0F, 0.0F, 0.0F, -0.6109F));

		PartDefinition LeftTuft_r1 = Torso.addOrReplaceChild("LeftTuft_r1", CubeListBuilder.create().texOffs(25, 57).addBox(-11.5F, -23.5F, -2.5F, 1.0F, 2.0F, 5.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 26.0F, 0.0F, 0.0F, 0.0F, 0.6109F));

		PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(36, 36).addBox(-2.0F, 0.0F, -1.7F, 4.0F, 7.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 2.375F, 5.9F, 1.4835F, 0.0F, 0.0F));

		PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(40, 0).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.1F, 0.0F, 1.1345F, 0.0F, 0.0F));

		PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(24, 17).addBox(-3.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
		.texOffs(4, 50).addBox(0.0F, 10.75F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(0, 50).addBox(-3.0F, 10.75F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(44, 49).addBox(-3.0F, 10.75F, -0.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(40, 49).addBox(-3.0F, 10.75F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 0.0F, 0.0F));

		PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
		.texOffs(14, 55).addBox(-1.0F, 10.75F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(52, 44).addBox(2.0F, 10.75F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(51, 35).addBox(2.0F, 10.75F, -0.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(8, 50).addBox(2.0F, 10.75F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		RightLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		LeftLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Torso.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		RightArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		LeftArm.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}