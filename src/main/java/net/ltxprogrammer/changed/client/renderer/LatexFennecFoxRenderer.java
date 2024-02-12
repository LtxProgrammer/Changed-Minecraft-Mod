package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexFennecFoxModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.GooFennecFox;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexFennecFoxRenderer extends AdvancedHumanoidRenderer<GooFennecFox, LatexFennecFoxModel, ArmorMaleWolfModel<GooFennecFox>> {
    public LatexFennecFoxRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexFennecFoxModel(context.bakeLayer(LatexFennecFoxModel.LAYER_LOCATION)),
                ArmorMaleWolfModel::new, ArmorMaleWolfModel.INNER_ARMOR, ArmorMaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new GooParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(GooFennecFox p_114482_) {
        return Changed.modResource("textures/latex_fennec_fox.png");
    }
}