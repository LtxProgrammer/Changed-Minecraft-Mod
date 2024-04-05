package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.layers.DarkLatexMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorHumanModel;
import net.ltxprogrammer.changed.entity.beast.LatexHuman;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexHumanRenderer extends AdvancedHumanoidRenderer<LatexHuman, LatexHumanModel, ArmorHumanModel<LatexHuman>> {
	public LatexHumanRenderer(EntityRendererProvider.Context context, boolean slim) {
		super(context, new LatexHumanModel(context.bakeLayer(
				slim ? LatexHumanModel.LAYER_LOCATION_SLIM : LatexHumanModel.LAYER_LOCATION)),
				ArmorHumanModel::new,
				slim ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR,
				slim ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR, 0.5f);
		this.addLayer(new LatexParticlesLayer<>(this, getModel()));
		this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
		this.addLayer(new DarkLatexMaskLayer<>(this, context.getModelSet()));
		this.addLayer(new GasMaskLayer<>(this, context.getModelSet()));
	}

	@Override
	public ResourceLocation getTextureLocation(LatexHuman human) {
		return human.getSkinTextureLocation();
	}

	@Override
	public void render(LatexHuman latex, float p_115456_, float p_115457_, PoseStack p_115458_, MultiBufferSource bufferSource, int p_115460_) {
		if (latex.getUnderlyingPlayer() instanceof AbstractClientPlayer clientPlayer)
			this.model.setModelProperties(clientPlayer);
		else
			this.model.defaultModelProperties();
		super.render(latex, p_115456_, p_115457_, p_115458_, bufferSource, p_115460_);
	}

	@Override
	protected void scale(LatexHuman entity, PoseStack pose, float partialTick) {
		float f = 0.9375F;
		pose.scale(0.9375F, 0.9375F, 0.9375F);
	}
}