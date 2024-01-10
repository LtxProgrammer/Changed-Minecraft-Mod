package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexMedusaCatModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleCatModel;
import net.ltxprogrammer.changed.entity.beast.LatexMedusaCat;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMedusaCatRenderer extends LatexHumanoidRenderer<LatexMedusaCat, LatexMedusaCatModel, ArmorLatexFemaleCatModel<LatexMedusaCat>> {
    public LatexMedusaCatRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMedusaCatModel(context.bakeLayer(LatexMedusaCatModel.LAYER_LOCATION)),
                ArmorLatexFemaleCatModel::new, ArmorLatexFemaleCatModel.INNER_ARMOR, ArmorLatexFemaleCatModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer.fixedColor(Color3.WHITE),CustomEyesLayer.fixedColor(Color3.parseHex("#f64967"))));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMedusaCat p_114482_) {
        return Changed.modResource("textures/latex_medusa_cat.png");
    }
}