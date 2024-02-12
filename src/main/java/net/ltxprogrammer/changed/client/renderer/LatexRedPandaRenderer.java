package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexRedPandaModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleCatModel;
import net.ltxprogrammer.changed.entity.beast.GooRedPanda;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexRedPandaRenderer extends AdvancedHumanoidRenderer<GooRedPanda, LatexRedPandaModel, ArmorLatexMaleCatModel<GooRedPanda>> {
    public LatexRedPandaRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexRedPandaModel(context.bakeLayer(LatexRedPandaModel.LAYER_LOCATION)),
                ArmorLatexMaleCatModel::new, ArmorLatexMaleCatModel.INNER_ARMOR, ArmorLatexMaleCatModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new GooParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(GooRedPanda p_114482_) {
        return Changed.modResource("textures/latex_red_panda.png");
    }
}