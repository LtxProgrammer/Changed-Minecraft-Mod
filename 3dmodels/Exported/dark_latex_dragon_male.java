// Made with Blockbench 4.5.0
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

		PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(12, 44).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(-2.5F, 10.0F, 0.0F));

		PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(44, 15).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(44, 15).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(44, 15).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

		PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(44, 15).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(44, 15).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(44, 15).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

		PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(29, 42).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

		PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(16, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

		PartDefinition RightUpperLeg_r1 = RightLeg.addOrReplaceChild("RightUpperLeg_r1", CubeListBuilder.create().texOffs(40, 11).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(43, 42).addBox(-2.0F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(2.5F, 10.0F, 0.0F));

		PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(36, 4).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(36, 4).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(36, 4).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

		PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(36, 4).addBox(-4.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(36, 4).addBox(-2.5F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(36, 4).addBox(-1.0F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

		PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(40, 21).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

		PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(32, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

		PartDefinition LeftUpperLeg_r1 = LeftLeg.addOrReplaceChild("LeftUpperLeg_r1", CubeListBuilder.create().texOffs(32, 32).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 8.0F, 8.0F, CubeDeformation.NONE)
		.texOffs(0, 48).addBox(-1.5F, -1.0F, -6.0F, 3.0F, 2.0F, 2.0F, CubeDeformation.NONE)
		.texOffs(44, 30).addBox(-2.0F, -2.0F, -7.0F, 4.0F, 2.0F, 3.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.0F, -7.0F, 0.0F, 0.0F, 0.0F, -0.2618F));

		PartDefinition Base_r1 = RightEar.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(34, 3).addBox(-1.0884F, -3.4697F, -2.2512F, 2.0F, 5.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.5F, 3.0F, 0.0F, -2.0891F, -0.4533F, 1.2989F));

		PartDefinition Base_r2 = RightEar.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(21, 18).addBox(-1.5088F, -5.0341F, -1.0F, 2.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7681F, -0.1841F, 0.0746F));

		PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create(), PartPose.offsetAndRotation(2.5F, -7.0F, 0.0F, 0.0F, 0.0F, 0.2618F));

		PartDefinition Base_r3 = LeftEar.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(29, 19).addBox(-2.0403F, -4.0223F, -1.0863F, 2.0F, 7.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(3.0F, 3.0F, 0.0F, -1.1519F, 0.4605F, 0.4624F));

		PartDefinition Base_r4 = LeftEar.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(28, 19).addBox(0.0088F, -5.0341F, -1.0F, 2.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.7681F, 0.1841F, -0.0746F));

		PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(32, 55).addBox(-4.0F, 33.5F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.25F))
		.texOffs(46, 56).addBox(-4.0F, 33.5F, 3.5F, 8.0F, 7.0F, 1.0F, new CubeDeformation(0.25F))
		.texOffs(46, 53).addBox(-4.5F, 33.5F, -3.5F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.25F))
		.texOffs(46, 53).addBox(3.5F, 33.5F, -3.5F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.25F))
		.texOffs(56, 58).addBox(-4.5F, 36.5F, 1.5F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.25F))
		.texOffs(56, 58).addBox(3.5F, 36.5F, 1.5F, 1.0F, 3.0F, 3.0F, new CubeDeformation(0.25F)), PartPose.offset(0.0F, -41.0F, 0.0F));

		PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 1.0F, -2.0F, 8.0F, 12.0F, 4.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition Base_r5 = Tail.addOrReplaceChild("Base_r5", CubeListBuilder.create().texOffs(53, 1).addBox(-1.0F, 0.1914F, -0.9483F, 2.0F, 7.0F, 2.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 2.0F, 6.0F, 1.4835F, 0.0F, 0.0F));

		PartDefinition Base_r6 = Tail.addOrReplaceChild("Base_r6", CubeListBuilder.create().texOffs(52, 0).addBox(-1.5F, 0.0F, -1.0F, 3.0F, 7.0F, 3.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 1.1781F, 0.0F, 0.0F));

		PartDefinition LeftWing = Torso.addOrReplaceChild("LeftWing", CubeListBuilder.create(), PartPose.offset(2.0F, 5.0F, 2.0F));

		PartDefinition Base_r7 = LeftWing.addOrReplaceChild("Base_r7", CubeListBuilder.create().texOffs(20, 24).addBox(8.0F, -27.0F, 0.0F, 3.0F, 16.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(21, 26).addBox(7.0F, -26.0F, 0.0F, 1.0F, 12.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(20, 27).addBox(5.0F, -24.0F, 0.0F, 1.0F, 5.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(20, 27).addBox(6.0F, -25.0F, 0.0F, 1.0F, 9.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(20, 27).addBox(2.0F, -24.0F, 0.0F, 3.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-2.0F, 22.0F, -2.0F, 0.0F, -0.6545F, 0.0F));

		PartDefinition RightWing = Torso.addOrReplaceChild("RightWing", CubeListBuilder.create(), PartPose.offset(-2.0F, 5.0F, 2.0F));

		PartDefinition Base_r8 = RightWing.addOrReplaceChild("Base_r8", CubeListBuilder.create().texOffs(20, 23).addBox(8.0F, -27.0F, -1.0F, 3.0F, 16.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(20, 27).addBox(7.0F, -26.0F, -1.0F, 1.0F, 12.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(20, 27).addBox(6.0F, -25.0F, -1.0F, 1.0F, 9.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(20, 27).addBox(5.0F, -24.0F, -1.0F, 1.0F, 5.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(20, 27).addBox(2.0F, -24.0F, -1.0F, 3.0F, 4.0F, 1.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(2.0F, 22.0F, -2.0F, 0.0F, -2.4871F, 0.0F));

		PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(24, 16).addBox(-3.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
		.texOffs(10, 50).addBox(0.0F, 10.75F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(10, 50).addBox(-3.0F, 10.75F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(10, 50).addBox(-3.0F, 10.75F, -0.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(10, 50).addBox(-3.0F, 10.75F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(-5.0F, 0.0F, 0.0F));

		PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, CubeDeformation.NONE)
		.texOffs(10, 50).addBox(-1.0F, 10.75F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(10, 50).addBox(2.0F, 10.75F, 1.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(10, 50).addBox(2.0F, 10.75F, -0.5F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(10, 50).addBox(2.0F, 10.75F, -2.0F, 1.0F, 1.0F, 1.0F, CubeDeformation.NONE), PartPose.offset(5.0F, 0.0F, 0.0F));

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