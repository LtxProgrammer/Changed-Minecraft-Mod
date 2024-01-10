package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.DarkLatexWolfMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.DarkLatexWolfMale;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DarkLatexWolfMaleRenderer extends LatexHumanoidRenderer<DarkLatexWolfMale, DarkLatexWolfMaleModel, ArmorLatexMaleWolfModel<DarkLatexWolfMale>> {
	public DarkLatexWolfMaleRenderer(EntityRendererProvider.Context context) {
		super(context, new DarkLatexWolfMaleModel(context.bakeLayer(DarkLatexWolfMaleModel.LAYER_LOCATION)),
				ArmorLatexMaleWolfModel::new, ArmorLatexMaleWolfModel.INNER_ARMOR, ArmorLatexMaleWolfModel.OUTER_ARMOR, 0.5f);
		this.addLayer(new LatexParticlesLayer<>(this, getModel()));
		this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer.fixedColor(Color3.parseHex("#303030")),
				CustomEyesLayer.fixedIfNotDarkLatexOverride(Color3.WHITE)));
	}

	@Override
	public ResourceLocation getTextureLocation(DarkLatexWolfMale p_114482_) {
		return Changed.modResource("textures/dark_latex_wolf_male.png");
	}
}