// Made with Blockbench 4.7.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class DarkLatexPup<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "darklatexpup"), "main");
	private final ModelPart Head;
	private final ModelPart Body;
	private final ModelPart RightFrontLeg;
	private final ModelPart LeftFrontLeg;
	private final ModelPart RightHindLeg;
	private final ModelPart LeftHindLeg;

	public DarkLatexPup(ModelPart root) {
		this.Head = root.getChild("Head");
		this.Body = root.getChild("Body");
		this.RightFrontLeg = root.getChild("RightFrontLeg");
		this.LeftFrontLeg = root.getChild("LeftFrontLeg");
		this.RightHindLeg = root.getChild("RightHindLeg");
		this.LeftHindLeg = root.getChild("LeftHindLeg");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F, new CubeDeformation(0.0F))
		.texOffs(38, 10).addBox(-1.5F, 2.0F, -8.0F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 13.0F, -3.0F));

		PartDefinition Mask = Head.addOrReplaceChild("Mask", CubeListBuilder.create().texOffs(32, 20).addBox(-2.0F, -14.0F, -14.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 19).addBox(-1.0F, -15.0F, -14.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(22, 3).addBox(-1.0F, -12.0F, -14.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(0, 27).addBox(-4.0F, -12.0F, -14.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(19, 17).addBox(3.0F, -9.0F, -14.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(19, 14).addBox(3.0F, -12.0F, -14.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(9, 27).addBox(-2.0F, -10.0F, -16.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 30).addBox(-4.0F, -9.0F, -14.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(28, 7).addBox(-3.0F, -10.0F, -14.0F, 6.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
		.texOffs(28, 17).addBox(-3.0F, -13.0F, -14.0F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 11.0F, 7.0F));

		PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create().texOffs(27, 39).addBox(-1.0F, -1.0F, -1.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
		.texOffs(0, 14).addBox(-1.0F, -1.601F, -0.4F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.04F))
		.texOffs(27, 34).addBox(-1.0F, -2.1F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
		.texOffs(35, 10).addBox(-1.0F, -2.9F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(2.5F, -4.175F, -2.0F, 0.0F, 0.0F, 0.2618F));

		PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create().texOffs(19, 39).addBox(-1.495F, -1.1F, -10.0F, 3.0F, 3.0F, 1.0F, new CubeDeformation(0.05F))
		.texOffs(0, 0).addBox(-0.495F, -1.7F, -9.4F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.04F))
		.texOffs(6, 34).addBox(-0.495F, -2.2F, -10.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.05F))
		.texOffs(35, 0).addBox(0.505F, -3.0F, -10.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-2.975F, -3.95F, 7.0F, 0.0F, 0.0F, -0.2618F));

		PartDefinition Body = partdefinition.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(21, 22).addBox(-3.5F, -3.5F, -3.0F, 7.0F, 7.0F, 5.0F, new CubeDeformation(0.0F))
		.texOffs(0, 14).addBox(-3.0F, -3.0F, 2.0F, 6.0F, 6.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.0F, 0.0F));

		PartDefinition Tail = Body.addOrReplaceChild("Tail", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 8.0F));

		PartDefinition cube_r1 = Tail.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(19, 14).addBox(-1.0F, -10.575F, 6.9F, 2.0F, 2.0F, 5.0F, new CubeDeformation(-0.005F)), PartPose.offsetAndRotation(0.0F, 11.0F, -4.0F, -0.1745F, 0.0F, 0.0F));

		PartDefinition cube_r2 = Tail.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 27).addBox(-1.0F, -12.25F, -1.0F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 11.0F, -4.0F, -0.48F, 0.0F, 0.0F));

		PartDefinition RightFrontLeg = partdefinition.addOrReplaceChild("RightFrontLeg", CubeListBuilder.create().texOffs(0, 34).addBox(-0.75F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 18.0F, -1.0F));

		PartDefinition LeftFrontLeg = partdefinition.addOrReplaceChild("LeftFrontLeg", CubeListBuilder.create().texOffs(12, 32).addBox(-1.25F, 0.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(2.0F, 18.0F, -1.0F));

		PartDefinition RightHindLeg = partdefinition.addOrReplaceChild("RightHindLeg", CubeListBuilder.create().texOffs(37, 34).addBox(-1.25F, 5.0F, -0.725F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.001F)), PartPose.offset(-2.0F, 18.0F, 7.0F));

		PartDefinition cube_r3 = RightHindLeg.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(38, 0).addBox(-3.25F, -4.975F, 2.2F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.0F, 6.0F, -3.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition cube_r4 = RightHindLeg.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(20, 34).addBox(-3.25F, -6.0F, -0.5F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.005F)), PartPose.offsetAndRotation(2.0F, 6.0F, -3.0F, -0.5236F, 0.0F, 0.0F));

		PartDefinition cube_r5 = RightHindLeg.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(28, 10).addBox(-3.25F, -8.0F, -0.5F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(2.0F, 6.0F, -3.0F, -0.2618F, 0.0F, 0.0F));

		PartDefinition LeftHindLeg = partdefinition.addOrReplaceChild("LeftHindLeg", CubeListBuilder.create().texOffs(22, 0).addBox(-0.75F, 5.0F, -0.725F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.001F)), PartPose.offset(2.0F, 18.0F, 7.0F));

		PartDefinition cube_r6 = LeftHindLeg.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(28, 0).addBox(1.25F, -8.0F, -0.5F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-2.0F, 6.0F, -3.0F, -0.2618F, 0.0F, 0.0F));

		PartDefinition cube_r7 = LeftHindLeg.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(38, 37).addBox(1.25F, -4.975F, 2.2F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.0F, 6.0F, -3.0F, -0.2182F, 0.0F, 0.0F));

		PartDefinition cube_r8 = LeftHindLeg.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(30, 34).addBox(1.25F, -6.0F, -0.5F, 2.0F, 2.0F, 3.0F, new CubeDeformation(-0.005F)), PartPose.offsetAndRotation(-2.0F, 6.0F, -3.0F, -0.5236F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		Body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		RightFrontLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		LeftFrontLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		RightHindLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		LeftHindLeg.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}