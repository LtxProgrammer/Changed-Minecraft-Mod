// Made with Blockbench 4.5.2
// Exported for Minecraft version 1.17 - 1.18 with Mojang mappings
// Paste this class into your mod and generate all required imports


public class custom_model<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "custom_model"), "main");
	private final ModelPart ChestPack;

	public custom_model(ModelPart root) {
		this.ChestPack = root.getChild("ChestPack");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition ChestPack = partdefinition.addOrReplaceChild("ChestPack", CubeListBuilder.create().texOffs(0, 16).addBox(2.5F, -1.0F, -3.5F, 4.0F, 8.0F, 8.0F, new CubeDeformation(-0.95F)), PartPose.offset(0.0F, 24.0F, 0.0F));

		PartDefinition LowerTorso_r1 = ChestPack.addOrReplaceChild("LowerTorso_r1", CubeListBuilder.create().texOffs(0, 0).addBox(-6.5F, -11.25F, -3.0F, 4.0F, 8.0F, 8.0F, new CubeDeformation(-0.95F)), PartPose.offsetAndRotation(0.0F, 2.0F, -7.0F, -1.5708F, 0.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		ChestPack.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}