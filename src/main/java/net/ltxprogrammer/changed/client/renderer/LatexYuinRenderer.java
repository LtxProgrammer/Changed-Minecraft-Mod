package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.LatexYuinModel;
import net.ltxprogrammer.changed.client.renderer.model.LightLatexWolfMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexYuinModel;
import net.ltxprogrammer.changed.entity.beast.LatexYuin;
import net.ltxprogrammer.changed.entity.beast.LightLatexWolfMale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexYuinRenderer extends LatexHumanoidRenderer<LatexYuin, LatexYuinModel, ArmorLatexYuinModel<LatexYuin>> {
    public LatexYuinRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexYuinModel(context.bakeLayer(LatexYuinModel.LAYER_LOCATION)),
                ArmorLatexYuinModel::new, ArmorLatexYuinModel.INNER_ARMOR, ArmorLatexYuinModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexYuin p_114482_) {
        return new ResourceLocation("changed:textures/latex_yuin.png");
    }
}