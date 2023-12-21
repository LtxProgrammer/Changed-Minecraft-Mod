package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexCrystalWolfHornedModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.LatexCrystalWolfHorned;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexCrystalWolfHornedRenderer extends LatexHumanoidRenderer<LatexCrystalWolfHorned, LatexCrystalWolfHornedModel, ArmorLatexWolfModel<LatexCrystalWolfHorned>> {
    public LatexCrystalWolfHornedRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexCrystalWolfHornedModel(context.bakeLayer(LatexCrystalWolfHornedModel.LAYER_LOCATION)),
                ArmorLatexWolfModel::new, ArmorLatexWolfModel.INNER_ARMOR, ArmorLatexWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer::always, CustomEyesLayer::always));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexCrystalWolfHorned p_114482_) {
        return Changed.modResource("textures/latex_crystal_wolf_horned.png");
    }
}