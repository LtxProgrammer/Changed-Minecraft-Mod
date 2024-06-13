package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.PureWhiteLatexWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.PureWhiteLatexWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class PureWhiteLatexWolfRenderer extends AdvancedHumanoidRenderer<PureWhiteLatexWolf, PureWhiteLatexWolfModel, ArmorLatexMaleWolfModel<PureWhiteLatexWolf>> {
	public PureWhiteLatexWolfRenderer(EntityRendererProvider.Context context) {
		super(context, new PureWhiteLatexWolfModel(context.bakeLayer(PureWhiteLatexWolfModel.LAYER_LOCATION)),
				ArmorLatexMaleWolfModel::new, ArmorLatexMaleWolfModel.INNER_ARMOR, ArmorLatexMaleWolfModel.OUTER_ARMOR, 0.5f);
		this.addLayer(new LatexParticlesLayer<>(this, getModel()));
		this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
		this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
	}

	@Override
	public ResourceLocation getTextureLocation(PureWhiteLatexWolf p_114482_) {
		return Changed.modResource("textures/pure_white_latex_wolf.png");
	}
}