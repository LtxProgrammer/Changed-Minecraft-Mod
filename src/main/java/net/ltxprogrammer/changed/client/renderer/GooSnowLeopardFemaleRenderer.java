package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.GooSnowLeopardFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleCatModel;
import net.ltxprogrammer.changed.entity.beast.GooSnowLeopardFemale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GooSnowLeopardFemaleRenderer extends AdvancedHumanoidRenderer<GooSnowLeopardFemale, GooSnowLeopardFemaleModel, ArmorLatexFemaleCatModel<GooSnowLeopardFemale>> {
    public GooSnowLeopardFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new GooSnowLeopardFemaleModel(context.bakeLayer(GooSnowLeopardFemaleModel.LAYER_LOCATION)),
                ArmorLatexFemaleCatModel::new, ArmorLatexFemaleCatModel.INNER_ARMOR, ArmorLatexFemaleCatModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new GooParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(GooSnowLeopardFemale p_114482_) {
        return Changed.modResource("textures/latex_snow_leopard_female.png");
    }
}