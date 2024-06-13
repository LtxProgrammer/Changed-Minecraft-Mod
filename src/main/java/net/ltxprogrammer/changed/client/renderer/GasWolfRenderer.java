package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomCoatLayer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.GasWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.GasWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GasWolfRenderer extends AdvancedHumanoidRenderer<GasWolf, GasWolfModel, ArmorLatexMaleWolfModel<GasWolf>> {
    public GasWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new GasWolfModel(context.bakeLayer( GasWolfModel.LAYER_LOCATION)),
                ArmorLatexMaleWolfModel::new, ArmorLatexMaleWolfModel.INNER_ARMOR, ArmorLatexMaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomCoatLayer<>(this, this.getModel(), Changed.modResource("textures/gas_wolf_coat")));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(GasWolf p_114482_) {
        return Changed.modResource("textures/gas_wolf.png");
    }
}