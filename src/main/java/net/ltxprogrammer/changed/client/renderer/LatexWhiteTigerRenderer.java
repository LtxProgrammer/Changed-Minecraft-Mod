package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.WhiteGooTigerModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleCatModel;
import net.ltxprogrammer.changed.entity.beast.LatexWhiteTiger;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexWhiteTigerRenderer extends AdvancedHumanoidRenderer<LatexWhiteTiger, WhiteGooTigerModel, ArmorLatexMaleCatModel<LatexWhiteTiger>> {
    public LatexWhiteTigerRenderer(EntityRendererProvider.Context context) {
        super(context, new WhiteGooTigerModel(context.bakeLayer(WhiteGooTigerModel.LAYER_LOCATION)),
                ArmorLatexMaleCatModel::new, ArmorLatexMaleCatModel.INNER_ARMOR, ArmorLatexMaleCatModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new GooParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexWhiteTiger p_114482_) {
        return Changed.modResource("textures/latex_white_tiger.png");
    }
}