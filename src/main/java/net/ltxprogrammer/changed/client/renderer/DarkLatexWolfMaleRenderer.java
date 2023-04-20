package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.DarkLatexWolfMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.DarkLatexWolfMale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DarkLatexWolfMaleRenderer extends LatexHumanoidRenderer<DarkLatexWolfMale, DarkLatexWolfMaleModel, ArmorLatexWolfModel<DarkLatexWolfMale>> {
	public DarkLatexWolfMaleRenderer(EntityRendererProvider.Context context) {
		super(context, new DarkLatexWolfMaleModel(context.bakeLayer(DarkLatexWolfMaleModel.LAYER_LOCATION)),
				ArmorLatexWolfModel::new, ArmorLatexWolfModel.INNER_ARMOR, ArmorLatexWolfModel.OUTER_ARMOR, 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(DarkLatexWolfMale p_114482_) {
		return Changed.modResource("textures/dark_latex_wolf_male.png");
	}

	public static class Remodel extends LatexHumanoidRenderer<DarkLatexWolfMale, DarkLatexWolfMaleModel.Remodel, ArmorLatexWolfModel<DarkLatexWolfMale>> {
		public Remodel(EntityRendererProvider.Context context) {
			super(context, new DarkLatexWolfMaleModel.Remodel(context.bakeLayer(DarkLatexWolfMaleModel.LAYER_LOCATION)),
					ArmorLatexWolfModel::new, ArmorLatexWolfModel.INNER_ARMOR, ArmorLatexWolfModel.OUTER_ARMOR, 0.5f);
		}

		@Override
		public ResourceLocation getTextureLocation(DarkLatexWolfMale p_114482_) {
			return Changed.modResource("textures/remodel/dark_latex_wolf_male.png");
		}
	}
}