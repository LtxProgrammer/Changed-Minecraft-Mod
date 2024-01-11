package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexBlueWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.LatexBlueWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexBlueWolfRenderer extends LatexHumanoidRenderer<LatexBlueWolf, LatexBlueWolfModel, ArmorLatexFemaleWolfModel<LatexBlueWolf>> {
    public LatexBlueWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexBlueWolfModel(context.bakeLayer(LatexBlueWolfModel.LAYER_LOCATION)),
                ArmorLatexFemaleWolfModel::new, ArmorLatexFemaleWolfModel.INNER_ARMOR, ArmorLatexFemaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexBlueWolf p_114482_) {
        return Changed.modResource("textures/latex_blue_wolf.png");
    }
}