// Made with Blockbench 4.5.2
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports


public class custom_model<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "custom_model"), "main");
	private final ModelPart Hand;

	public custom_model(ModelPart root) {
		this.Hand = root.getChild("Hand");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Hand = partdefinition.addOrReplaceChild("Hand", CubeListBuilder.create().texOffs(0, 24).addBox(-9.0F, -10.0F, -5.0F, 18.0F, 10.0F, 10.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition cube_r1 = Hand.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 44).addBox(-10.0F, -15.0F, 3.5F, 20.0F, 15.0F, 1.0F, CubeDeformation.NONE)
		.texOffs(0, 0).addBox(-10.0F, -14.0F, -4.5F, 20.0F, 16.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -10.0F, 0.0F, -0.3491F, 0.0F, 0.0F));

		PartDefinition Pinkie = Hand.addOrReplaceChild("Pinkie", CubeListBuilder.create(), PartPose.offset(7.0F, -23.0F, 4.0F));

		PartDefinition cube_r2 = Pinkie.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(48, 0).addBox(-10.0F, -7.2143F, -2.9557F, 5.0F, 5.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(7.5F, -3.75F, 0.0F, 0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r3 = Pinkie.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(48, 16).addBox(4.0F, -8.2143F, -1.9557F, 6.0F, 10.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-7.0F, -3.25F, -0.5F, 0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r4 = Pinkie.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(56, 0).addBox(-11.0F, -21.0F, -3.5F, 6.0F, 5.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(8.0F, 14.75F, -5.5F, -0.3491F, 0.0F, 0.0F));

		PartDefinition Middle = Hand.addOrReplaceChild("Middle", CubeListBuilder.create(), PartPose.offset(0.0F, -23.0F, 4.0F));

		PartDefinition cube_r5 = Middle.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(56, 34).addBox(-10.0F, -7.2143F, -2.9557F, 5.0F, 5.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(7.5F, -3.75F, 0.0F, 0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r6 = Middle.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 60).addBox(-3.0F, -8.2143F, -1.9557F, 6.0F, 10.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, -3.25F, -0.5F, 0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r7 = Middle.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(68, 26).addBox(-11.0F, -21.0F, -3.5F, 6.0F, 5.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(8.0F, 14.75F, -5.5F, -0.3491F, 0.0F, 0.0F));

		PartDefinition Index = Hand.addOrReplaceChild("Index", CubeListBuilder.create(), PartPose.offset(-7.0F, -23.0F, 4.0F));

		PartDefinition cube_r8 = Index.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(20, 60).addBox(-10.0F, -7.2143F, -2.9557F, 5.0F, 5.0F, 1.0F, new CubeDeformation(0.25F)), PartPose.offsetAndRotation(7.5F, -3.75F, 0.0F, 0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r9 = Index.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(28, 62).addBox(-10.0F, -8.2143F, -1.9557F, 6.0F, 10.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(7.0F, -3.25F, -0.5F, 0.3491F, 0.0F, 0.0F));

		PartDefinition cube_r10 = Index.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(73, 39).addBox(-10.0F, -21.0F, -3.5F, 6.0F, 5.0F, 8.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(7.0F, 14.75F, -5.5F, -0.3491F, 0.0F, 0.0F));

		PartDefinition Thumb = Hand.addOrReplaceChild("Thumb", CubeListBuilder.create(), PartPose.offset(-10.0F, -16.0F, -1.0F));

		PartDefinition cube_r11 = Thumb.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(68, 13).addBox(-5.0F, -3.3015F, -5.0399F, 11.0F, 7.0F, 1.0F, new CubeDeformation(0.25F))
		.texOffs(42, 44).addBox(-6.0F, -4.3015F, -4.5399F, 11.0F, 9.0F, 9.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(-5.75F, 0.925F, 0.75F, -0.4301F, -0.609F, 0.2567F));

		PartDefinition cube_r12 = Thumb.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(56, 62).addBox(-5.0F, -4.3015F, -2.7899F, 5.0F, 9.0F, 9.0F, CubeDeformation.NONE), PartPose.offsetAndRotation(0.0F, 1.0F, 1.0F, -0.3491F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 128, 128);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Hand.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}