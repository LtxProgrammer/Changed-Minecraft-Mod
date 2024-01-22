package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.DarkLatexWolfFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.DarkLatexWolfFemale;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DarkLatexWolfFemaleRenderer extends LatexHumanoidRenderer<DarkLatexWolfFemale, DarkLatexWolfFemaleModel, ArmorLatexFemaleWolfModel<DarkLatexWolfFemale>> {
	public DarkLatexWolfFemaleRenderer(EntityRendererProvider.Context context) {
		super(context, new DarkLatexWolfFemaleModel(context.bakeLayer(DarkLatexWolfFemaleModel.LAYER_LOCATION)),
				ArmorLatexFemaleWolfModel::new, ArmorLatexFemaleWolfModel.INNER_ARMOR, ArmorLatexFemaleWolfModel.OUTER_ARMOR, 0.5f);
		this.addLayer(new LatexParticlesLayer<>(this, getModel()));
		this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer.fixedColor(Color3.parseHex("#303030")),
				CustomEyesLayer.fixedIfNotDarkLatexOverrideLeft(Color3.WHITE),
				CustomEyesLayer.fixedIfNotDarkLatexOverrideRight(Color3.WHITE)));
	}

	@Override
	public ResourceLocation getTextureLocation(DarkLatexWolfFemale p_114482_) {
		return Changed.modResource("textures/dark_latex_wolf_female.png");
	}
}