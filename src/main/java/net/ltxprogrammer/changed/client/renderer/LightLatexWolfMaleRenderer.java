package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.WhiteGooWolfMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.WhiteGooWolfMale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LightLatexWolfMaleRenderer extends AdvancedHumanoidRenderer<WhiteGooWolfMale, WhiteGooWolfMaleModel, ArmorMaleWolfModel<WhiteGooWolfMale>> {
    public LightLatexWolfMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new WhiteGooWolfMaleModel(context.bakeLayer(WhiteGooWolfMaleModel.LAYER_LOCATION)),
                ArmorMaleWolfModel::new, ArmorMaleWolfModel.INNER_ARMOR, ArmorMaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new GooParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(WhiteGooWolfMale p_114482_) {
        return Changed.modResource("textures/light_latex_wolf_male.png");
    }
}