package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexTrafficConeDragonModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexBigTailDragonModel;
import net.ltxprogrammer.changed.entity.beast.LatexTrafficConeDragon;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexTrafficConeDragonRenderer extends LatexHumanoidRenderer<LatexTrafficConeDragon, LatexTrafficConeDragonModel, ArmorLatexBigTailDragonModel<LatexTrafficConeDragon>> {
	public LatexTrafficConeDragonRenderer(EntityRendererProvider.Context context) {
		super(context, new LatexTrafficConeDragonModel(context.bakeLayer(LatexTrafficConeDragonModel.LAYER_LOCATION)),
				ArmorLatexBigTailDragonModel::new, ArmorLatexBigTailDragonModel.INNER_ARMOR, ArmorLatexBigTailDragonModel.OUTER_ARMOR, 0.5f);
		this.addLayer(new LatexParticlesLayer<>(this, this.model));
		this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(),
				CustomEyesLayer.fixedColor(Color3.parseHex("#ffea86")),
				CustomEyesLayer.fixedColor(Color3.BLACK)));
	}

	@Override
	public ResourceLocation getTextureLocation(LatexTrafficConeDragon p_114482_) {
		return Changed.modResource("textures/latex_traffic_cone_dragon.png");
	}
}