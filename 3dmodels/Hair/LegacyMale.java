// Made with Blockbench 4.6.4
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


public class LegacyMale<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("modid", "legacymale"), "main");
	private final ModelPart Hair;
	private final ModelPart bb_main;

	public LegacyMale(ModelPart root) {
		this.Hair = root.getChild("Hair");
		this.bb_main = root.getChild("bb_main");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition Hair = partdefinition.addOrReplaceChild("Hair", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -5.25F, -3.5F, 4.0F, 1.0F, 8.0F, new CubeDeformation(0.4F))
		.texOffs(24, 0).addBox(-4.0F, -5.0F, -3.25F, 8.0F, 1.0F, 4.0F, new CubeDeformation(0.2F))
		.texOffs(0, 25).addBox(3.75F, -4.25F, -3.1F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.401F))
		.texOffs(32, 14).addBox(-3.5F, -3.45F, -4.35F, 7.0F, 1.0F, 1.0F, new CubeDeformation(0.2F))
		.texOffs(0, 3).addBox(3.95F, -4.45F, -4.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.2F))
		.texOffs(0, 0).addBox(-4.9F, -4.45F, -4.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.2F))
		.texOffs(32, 7).addBox(3.75F, -1.0F, -0.1F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.4F))
		.texOffs(29, 29).addBox(-4.7F, -1.0F, -0.1F, 1.0F, 2.0F, 5.0F, new CubeDeformation(0.4F))
		.texOffs(16, 22).addBox(-4.7F, -4.25F, -3.1F, 1.0F, 3.0F, 8.0F, new CubeDeformation(0.401F))
		.texOffs(16, 16).addBox(-4.0F, -5.5F, -0.2F, 8.0F, 1.0F, 5.0F, new CubeDeformation(0.1F))
		.texOffs(24, 5).addBox(-4.0F, -4.55F, -4.6F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.1F))
		.texOffs(26, 22).addBox(-4.0F, -4.25F, 4.05F, 8.0F, 6.0F, 1.0F, new CubeDeformation(0.4F)), PartPose.offset(0.0F, 20.0F, 0.0F));

		PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

		return LayerDefinition.create(meshdefinition, 64, 64);
	}

	@Override
	public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		Hair.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
		bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}