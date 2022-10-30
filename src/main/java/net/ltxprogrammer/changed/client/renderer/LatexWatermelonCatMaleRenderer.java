package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.LatexWatermelonCatMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.LatexWatermelonCatMale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexWatermelonCatMaleRenderer extends LatexHumanoidRenderer<LatexWatermelonCatMale, LatexWatermelonCatMaleModel, ArmorLatexWolfModel<LatexWatermelonCatMale>> {
    public LatexWatermelonCatMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexWatermelonCatMaleModel(context.bakeLayer(LatexWatermelonCatMaleModel.LAYER_LOCATION)),
                ArmorLatexWolfModel::new, ArmorLatexWolfModel.INNER_ARMOR, ArmorLatexWolfModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexWatermelonCatMale p_114482_) {
        return new ResourceLocation("changed:textures/latex_watermelon_cat_male.png");
    }
}