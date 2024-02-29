package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexSnowLeopardMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleCatModel;
import net.ltxprogrammer.changed.entity.beast.LatexSnowLeopardMale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSnowLeopardMaleRenderer extends LatexHumanoidRenderer<LatexSnowLeopardMale, LatexSnowLeopardMaleModel, ArmorLatexMaleCatModel<LatexSnowLeopardMale>> {
    public LatexSnowLeopardMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSnowLeopardMaleModel(context.bakeLayer(LatexSnowLeopardMaleModel.LAYER_LOCATION)),
                ArmorLatexMaleCatModel::new, ArmorLatexMaleCatModel.INNER_ARMOR, ArmorLatexMaleCatModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSnowLeopardMale p_114482_) {
        return Changed.modResource("textures/latex_snow_leopard_male.png");
    }
}