package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexTrafficConeDragonModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexBigTailDragonModel;
import net.ltxprogrammer.changed.entity.beast.LatexTrafficConeDragon;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexTrafficConeDragonRenderer extends AdvancedHumanoidRenderer<LatexTrafficConeDragon, LatexTrafficConeDragonModel, ArmorLatexBigTailDragonModel<LatexTrafficConeDragon>> {
	public LatexTrafficConeDragonRenderer(EntityRendererProvider.Context context) {
		super(context, new LatexTrafficConeDragonModel(context.bakeLayer(LatexTrafficConeDragonModel.LAYER_LOCATION)), ArmorLatexBigTailDragonModel.MODEL_SET, 0.5f);
		this.addLayer(new LatexParticlesLayer<>(this, this.model));
		this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
		this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
				.withSclera(Color3.fromInt(0xffea86)).withIris(Color3.BLACK).build());
		this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
	}

	@Override
	public ResourceLocation getTextureLocation(LatexTrafficConeDragon p_114482_) {
		return Changed.modResource("textures/latex_traffic_cone_dragon.png");
	}
}