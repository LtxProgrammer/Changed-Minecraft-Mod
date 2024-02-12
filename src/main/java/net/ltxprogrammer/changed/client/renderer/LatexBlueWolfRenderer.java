package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.BlueGooWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorFemaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.BlueGooWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexBlueWolfRenderer extends AdvancedHumanoidRenderer<BlueGooWolf, BlueGooWolfModel, ArmorFemaleWolfModel<BlueGooWolf>> {
    public LatexBlueWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new BlueGooWolfModel(context.bakeLayer(BlueGooWolfModel.LAYER_LOCATION)),
                ArmorFemaleWolfModel::new, ArmorFemaleWolfModel.INNER_ARMOR, ArmorFemaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new GooParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(BlueGooWolf p_114482_) {
        return Changed.modResource("textures/latex_blue_wolf.png");
    }
}