package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.BlackGooWolfMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.BlackGooWolfMale;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BlackGooWolfMaleRenderer extends AdvancedHumanoidRenderer<BlackGooWolfMale, BlackGooWolfMaleModel, ArmorMaleWolfModel<BlackGooWolfMale>> {
	public BlackGooWolfMaleRenderer(EntityRendererProvider.Context context) {
		super(context, new BlackGooWolfMaleModel(context.bakeLayer(BlackGooWolfMaleModel.LAYER_LOCATION)),
				ArmorMaleWolfModel::new, ArmorMaleWolfModel.INNER_ARMOR, ArmorMaleWolfModel.OUTER_ARMOR, 0.5f);
		this.addLayer(new GooParticlesLayer<>(this, getModel(), model::isPartNotMask));
		this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer.fixedColor(Color3.parseHex("#242424")),
				CustomEyesLayer.fixedIfNotBlackGooOverrideLeft(Color3.WHITE),
				CustomEyesLayer.fixedIfNotBlackGooOverrideRight(Color3.WHITE)));
	}

	@Override
	public ResourceLocation getTextureLocation(BlackGooWolfMale p_114482_) {
		return Changed.modResource("textures/black_goo_wolf_male.png");
	}
}