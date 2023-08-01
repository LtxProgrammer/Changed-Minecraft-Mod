package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexTrafficConeDragonModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexTrafficConeDragonModel;
import net.ltxprogrammer.changed.entity.beast.LatexTrafficConeDragon;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexTrafficConeDragonRenderer extends LatexHumanoidRenderer<LatexTrafficConeDragon, LatexTrafficConeDragonModel, ArmorLatexTrafficConeDragonModel<LatexTrafficConeDragon>> {
	public LatexTrafficConeDragonRenderer(EntityRendererProvider.Context context) {
		super(context, new LatexTrafficConeDragonModel(context.bakeLayer(LatexTrafficConeDragonModel.LAYER_LOCATION)),
				ArmorLatexTrafficConeDragonModel::new, ArmorLatexTrafficConeDragonModel.INNER_ARMOR, ArmorLatexTrafficConeDragonModel.OUTER_ARMOR, 0.5f);
	}

	@Override
	public ResourceLocation getTextureLocation(LatexTrafficConeDragon p_114482_) {
		return Changed.modResource("textures/latex_traffic_cone_dragon.png");
	}

	public static class Remodel extends LatexHumanoidRenderer<LatexTrafficConeDragon, LatexTrafficConeDragonModel.Remodel, ArmorLatexTrafficConeDragonModel.RemodelMale<LatexTrafficConeDragon>> {
		public Remodel(EntityRendererProvider.Context context) {
			super(context, new LatexTrafficConeDragonModel.Remodel(context.bakeLayer(LatexTrafficConeDragonModel.LAYER_LOCATION)),
					ArmorLatexTrafficConeDragonModel.RemodelMale::new, ArmorLatexTrafficConeDragonModel.RemodelMale.INNER_ARMOR, ArmorLatexTrafficConeDragonModel.RemodelMale.OUTER_ARMOR, 0.5f);
		}

		@Override
		public ResourceLocation getTextureLocation(LatexTrafficConeDragon p_114482_) {
			return Changed.modResource("textures/remodel/latex_traffic_cone_dragon.png");
		}
	}
}