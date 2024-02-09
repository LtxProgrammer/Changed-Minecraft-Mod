package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.DarkLatexMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexPartialLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.DarkLatexWolfPartialModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.DarkLatexWolfPartial;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.PlayerModelPart;

public class DarkLatexWolfPartialRenderer extends LatexHumanoidRenderer<DarkLatexWolfPartial, DarkLatexWolfPartialModel, ArmorLatexMaleWolfModel<DarkLatexWolfPartial>> {
	public DarkLatexWolfPartialRenderer(EntityRendererProvider.Context context, boolean slim) {
		super(context, DarkLatexWolfPartialModel.human(context.bakeLayer(
				slim ? DarkLatexWolfPartialModel.LAYER_LOCATION_HUMAN_SLIM : DarkLatexWolfPartialModel.LAYER_LOCATION_HUMAN)),
				ArmorLatexMaleWolfModel::new, ArmorLatexMaleWolfModel.INNER_ARMOR, ArmorLatexMaleWolfModel.OUTER_ARMOR, 0.5f);
		this.addLayer(new LatexPartialLayer<>(this, DarkLatexWolfPartialModel.latex(
				context.bakeLayer(slim ? DarkLatexWolfPartialModel.LAYER_LOCATION_LATEX_SLIM : DarkLatexWolfPartialModel.LAYER_LOCATION_LATEX)),
				slim ? Changed.modResource("textures/dark_latex_wolf_partial_slim.png") : Changed.modResource("textures/dark_latex_wolf_partial.png")));
		this.addLayer(new LatexParticlesLayer<>(this, getModel()));
		this.addLayer(new DarkLatexMaskLayer<>(this, context.getModelSet()));
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
}