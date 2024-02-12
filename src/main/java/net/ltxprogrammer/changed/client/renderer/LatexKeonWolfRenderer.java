package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexKeonWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.GooKeonWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexKeonWolfRenderer extends AdvancedHumanoidRenderer<GooKeonWolf, LatexKeonWolfModel, ArmorMaleWolfModel<GooKeonWolf>> {
    public LatexKeonWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexKeonWolfModel(context.bakeLayer(LatexKeonWolfModel.LAYER_LOCATION)),
                ArmorMaleWolfModel::new, ArmorMaleWolfModel.INNER_ARMOR, ArmorMaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(GooKeonWolf p_114482_) {
        return Changed.modResource("textures/latex_keon.png");
    }
}