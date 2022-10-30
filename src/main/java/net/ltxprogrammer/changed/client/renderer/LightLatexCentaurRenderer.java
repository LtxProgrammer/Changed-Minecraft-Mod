package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.LightLatexCentaurModel;
import net.ltxprogrammer.changed.client.renderer.model.LightLatexWolfMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLightLatexCentaurModel;
import net.ltxprogrammer.changed.entity.beast.LightLatexCentaur;
import net.ltxprogrammer.changed.entity.beast.LightLatexKnight;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LightLatexCentaurRenderer extends LatexHumanoidRenderer<LightLatexCentaur, LightLatexCentaurModel, ArmorLightLatexCentaurModel> {
    public LightLatexCentaurRenderer(EntityRendererProvider.Context context) {
        super(context, new LightLatexCentaurModel(context.bakeLayer(LightLatexCentaurModel.LAYER_LOCATION)),
                ArmorLightLatexCentaurModel::new, ArmorLightLatexCentaurModel.INNER_ARMOR, ArmorLightLatexCentaurModel.OUTER_ARMOR, 0.7f);
    }

    @Override
    public ResourceLocation getTextureLocation(LightLatexCentaur p_114482_) {
        return new ResourceLocation("changed:textures/light_latex_centaur.png");
    }
}