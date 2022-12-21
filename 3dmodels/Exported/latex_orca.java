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

		PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(48, 45).addBox(-2.0F, 12.0F, -1.75F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.75F, 10.0F, 0.0F));

		PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F))
		.texOffs(0, 18).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F))
		.texOffs(0, 32).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F)), PartPose.offsetAndRotation(2.0F, 13.0F, -2.25F, -0.6109F, 0.0F, 0.0F));

		PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(24, 4).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F))
		.texOffs(24, 6).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F))
		.texOffs(27, 5).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(2.0F, 16.0F, -0.75F, 0.0F, 0.0F, 0.0F));

		PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(40, 23).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

		PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(40, 12).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

		PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(32, 43).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(47, 40).addBox(-2.0F, 12.0F, -1.75F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(2.75F, 10.0F, 0.0F));

		PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(32, 0).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F))
		.texOffs(12, 32).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F))
		.texOffs(0, 34).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.005F)), PartPose.offsetAndRotation(2.0F, 13.0F, -2.25F, -0.6109F, 0.0F, 0.0F));

		PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(16, 32).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F))
		.texOffs(29, 32).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F))
		.texOffs(32, 33).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(2.0F, 16.0F, -0.75F, 0.0F, 0.0F, 0.0F));

		PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(0, 48).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

		PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(34, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

		PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(16, 41).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(50, 32).addBox(-1.5F, -2.0F, -7.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
		.texOffs(50, 37).addBox(-1.5F, 0.0F, -6.0F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition Snout_r1 = Head.addOrReplaceChild("Snout_r1", CubeListBuilder.create().texOffs(22, 51).addBox(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(-1.5F, -2.0F, -7.0F, 0.0F, -0.2182F, 0.0F));

		PartDefinition Snout_r2 = Head.addOrReplaceChild("Snout_r2", CubeListBuilder.create().texOffs(52, 8).addBox(-1.0F, 0.0F, 0.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(-0.001F)), PartPose.offsetAndRotation(1.5F, -2.0F, -7.0F, 0.0F, 0.2182F, 0.0F));

		PartDefinition Fins = Head.addOrReplaceChild("Fins", CubeListBuilder.create(), PartPose.offset(-0.1F, 27.0F, 0.0F));

		PartDefinition HeadFin_r1 = Fins.addOrReplaceChild("HeadFin_r1", CubeListBuilder.create().texOffs(24, 0).addBox(-2.1808F, -1.0F, -0.5736F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.5F, -29.0F, -0.75F, 0.5236F, 0.9599F, 0.0F));

		PartDefinition HeadFin_r2 = Fins.addOrReplaceChild("HeadFin_r2", CubeListBuilder.create().texOffs(32, 12).addBox(-2.1808F, -1.0F, -0.5736F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.5F, -29.0F, -0.75F, -0.5236F, 0.9599F, -3.1416F));

		PartDefinition Base_r1 = Fins.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(20, 16).addBox(-0.5F, -1.3434F, 5.2873F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(32, 53).addBox(-1.5F, -1.3434F, 3.5373F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(16, 32).addBox(-2.5F, -1.3434F, -1.2127F, 4.0F, 4.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -32.5F, -2.0F, 0.2849F, 0.274F, -0.7459F));

		PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(14, 21).addBox(-2.0F, -34.75F, -4.0F, 4.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(14, 25).addBox(-4.0F, -34.5F, -3.5F, 8.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(18, 23).addBox(2.0F, -34.75F, 0.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(16, 21).addBox(2.5F, -34.0F, -3.0F, 2.0F, 3.0F, 7.0F, new CubeDeformation(0.025F))
		.texOffs(20, 26).addBox(3.5F, -34.0F, -4.5F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(22, 25).addBox(-4.5F, -34.0F, -4.5F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(18, 23).addBox(2.5F, -31.0F, -1.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(17, 22).addBox(-4.5F, -31.0F, -1.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(15, 21).addBox(-4.5F, -34.0F, -3.0F, 2.0F, 3.0F, 7.0F, new CubeDeformation(0.025F))
		.texOffs(16, 26).addBox(-4.0F, -34.75F, 0.0F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(16, 28).addBox(-4.0F, -34.0F, -4.5F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(17, 24).addBox(-4.0F, -34.0F, 4.0F, 8.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(17, 27).addBox(-3.5F, -33.0F, -4.25F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 27.0F, 0.0F));

		PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 1.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(33, 1).addBox(-1.0F, -1.075F, -0.625F, 2.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.9599F, 0.0F, 0.0F));

		PartDefinition Base_r3 = Tail.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(48, 0).addBox(-1.5F, -1.3563F, -0.6088F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.5F, 9.25F, 1.309F, 0.0F, 0.0F));

		PartDefinition Base_r4 = Tail.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, 0.75F, -0.8F, 4.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

		PartDefinition Base_r5 = Tail.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(0, 0).addBox(-1.0F, -0.3449F, -0.7203F, 2.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 13.0F, 1.4835F, 0.0F, 0.0F));

		PartDefinition Base_r6 = Tail.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(0, 57).addBox(-0.5F, 5.6462F, -1.8296F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(45, 50).addBox(-0.5F, -1.3538F, -1.8296F, 1.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.75F, 5.0F, 17.5F, 0.4363F, 0.0F, 1.5708F));

		PartDefinition Base_r7 = Tail.addOrReplaceChild("Base_r7", CubeListBuilder.create().texOffs(0, 57).addBox(-0.5F, -8.3668F, -2.1179F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(14, 51).addBox(-0.5F, -6.3668F, -2.1179F, 1.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 5.0F, 17.5F, -0.4363F, 0.0F, 1.5708F));

		PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(24, 16).addBox(-3.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(36, 16).addBox(-3.0F, 10.75F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(29, 34).addBox(-3.0F, 10.75F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(16, 34).addBox(-3.0F, 10.75F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(12, 34).addBox(0.0F, 10.75F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 0.0F, 0.0F));

		PartDefinition Spike_r1 = RightArm.addOrReplaceChild("Spike_r1", CubeListBuilder.create().texOffs(53, 50).addBox(-0.5F, -2.425F, -2.15F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0789F, 4.8746F, 1.1151F, -2.6425F, 0.8346F, 3.1091F));

		PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(28, 41).addBox(2.0F, 10.75F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(16, 41).addBox(2.0F, 10.75F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(40, 12).addBox(2.0F, 10.75F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(36, 18).addBox(-1.0F, 10.75F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 0.0F, 0.0F));

		PartDefinition Spike_r2 = LeftArm.addOrReplaceChild("Spike_r2", CubeListBuilder.create().texOffs(54, 23).addBox(-0.125F, -1.5F, -0.5F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.6568F, 4.0711F, 1.6568F, -0.4796F, -0.6979F, 0.7102F));

		return LayerDefinition.create(meshdefinition, 64, 64);
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