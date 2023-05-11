package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
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
		return Changed.modResource("textures/dark_latex_wolf_female.png");
	}

	public static class Remodel extends LatexHumanoidRenderer<DarkLatexWolfFemale, DarkLatexWolfFemaleModel.Remodel, ArmorLatexWolfModel.RemodelFemale<DarkLatexWolfFemale>> {
		public Remodel(EntityRendererProvider.Context context) {
			super(context, new DarkLatexWolfFemaleModel.Remodel(context.bakeLayer(DarkLatexWolfFemaleModel.LAYER_LOCATION)),
					ArmorLatexWolfModel.RemodelFemale::new, ArmorLatexWolfModel.RemodelFemale.INNER_ARMOR, ArmorLatexWolfModel.RemodelFemale.OUTER_ARMOR, 0.5f);
		}

		@Override
		public ResourceLocation getTextureLocation(DarkLatexWolfFemale p_114482_) {
			return Changed.modResource("textures/remodel/dark_latex_wolf_female.png");
		}
	}
}