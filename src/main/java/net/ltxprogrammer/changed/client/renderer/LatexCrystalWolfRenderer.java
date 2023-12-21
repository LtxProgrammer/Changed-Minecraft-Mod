package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexCrystalWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.LatexCrystalWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexCrystalWolfRenderer extends LatexHumanoidRenderer<LatexCrystalWolf, LatexCrystalWolfModel, ArmorLatexMaleWolfModel<LatexCrystalWolf>> {
    public LatexCrystalWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexCrystalWolfModel(context.bakeLayer(LatexCrystalWolfModel.LAYER_LOCATION)),
                ArmorLatexMaleWolfModel::new, ArmorLatexMaleWolfModel.INNER_ARMOR, ArmorLatexMaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer::always, CustomEyesLayer::always));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexCrystalWolf p_114482_) {
        return Changed.modResource("textures/latex_crystal_wolf.png");
    }
}