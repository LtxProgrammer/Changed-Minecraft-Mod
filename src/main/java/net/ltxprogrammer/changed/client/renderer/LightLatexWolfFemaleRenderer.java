package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LightLatexWolfFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.LightLatexWolfFemale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LightLatexWolfFemaleRenderer extends LatexHumanoidRenderer<LightLatexWolfFemale, LightLatexWolfFemaleModel, ArmorLatexFemaleWolfModel<LightLatexWolfFemale>> {
    public LightLatexWolfFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LightLatexWolfFemaleModel(context.bakeLayer(LightLatexWolfFemaleModel.LAYER_LOCATION)),
                ArmorLatexFemaleWolfModel::new, ArmorLatexFemaleWolfModel.INNER_ARMOR, ArmorLatexFemaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LightLatexWolfFemale p_114482_) {
        return Changed.modResource("textures/light_latex_wolf_female.png");
    }
}