package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexSnowLeopardFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleCatModel;
import net.ltxprogrammer.changed.entity.beast.LatexSnowLeopardFemale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSnowLeopardFemaleRenderer extends AdvancedHumanoidRenderer<LatexSnowLeopardFemale, LatexSnowLeopardFemaleModel, ArmorLatexFemaleCatModel<LatexSnowLeopardFemale>> {
    public LatexSnowLeopardFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSnowLeopardFemaleModel(context.bakeLayer(LatexSnowLeopardFemaleModel.LAYER_LOCATION)), ArmorLatexFemaleCatModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSnowLeopardFemale p_114482_) {
        return Changed.modResource("textures/latex_snow_leopard_female.png");
    }
}