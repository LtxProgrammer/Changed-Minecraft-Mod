package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.DarkLatexWolfFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.DarkLatexWolfFemale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DarkLatexWolfFemaleRenderer extends LatexHumanoidRenderer<DarkLatexWolfFemale, DarkLatexWolfFemaleModel, ArmorLatexWolfModel<DarkLatexWolfFemale>> {
	public DarkLatexWolfFemaleRenderer(EntityRendererProvider.Context context) {
		super(context, new DarkLatexWolfFemaleModel(context.bakeLayer(DarkLatexWolfFemaleModel.LAYER_LOCATION)),
				ArmorLatexWolfModel::new, ArmorLatexWolfModel.INNER_ARMOR, ArmorLatexWolfModel.OUTER_ARMOR, 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(DarkLatexWolfFemale p_114482_) {
		return new ResourceLocation("changed:textures/dark_latex_wolf.png");
	}
}