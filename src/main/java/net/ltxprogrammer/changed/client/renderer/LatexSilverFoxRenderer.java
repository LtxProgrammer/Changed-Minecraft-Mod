package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexSilverFoxModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.LatexSilverFox;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSilverFoxRenderer extends LatexHumanoidRenderer<LatexSilverFox, LatexSilverFoxModel, ArmorLatexMaleWolfModel<net.ltxprogrammer.changed.entity.beast.LatexSilverFox>> {
    public LatexSilverFoxRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSilverFoxModel(context.bakeLayer(LatexSilverFoxModel.LAYER_LOCATION)),
                ArmorLatexMaleWolfModel::new, ArmorLatexMaleWolfModel.INNER_ARMOR, ArmorLatexMaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSilverFox p_114482_) {
        return Changed.modResource("textures/latex_keon.png");
    }
}