package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexWhiteTigerModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleCatModel;
import net.ltxprogrammer.changed.entity.beast.LatexWhiteTiger;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexWhiteTigerRenderer extends LatexHumanoidRenderer<LatexWhiteTiger, LatexWhiteTigerModel, ArmorLatexMaleCatModel<LatexWhiteTiger>> {
    public LatexWhiteTigerRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexWhiteTigerModel(context.bakeLayer(LatexWhiteTigerModel.LAYER_LOCATION)),
                ArmorLatexMaleCatModel::new, ArmorLatexMaleCatModel.INNER_ARMOR, ArmorLatexMaleCatModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexWhiteTiger p_114482_) {
        return Changed.modResource("textures/latex_white_tiger.png");
    }
}