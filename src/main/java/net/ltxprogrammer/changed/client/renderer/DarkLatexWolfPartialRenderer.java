package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.DarkLatexMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexPartialLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.BlackGooWolfPartialModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.BlackGooWolfPartial;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DarkLatexWolfPartialRenderer extends AdvancedHumanoidRenderer<BlackGooWolfPartial, BlackGooWolfPartialModel, ArmorMaleWolfModel<BlackGooWolfPartial>> {
	public DarkLatexWolfPartialRenderer(EntityRendererProvider.Context context, boolean slim) {
		super(context, BlackGooWolfPartialModel.human(context.bakeLayer(
				slim ? BlackGooWolfPartialModel.LAYER_LOCATION_HUMAN_SLIM : BlackGooWolfPartialModel.LAYER_LOCATION_HUMAN)),
				ArmorMaleWolfModel::new, ArmorMaleWolfModel.INNER_ARMOR, ArmorMaleWolfModel.OUTER_ARMOR, 0.5f);
		this.addLayer(new LatexPartialLayer<>(this, BlackGooWolfPartialModel.latex(
				context.bakeLayer(slim ? BlackGooWolfPartialModel.LAYER_LOCATION_GOO_SLIM : BlackGooWolfPartialModel.LAYER_LOCATION_GOO)),
				slim ? Changed.modResource("textures/dark_latex_wolf_partial_slim.png") : Changed.modResource("textures/dark_latex_wolf_partial.png")));
		this.addLayer(new GooParticlesLayer<>(this, getModel()));
		this.addLayer(new DarkLatexMaskLayer<>(this, context.getModelSet()));
	}

	@Override
	public ResourceLocation getTextureLocation(BlackGooWolfPartial partial) {
		return partial.getSkinTextureLocation();
	}

	@Override
	public void render(BlackGooWolfPartial latex, float p_115456_, float p_115457_, PoseStack p_115458_, MultiBufferSource bufferSource, int p_115460_) {
		if (latex.getUnderlyingPlayer() instanceof AbstractClientPlayer clientPlayer)
			this.model.setModelProperties(clientPlayer);
		else
			this.model.defaultModelProperties();
		super.render(latex, p_115456_, p_115457_, p_115458_, bufferSource, p_115460_);
	}
}