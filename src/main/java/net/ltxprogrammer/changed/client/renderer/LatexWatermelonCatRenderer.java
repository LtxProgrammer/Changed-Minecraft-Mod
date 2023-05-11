package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexWatermelonCatModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.LatexWatermelonCat;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexWatermelonCatRenderer extends LatexHumanoidRenderer<LatexWatermelonCat, LatexWatermelonCatModel, ArmorLatexWolfModel<LatexWatermelonCat>> {
    public LatexWatermelonCatRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexWatermelonCatModel(context.bakeLayer(LatexWatermelonCatModel.LAYER_LOCATION)),
                ArmorLatexWolfModel::new, ArmorLatexWolfModel.INNER_ARMOR, ArmorLatexWolfModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexWatermelonCat p_114482_) {
        return Changed.modResource("textures/latex_watermelon_cat.png");
    }

    public static class Remodel extends LatexHumanoidRenderer<LatexWatermelonCat, LatexWatermelonCatModel.Remodel, ArmorLatexWolfModel.RemodelFemale<LatexWatermelonCat>> {
        public Remodel(EntityRendererProvider.Context context) {
            super(context, new LatexWatermelonCatModel.Remodel(context.bakeLayer(LatexWatermelonCatModel.LAYER_LOCATION)),
                    ArmorLatexWolfModel.RemodelFemale::new, ArmorLatexWolfModel.RemodelFemale.INNER_ARMOR, ArmorLatexWolfModel.RemodelFemale.OUTER_ARMOR, 0.5f);
        }

        @Override
        public ResourceLocation getTextureLocation(LatexWatermelonCat p_114482_) {
            return Changed.modResource("textures/remodel/latex_watermelon_cat.png");
        }
    }
}