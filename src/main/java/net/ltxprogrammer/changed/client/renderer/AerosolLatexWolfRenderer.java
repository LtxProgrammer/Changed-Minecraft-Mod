package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomCoatLayer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.model.AerosolLatexWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.AerosolLatexWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class AerosolLatexWolfRenderer extends AdvancedHumanoidRenderer<AerosolLatexWolf, AerosolLatexWolfModel, ArmorMaleWolfModel<AerosolLatexWolf>> {
    public AerosolLatexWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new AerosolLatexWolfModel(context.bakeLayer( AerosolLatexWolfModel.LAYER_LOCATION)),
                ArmorMaleWolfModel::new, ArmorMaleWolfModel.INNER_ARMOR, ArmorMaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new CustomCoatLayer<>(this, this.getModel(), Changed.modResource("textures/latex_gas_wolf_coat")));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(AerosolLatexWolf p_114482_) {
        return Changed.modResource("textures/latex_gas_wolf.png");
    }
}