package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.LatexPartialLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.DarkLatexWolfPartialModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.DarkLatexWolfPartial;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DarkLatexWolfPartialRenderer extends LatexHumanoidRenderer<DarkLatexWolfPartial, DarkLatexWolfPartialModel, ArmorLatexMaleWolfModel<DarkLatexWolfPartial>> {
	public DarkLatexWolfPartialRenderer(EntityRendererProvider.Context context) {
		super(context, new DarkLatexWolfPartialModel(context.bakeLayer(DarkLatexWolfPartialModel.LAYER_LOCATION_HUMAN)),
				ArmorLatexMaleWolfModel::new, ArmorLatexMaleWolfModel.INNER_ARMOR, ArmorLatexMaleWolfModel.OUTER_ARMOR, 0.5f);
		this.addLayer(new LatexPartialLayer<>(this, new DarkLatexWolfPartialModel(context.bakeLayer(DarkLatexWolfPartialModel.LAYER_LOCATION_LATEX)), Changed.modResource("textures/dark_latex_wolf_partial.png")));
		this.addLayer(new LatexParticlesLayer<>(this, getModel()));
	}

	@Override
	public ResourceLocation getTextureLocation(DarkLatexWolfPartial partial) {
		var underlying = partial.getUnderlyingPlayer();
		if (!(underlying instanceof AbstractClientPlayer clientPlayer))
			return Changed.modResource("textures/dark_latex_wolf_male.png"); // Fallback
		return clientPlayer.getSkinTextureLocation();
	}
}