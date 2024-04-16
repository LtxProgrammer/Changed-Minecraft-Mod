package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexTranslucentLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.PhageLatexWolfFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.PhageLatexWolfFemale;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class PhageLatexWolfFemaleRenderer extends AdvancedHumanoidRenderer<PhageLatexWolfFemale, PhageLatexWolfFemaleModel, ArmorLatexFemaleWolfModel<PhageLatexWolfFemale>> {
	public PhageLatexWolfFemaleRenderer(EntityRendererProvider.Context context) {
		super(context, new PhageLatexWolfFemaleModel(context.bakeLayer(PhageLatexWolfFemaleModel.LAYER_LOCATION)),
				ArmorLatexFemaleWolfModel::new, ArmorLatexFemaleWolfModel.INNER_ARMOR, ArmorLatexFemaleWolfModel.OUTER_ARMOR, 0.5f);
		this.addLayer(new LatexParticlesLayer<>(this, getModel(), model::isPartNotMask));
		this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
		this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer.fixedColor(Color3.parseHex("#242424")),
				CustomEyesLayer.fixedIfNotDarkLatexOverrideLeft(Color3.WHITE),
				CustomEyesLayer.fixedIfNotDarkLatexOverrideRight(Color3.WHITE)));
		this.addLayer(new LatexTranslucentLayer<>(this, model, Changed.modResource("textures/phage_latex_wolf_female_translucent.png")));
	}

	@Override
	public ResourceLocation getTextureLocation(PhageLatexWolfFemale p_114482_) {
		return Changed.modResource("textures/phage_latex_wolf_female.png");
	}
}