package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.PureWhiteLatexCerberusModel;
import net.ltxprogrammer.changed.client.renderer.model.PureWhiteLatexWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModelSet;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorPureWhiteLatexCerberusModel;
import net.ltxprogrammer.changed.entity.beast.PureWhiteLatexCerberus;
import net.ltxprogrammer.changed.entity.beast.PureWhiteLatexWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class PureWhiteLatexCerberusRenderer extends AdvancedHumanoidRenderer<PureWhiteLatexCerberus, PureWhiteLatexCerberusModel> {
	public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/pure_white_latex_wolf.png");

	public PureWhiteLatexCerberusRenderer(EntityRendererProvider.Context context) {
		super(context, new PureWhiteLatexCerberusModel(context.bakeLayer(PureWhiteLatexCerberusModel.LAYER_LOCATION)),
				ArmorModelSet.castOf(ArmorPureWhiteLatexCerberusModel.MODEL_SET, ArmorPureWhiteLatexCerberusModel::new), 0.7f);
		this.addLayer(new LatexParticlesLayer<>(this, getModel()));
		this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
		this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
	}

	@Override
	public ResourceLocation getTextureLocation(PureWhiteLatexCerberus entity) {
		return DEFAULT_SKIN_LOCATION;
	}
}