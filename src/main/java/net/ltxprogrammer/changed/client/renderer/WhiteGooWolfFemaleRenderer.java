package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.WhiteGooWolfFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorFemaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.WhiteGooWolfFemale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class WhiteGooWolfFemaleRenderer extends AdvancedHumanoidRenderer<WhiteGooWolfFemale, WhiteGooWolfFemaleModel, ArmorFemaleWolfModel<WhiteGooWolfFemale>> {
    public WhiteGooWolfFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new WhiteGooWolfFemaleModel(context.bakeLayer(WhiteGooWolfFemaleModel.LAYER_LOCATION)),
                ArmorFemaleWolfModel::new, ArmorFemaleWolfModel.INNER_ARMOR, ArmorFemaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new GooParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(WhiteGooWolfFemale p_114482_) {
        return Changed.modResource("textures/white_goo_wolf_female.png");
    }
}