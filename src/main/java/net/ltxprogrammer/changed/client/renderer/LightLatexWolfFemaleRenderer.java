package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LightLatexWolfFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.LightLatexWolfFemale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LightLatexWolfFemaleRenderer extends LatexHumanoidRenderer<LightLatexWolfFemale, LightLatexWolfFemaleModel, ArmorLatexWolfModel<LightLatexWolfFemale>> {
    public LightLatexWolfFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LightLatexWolfFemaleModel(context.bakeLayer(LightLatexWolfFemaleModel.LAYER_LOCATION)),
                ArmorLatexWolfModel::new, ArmorLatexWolfModel.INNER_ARMOR, ArmorLatexWolfModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LightLatexWolfFemale p_114482_) {
        return Changed.modResource("textures/light_latex_wolf_female.png");
    }
}