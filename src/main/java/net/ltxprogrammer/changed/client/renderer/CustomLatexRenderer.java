package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.CustomLatexModel;
import net.ltxprogrammer.changed.client.renderer.model.PureWhiteLatexWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.CustomLatexEntity;
import net.ltxprogrammer.changed.entity.beast.PureWhiteLatexWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class CustomLatexRenderer extends AdvancedHumanoidRenderer<CustomLatexEntity, CustomLatexModel, ArmorLatexMaleWolfModel<CustomLatexEntity>> {
	public CustomLatexRenderer(EntityRendererProvider.Context context) {
		super(context, new CustomLatexModel(context.bakeLayer(CustomLatexModel.LAYER_LOCATION)), ArmorLatexMaleWolfModel.MODEL_SET, 0.5f);
		this.addLayer(new LatexParticlesLayer<>(this, getModel()));
		this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
		this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
	}

	@Override
	public ResourceLocation getTextureLocation(CustomLatexEntity p_114482_) {
		return Changed.modResource("textures/custom_latex.png");
	}
}