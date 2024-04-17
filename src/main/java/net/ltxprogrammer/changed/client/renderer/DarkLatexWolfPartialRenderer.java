package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.*;
import net.ltxprogrammer.changed.client.renderer.model.DarkLatexWolfPartialModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.DarkLatexWolfPartial;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DarkLatexWolfPartialRenderer extends AdvancedHumanoidRenderer<DarkLatexWolfPartial, DarkLatexWolfPartialModel, ArmorLatexMaleWolfModel<DarkLatexWolfPartial>> {
	public DarkLatexWolfPartialRenderer(EntityRendererProvider.Context context, boolean slim) {
		super(context, DarkLatexWolfPartialModel.human(context.bakeLayer(
				slim ? DarkLatexWolfPartialModel.LAYER_LOCATION_HUMAN_SLIM : DarkLatexWolfPartialModel.LAYER_LOCATION_HUMAN)),
				ArmorLatexMaleWolfModel::new, ArmorLatexMaleWolfModel.INNER_ARMOR, ArmorLatexMaleWolfModel.OUTER_ARMOR, 0.5f);
		var partialModel = new LatexPartialLayer<>(this, DarkLatexWolfPartialModel.latex(
				context.bakeLayer(slim ? DarkLatexWolfPartialModel.LAYER_LOCATION_LATEX_SLIM : DarkLatexWolfPartialModel.LAYER_LOCATION_LATEX)),
				slim ? Changed.modResource("textures/dark_latex_wolf_partial_slim.png") : Changed.modResource("textures/dark_latex_wolf_partial.png"));
		this.addLayer(partialModel);
		this.addLayer(new LatexParticlesLayer<>(this).addModel(partialModel.getModel(), entity -> partialModel.getTexture()));
		this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
		this.addLayer(new DarkLatexMaskLayer<>(this, context.getModelSet()));
		this.addLayer(new GasMaskLayer<>(this, context.getModelSet()));
	}

	@Override
	public ResourceLocation getTextureLocation(DarkLatexWolfPartial partial) {
		return partial.getSkinTextureLocation();
	}

	@Override
	public void render(DarkLatexWolfPartial latex, float p_115456_, float p_115457_, PoseStack p_115458_, MultiBufferSource bufferSource, int p_115460_) {
		if (latex.getUnderlyingPlayer() instanceof AbstractClientPlayer clientPlayer)
			this.model.setModelProperties(clientPlayer);
		else
			this.model.defaultModelProperties();
		super.render(latex, p_115456_, p_115457_, p_115458_, bufferSource, p_115460_);
	}

	@Override
	protected void scale(DarkLatexWolfPartial entity, PoseStack pose, float partialTick) {
		float f = 0.9375F;
		pose.scale(0.9375F, 0.9375F, 0.9375F);
	}
}