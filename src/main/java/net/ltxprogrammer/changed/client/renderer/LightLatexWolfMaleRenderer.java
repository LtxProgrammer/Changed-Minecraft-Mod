package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LightLatexWolfMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.LightLatexWolfMale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LightLatexWolfMaleRenderer extends LatexHumanoidRenderer<LightLatexWolfMale, LightLatexWolfMaleModel, ArmorLatexWolfModel<LightLatexWolfMale>> {
    public LightLatexWolfMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LightLatexWolfMaleModel(context.bakeLayer(LightLatexWolfMaleModel.LAYER_LOCATION)),
                ArmorLatexWolfModel::new, ArmorLatexWolfModel.INNER_ARMOR, ArmorLatexWolfModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LightLatexWolfMale p_114482_) {
        return Changed.modResource("textures/light_latex_wolf_male.png");
    }

    public static class Remodel extends LatexHumanoidRenderer<LightLatexWolfMale, LightLatexWolfMaleModel.Remodel, ArmorLatexWolfModel.RemodelMale<LightLatexWolfMale>> {
        public Remodel(EntityRendererProvider.Context context) {
            super(context, new LightLatexWolfMaleModel.Remodel(context.bakeLayer(LightLatexWolfMaleModel.LAYER_LOCATION)),
                    ArmorLatexWolfModel.RemodelMale::new, ArmorLatexWolfModel.RemodelMale.INNER_ARMOR, ArmorLatexWolfModel.RemodelMale.OUTER_ARMOR, 0.5f);
        }

        @Override
        public ResourceLocation getTextureLocation(LightLatexWolfMale p_114482_) {
            return Changed.modResource("textures/remodel/light_latex_wolf_male.png");
        }
    }
}