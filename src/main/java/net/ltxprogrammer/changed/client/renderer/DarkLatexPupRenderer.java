package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.DarkLatexPupModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorNoneModel;
import net.ltxprogrammer.changed.entity.beast.DarkLatexPup;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DarkLatexPupRenderer extends LatexHumanoidRenderer<DarkLatexPup, DarkLatexPupModel, ArmorNoneModel<DarkLatexPup>> {
	public DarkLatexPupRenderer(EntityRendererProvider.Context context) {
		super(context, new DarkLatexPupModel(context.bakeLayer(DarkLatexPupModel.LAYER_LOCATION)),
				ArmorNoneModel::new, ArmorNoneModel.INNER_ARMOR, ArmorNoneModel.OUTER_ARMOR, 0.4F);
	}

	@Override
	public ResourceLocation getTextureLocation(DarkLatexPup entity) {
		return Changed.modResource("textures/dark_latex_pup.png");
	}
}