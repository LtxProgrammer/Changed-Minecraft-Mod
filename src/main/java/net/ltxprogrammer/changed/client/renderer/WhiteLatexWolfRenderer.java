package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.WhiteLatexWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.WhiteLatexWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class WhiteLatexWolfRenderer extends LatexHumanoidRenderer<WhiteLatexWolf, WhiteLatexWolfModel, ArmorLatexWolfModel<WhiteLatexWolf>> {
	public WhiteLatexWolfRenderer(EntityRendererProvider.Context context) {
		super(context, new WhiteLatexWolfModel(context.bakeLayer(WhiteLatexWolfModel.LAYER_LOCATION)),
				ArmorLatexWolfModel::new, ArmorLatexWolfModel.INNER_ARMOR, ArmorLatexWolfModel.OUTER_ARMOR, 0.5f);
		this.addLayer(new LatexParticlesLayer<>(this, getModel()));
	}

	@Override
	public ResourceLocation getTextureLocation(WhiteLatexWolf p_114482_) {
		return Changed.modResource("textures/white_latex_drone.png");
	}
}