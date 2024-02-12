package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.GooSnowLeopardMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleCatModel;
import net.ltxprogrammer.changed.entity.beast.GooSnowLeopardMale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GooSnowLeopardMaleRenderer extends AdvancedHumanoidRenderer<GooSnowLeopardMale, GooSnowLeopardMaleModel, ArmorLatexMaleCatModel<GooSnowLeopardMale>> {
    public GooSnowLeopardMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new GooSnowLeopardMaleModel(context.bakeLayer(GooSnowLeopardMaleModel.LAYER_LOCATION)),
                ArmorLatexMaleCatModel::new, ArmorLatexMaleCatModel.INNER_ARMOR, ArmorLatexMaleCatModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new GooParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(GooSnowLeopardMale p_114482_) {
        return Changed.modResource("textures/latex_snow_leopard_male.png");
    }
}