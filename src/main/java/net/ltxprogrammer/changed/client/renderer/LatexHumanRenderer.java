package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.layers.DarkLatexMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.GooHumanModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.GooHuman;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexHumanRenderer extends AdvancedHumanoidRenderer<GooHuman, GooHumanModel, ArmorMaleWolfModel<GooHuman>> {
	public LatexHumanRenderer(EntityRendererProvider.Context context, boolean slim) {
		super(context, new GooHumanModel(context.bakeLayer(
				slim ? GooHumanModel.LAYER_LOCATION_SLIM : GooHumanModel.LAYER_LOCATION)),
				ArmorMaleWolfModel::new, ArmorMaleWolfModel.INNER_ARMOR, ArmorMaleWolfModel.OUTER_ARMOR, 0.5f);
		this.addLayer(new GooParticlesLayer<>(this, getModel()));
		this.addLayer(new DarkLatexMaskLayer<>(this, context.getModelSet()));
	}

	@Override
	public ResourceLocation getTextureLocation(GooHuman human) {
		return human.getSkinTextureLocation();
	}

	@Override
	public void render(GooHuman latex, float p_115456_, float p_115457_, PoseStack p_115458_, MultiBufferSource bufferSource, int p_115460_) {
		if (latex.getUnderlyingPlayer() instanceof AbstractClientPlayer clientPlayer)
			this.model.setModelProperties(clientPlayer);
		else
			this.model.defaultModelProperties();
		super.render(latex, p_115456_, p_115457_, p_115458_, bufferSource, p_115460_);
	}
}