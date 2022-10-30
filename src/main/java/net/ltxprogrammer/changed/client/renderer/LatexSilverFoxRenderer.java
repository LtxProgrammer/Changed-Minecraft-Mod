package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.LatexSilverFoxModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.LatexSilverFox;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSilverFoxRenderer extends LatexHumanoidRenderer<LatexSilverFox, LatexSilverFoxModel, ArmorLatexWolfModel<LatexSilverFox>> {
    public LatexSilverFoxRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSilverFoxModel(context.bakeLayer(LatexSilverFoxModel.LAYER_LOCATION)),
                ArmorLatexWolfModel::new, ArmorLatexWolfModel.INNER_ARMOR, ArmorLatexWolfModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSilverFox p_114482_) {
        return new ResourceLocation("changed:textures/latex_silver_fox.png");
    }
}