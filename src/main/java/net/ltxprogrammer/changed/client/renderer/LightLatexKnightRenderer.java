package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.LightLatexKnightModel;
import net.ltxprogrammer.changed.client.renderer.model.LightLatexWolfMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLightLatexKnightModel;
import net.ltxprogrammer.changed.entity.beast.LightLatexKnight;
import net.ltxprogrammer.changed.entity.beast.LightLatexWolfMale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LightLatexKnightRenderer extends LatexHumanoidRenderer<LightLatexKnight, LightLatexKnightModel, ArmorLightLatexKnightModel> {
    public LightLatexKnightRenderer(EntityRendererProvider.Context context) {
        super(context, new LightLatexKnightModel(context.bakeLayer(LightLatexKnightModel.LAYER_LOCATION)),
                ArmorLightLatexKnightModel::new, ArmorLightLatexKnightModel.INNER_ARMOR, ArmorLightLatexKnightModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LightLatexKnight p_114482_) {
        return new ResourceLocation("changed:textures/light_latex_knight.png");
    }
}