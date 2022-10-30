package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.LightLatexKnightFusionModel;
import net.ltxprogrammer.changed.client.renderer.model.LightLatexWolfMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLightLatexKnightFusionModel;
import net.ltxprogrammer.changed.entity.beast.LightLatexKnight;
import net.ltxprogrammer.changed.entity.beast.LightLatexKnightFusion;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LightLatexKnightFusionRenderer extends LatexHumanoidRenderer<LightLatexKnightFusion, LightLatexKnightFusionModel, ArmorLightLatexKnightFusionModel> {
    public LightLatexKnightFusionRenderer(EntityRendererProvider.Context context) {
        super(context, new LightLatexKnightFusionModel(context.bakeLayer(LightLatexKnightFusionModel.LAYER_LOCATION)),
                ArmorLightLatexKnightFusionModel::new, ArmorLightLatexKnightFusionModel.INNER_ARMOR, ArmorLightLatexKnightFusionModel.OUTER_ARMOR, 0.6f);
    }

    @Override
    public ResourceLocation getTextureLocation(LightLatexKnightFusion p_114482_) {
        return new ResourceLocation("changed:textures/light_latex_knight_fusion.png");
    }
}