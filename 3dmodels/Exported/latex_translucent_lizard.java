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

		PartDefinition RightLeg = partdefinition.addOrReplaceChild("RightLeg", CubeListBuilder.create().texOffs(50, 0).addBox(-1.9999F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.5F, 10.0F, 0.0F));

		PartDefinition Toe_r1 = RightLeg.addOrReplaceChild("Toe_r1", CubeListBuilder.create().texOffs(0, 16).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 18).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(24, 5).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

		PartDefinition Toe_r2 = RightLeg.addOrReplaceChild("Toe_r2", CubeListBuilder.create().texOffs(20, 16).addBox(-1.0001F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(20, 18).addBox(-2.501F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(23, 17).addBox(-4.0001F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

		PartDefinition RightLowerLeg_r1 = RightLeg.addOrReplaceChild("RightLowerLeg_r1", CubeListBuilder.create().texOffs(0, 52).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

		PartDefinition RightMidLeg_r1 = RightLeg.addOrReplaceChild("RightMidLeg_r1", CubeListBuilder.create().texOffs(46, 8).addBox(-1.99F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

		PartDefinition Gooeycenter_r1 = RightLeg.addOrReplaceChild("Gooeycenter_r1", CubeListBuilder.create().texOffs(46, 56).addBox(-1.5F, 1.0F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(-0.25F))
		.texOffs(28, 46).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition LeftLeg = partdefinition.addOrReplaceChild("LeftLeg", CubeListBuilder.create().texOffs(48, 27).addBox(-2.001F, 12.0F, -2.75F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(2.5F, 10.0F, 0.0F));

		PartDefinition Toe_r3 = LeftLeg.addOrReplaceChild("Toe_r3", CubeListBuilder.create().texOffs(24, 19).addBox(-2.5F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(26, 16).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(12, 32).addBox(-4.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 13.0F, -3.25F, -0.6109F, 0.0F, 0.0F));

		PartDefinition Toe_r4 = LeftLeg.addOrReplaceChild("Toe_r4", CubeListBuilder.create().texOffs(27, 6).addBox(-0.999F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(30, 0).addBox(-2.499F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 32).addBox(-3.999F, -3.0F, -1.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 16.0F, -1.75F, 0.0F, 0.0F, 0.0F));

		PartDefinition LeftLowerLeg_r1 = LeftLeg.addOrReplaceChild("LeftLowerLeg_r1", CubeListBuilder.create().texOffs(51, 34).addBox(-2.0F, -7.5F, -1.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 14.25F, -1.25F, -0.5236F, 0.0F, 0.0F));

		PartDefinition LeftMidLeg_r1 = LeftLeg.addOrReplaceChild("LeftMidLeg_r1", CubeListBuilder.create().texOffs(12, 44).addBox(-2.01F, 0.0F, -2.0F, 4.0F, 7.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 4.25F, -1.5F, 0.5672F, 0.0F, 0.0F));

		PartDefinition Gooeycenter_r2 = LeftLeg.addOrReplaceChild("Gooeycenter_r2", CubeListBuilder.create().texOffs(34, 56).addBox(-1.5F, 1.0F, -1.5F, 3.0F, 5.0F, 3.0F, new CubeDeformation(-0.25F))
		.texOffs(44, 46).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -7.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
		.texOffs(24, 16).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(32, 12).addBox(-2.0F, -2.0F, -6.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 5).addBox(-1.5F, 0.0F, -5.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition leftear_r1 = Head.addOrReplaceChild("leftear_r1", CubeListBuilder.create().texOffs(14, 55).addBox(-6.15F, -28.9F, -23.8F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 26.0F, 0.0F, -0.7418F, 0.2182F, 0.2618F));

		PartDefinition leftear_r2 = Head.addOrReplaceChild("leftear_r2", CubeListBuilder.create().texOffs(58, 54).addBox(-5.0F, -13.25F, -29.05F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 26.0F, 0.0F, -1.2654F, 0.3927F, 0.2618F));

		PartDefinition rightear_r1 = Head.addOrReplaceChild("rightear_r1", CubeListBuilder.create().texOffs(0, 61).addBox(3.0F, -13.25F, -29.05F, 2.0F, 7.0F, 2.0F, new CubeDeformation(0.025F)), PartPose.offsetAndRotation(0.0F, 26.0F, 0.0F, -1.2654F, -0.3927F, -0.2618F));

		PartDefinition rightear_r2 = Head.addOrReplaceChild("rightear_r2", CubeListBuilder.create().texOffs(24, 56).addBox(4.15F, -28.9F, -23.8F, 2.0F, 7.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 26.0F, 0.0F, -0.7418F, -0.2182F, -0.2618F));

		PartDefinition Torso = partdefinition.addOrReplaceChild("Torso", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 1.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(32, 0).addBox(-3.0F, 2.25F, -1.5F, 6.0F, 9.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -2.0F, 0.0F));

		PartDefinition Tail = Torso.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, 12.0F, 0.0F));

		PartDefinition Base_r1 = Tail.addOrReplaceChild("Base_r1", CubeListBuilder.create().texOffs(56, 43).addBox(-1.0F, -10.35F, 13.925F, 2.0F, 2.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(0.0F, 14.0F, 0.0F, 0.0436F, 0.0F, 0.0F));

		PartDefinition Base_r2 = Tail.addOrReplaceChild("Base_r2", CubeListBuilder.create().texOffs(36, 28).addBox(-1.5F, -13.2F, 6.9F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.0F, 14.0F, 0.0F, -0.1309F, 0.0F, 0.0F));

		PartDefinition Base_r3 = Tail.addOrReplaceChild("Base_r3", CubeListBuilder.create().texOffs(36, 37).addBox(-1.5F, -1.375F, -2.6983F, 3.0F, 3.0F, 6.0F, new CubeDeformation(-0.025F)), PartPose.offsetAndRotation(0.0F, 2.0F, 6.0F, -0.3927F, 0.0F, 0.0F));

		PartDefinition Base_r4 = Tail.addOrReplaceChild("Base_r4", CubeListBuilder.create().texOffs(48, 19).addBox(-2.0F, -2.8F, 0.4F, 4.0F, 4.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, -0.5236F, 0.0F, 0.0F));

		PartDefinition RightArm = partdefinition.addOrReplaceChild("RightArm", CubeListBuilder.create().texOffs(20, 28).addBox(-3.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(0, 0).addBox(-2.0F, 6.75F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.2F))
		.texOffs(0, 34).addBox(0.0F, 10.85F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(32, 30).addBox(-3.0F, 10.85F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(32, 28).addBox(-3.0F, 10.85F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(16, 32).addBox(-3.0F, 10.85F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, 0.0F, 0.0F));

		PartDefinition LeftArm = partdefinition.addOrReplaceChild("LeftArm", CubeListBuilder.create().texOffs(0, 32).addBox(-1.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F))
		.texOffs(24, 0).addBox(0.0F, 6.75F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.2F))
		.texOffs(16, 36).addBox(-1.0F, 10.85F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(35, 29).addBox(2.0F, 10.85F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(16, 34).addBox(2.0F, 10.85F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(12, 34).addBox(2.0F, 10.85F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, 0.0F, 0.0F));

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