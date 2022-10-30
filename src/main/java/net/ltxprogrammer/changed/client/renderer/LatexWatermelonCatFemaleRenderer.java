package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.LatexWatermelonCatFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.LatexWatermelonCatFemale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexWatermelonCatFemaleRenderer extends LatexHumanoidRenderer<LatexWatermelonCatFemale, LatexWatermelonCatFemaleModel, ArmorLatexWolfModel<LatexWatermelonCatFemale>> {
    public LatexWatermelonCatFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexWatermelonCatFemaleModel(context.bakeLayer(LatexWatermelonCatFemaleModel.LAYER_LOCATION)),
                ArmorLatexWolfModel::new, ArmorLatexWolfModel.INNER_ARMOR, ArmorLatexWolfModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexWatermelonCatFemale p_114482_) {
        return new ResourceLocation("changed:textures/latex_watermelon_cat_female.png");
    }
}