package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexMingCatModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleCatModel;
import net.ltxprogrammer.changed.entity.beast.LatexMingCat;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMingCatRenderer extends LatexHumanoidRenderer<LatexMingCat, LatexMingCatModel, ArmorLatexMaleCatModel<LatexMingCat>> {
    public LatexMingCatRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMingCatModel(context.bakeLayer(LatexMingCatModel.LAYER_LOCATION)),
                ArmorLatexMaleCatModel::new, ArmorLatexMaleCatModel.INNER_ARMOR, ArmorLatexMaleCatModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMingCat p_114482_) {
        return Changed.modResource("textures/latex_ming_cat.png");
    }
}