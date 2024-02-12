package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.PureWhiteGooWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.PureWhiteGooWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class WhiteLatexWolfRenderer extends AdvancedHumanoidRenderer<PureWhiteGooWolf, PureWhiteGooWolfModel, ArmorMaleWolfModel<PureWhiteGooWolf>> {
	public WhiteLatexWolfRenderer(EntityRendererProvider.Context context) {
		super(context, new PureWhiteGooWolfModel(context.bakeLayer(PureWhiteGooWolfModel.LAYER_LOCATION)),
				ArmorMaleWolfModel::new, ArmorMaleWolfModel.INNER_ARMOR, ArmorMaleWolfModel.OUTER_ARMOR, 0.5f);
		this.addLayer(new GooParticlesLayer<>(this, getModel()));
	}

	@Override
	public ResourceLocation getTextureLocation(PureWhiteGooWolf p_114482_) {
		return Changed.modResource("textures/white_latex_drone.png");
	}
}