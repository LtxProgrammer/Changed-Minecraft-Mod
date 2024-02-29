package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TaurChestPackLayer;
import net.ltxprogrammer.changed.client.renderer.model.WhiteGooCentaurModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLightLatexCentaurModel;
import net.ltxprogrammer.changed.entity.beast.WhiteGooCentaur;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;

public class LightLatexCentaurRenderer extends AdvancedHumanoidRenderer<WhiteGooCentaur, WhiteGooCentaurModel, ArmorLightLatexCentaurModel> {
    public LightLatexCentaurRenderer(EntityRendererProvider.Context context) {
        super(context, new WhiteGooCentaurModel(context.bakeLayer(WhiteGooCentaurModel.LAYER_LOCATION)),
                ArmorLightLatexCentaurModel::new, ArmorLightLatexCentaurModel.INNER_ARMOR, ArmorLightLatexCentaurModel.OUTER_ARMOR, 0.7f);
        this.addLayer(new SaddleLayer<>(this, getModel(), Changed.modResource("textures/light_latex_centaur_saddle.png")));
        this.addLayer(new TaurChestPackLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(WhiteGooCentaur p_114482_) {
        return Changed.modResource("textures/light_latex_centaur.png");
    }
}