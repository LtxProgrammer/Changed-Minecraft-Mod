// Made with Blockbench 4.5.2
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports


public class custom_model<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "custom_model"), "main");
	private final ModelPart Head;

	public custom_model(ModelPart root) {
		this.Head = root.getChild("Head");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Head = partdefinition.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 96).addBox(-24.0F, -48.0F, -24.0F, 48.0F, 48.0F, 48.0F, CubeDeformation.NONE)
		.texOffs(0, 0).addBox(-24.0F, -48.0F, -24.0F, 48.0F, 48.0F, 48.0F, CubeDeformation.NONE)
		.texOffs(240, 30).addBox(-12.0F, -18.0F, -36.0F, 24.0F, 12.0F, 12.0F, CubeDeformation.NONE)
		.texOffs(216, 174).addBox(-9.0F, -6.0F, -30.0F, 18.0F, 6.0F, 12.0F, CubeDeformation.NONE), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition Hair = Head.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(144, 48).addBox(-12.0F, -48.0F, -24.0F, 24.0F, 6.0F, 48.0F, CubeDeformation.NONE)
		.texOffs(0, 192).addBox(15.0F, -42.0F, -18.0F, 12.0F, 18.0F, 42.0F, CubeDeformation.NONE)
		.texOffs(96, 240).addBox(21.0F, -42.0F, -29.0F, 6.0F, 12.0F, 12.0F, CubeDeformation.NONE)
		.texOffs(240, 54).addBox(-27.0F, -42.0F, -29.0F, 6.0F, 12.0F, 12.0F, CubeDeformation.NONE)
		.texOffs(120, 210).addBox(15.0F, -24.0F, -6.0F, 12.0F, 12.0F, 30.0F, CubeDeformation.NONE)
		.texOffs(150, 150).addBox(-27.0F, -42.0F, -18.0F, 12.0F, 18.0F, 42.0F, CubeDeformation.NONE)
		.texOffs(66, 192).addBox(-27.0F, -24.0F, -6.0F, 12.0F, 12.0F, 30.0F, CubeDeformation.NONE)
		.texOffs(144, 102).addBox(-24.0F, -43.0F, 24.0F, 48.0F, 36.0F, 6.0F, CubeDeformation.NONE)
		.texOffs(144, 0).addBox(-24.0F, -46.0F, -24.0F, 48.0F, 6.0F, 24.0F, CubeDeformation.NONE)
		.texOffs(144, 30).addBox(-24.0F, -42.0F, -29.0F, 48.0F, 6.0F, 6.0F, CubeDeformation.NONE)
		.texOffs(174, 210).addBox(-21.0F, -36.0F, -28.0F, 42.0F, 6.0F, 6.0F, CubeDeformation.NONE)
		.texOffs(204, 222).addBox(12.0F, -48.0F, 0.0F, 12.0F, 6.0F, 24.0F, CubeDeformation.NONE)
		.texOffs(216, 144).addBox(-24.0F, -48.0F, 0.0F, 12.0F, 6.0F, 24.0F, CubeDeformation.NONE), PartPose.offset(0.0F, -6.0F, 0.0F));

		PartDefinition LeftEar = Head.addOrReplaceChild("LeftEar", CubeListBuilder.create().texOffs(0, 24).addBox(-1.0F, -16.0F, -1.0F, 18.0F, 18.0F, 6.0F, new CubeDeformation(0.05F))
		.texOffs(0, 192).addBox(-1.0F, -18.6F, 2.6F, 12.0F, 24.0F, 6.0F, new CubeDeformation(0.04F))
		.texOffs(0, 222).addBox(-1.0F, -22.1F, -1.0F, 12.0F, 6.0F, 6.0F, new CubeDeformation(0.05F))
		.texOffs(144, 12).addBox(-1.0F, -27.9F, -1.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(8.6F, -44.475F, -3.0F, 0.0F, 0.0F, 0.2618F));

		PartDefinition RightEar = Head.addOrReplaceChild("RightEar", CubeListBuilder.create().texOffs(0, 0).addBox(-16.495F, -16.0F, -1.0F, 18.0F, 18.0F, 6.0F, new CubeDeformation(0.05F))
		.texOffs(0, 96).addBox(-10.495F, -18.6F, 2.6F, 12.0F, 24.0F, 6.0F, new CubeDeformation(0.04F))
		.texOffs(0, 126).addBox(-10.495F, -22.1F, -1.0F, 12.0F, 6.0F, 6.0F, new CubeDeformation(0.05F))
		.texOffs(144, 0).addBox(-4.495F, -28.0F, -1.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.05F)), PartPose.offsetAndRotation(-9.075F, -44.25F, -3.0F, 0.0F, 0.0F, -0.2618F));

		return LayerDefinition.create(meshdefinition, 512, 512);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}