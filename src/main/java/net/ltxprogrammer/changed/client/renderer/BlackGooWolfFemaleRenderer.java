package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.BlackGooWolfFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorFemaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.BlackGooWolfFemale;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BlackGooWolfFemaleRenderer extends AdvancedHumanoidRenderer<BlackGooWolfFemale, BlackGooWolfFemaleModel, ArmorFemaleWolfModel<BlackGooWolfFemale>> {
	public BlackGooWolfFemaleRenderer(EntityRendererProvider.Context context) {
		super(context, new BlackGooWolfFemaleModel(context.bakeLayer(BlackGooWolfFemaleModel.LAYER_LOCATION)),
				ArmorFemaleWolfModel::new, ArmorFemaleWolfModel.INNER_ARMOR, ArmorFemaleWolfModel.OUTER_ARMOR, 0.5f);
		this.addLayer(new GooParticlesLayer<>(this, getModel(), model::isPartNotMask));
		this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer.fixedColor(Color3.parseHex("#242424")),
				CustomEyesLayer.fixedIfNotBlackGooOverrideLeft(Color3.WHITE),
				CustomEyesLayer.fixedIfNotBlackGooOverrideRight(Color3.WHITE)));
	}

	@Override
	public ResourceLocation getTextureLocation(BlackGooWolfFemale p_114482_) {
		return Changed.modResource("textures/dark_latex_wolf_female.png");
	}
}