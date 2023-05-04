package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexPurpleFoxModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.LatexPurpleFox;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexPurpleFoxRenderer extends LatexHumanoidRenderer<LatexPurpleFox, LatexPurpleFoxModel, ArmorLatexWolfModel<LatexPurpleFox>> {
    public LatexPurpleFoxRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexPurpleFoxModel(context.bakeLayer(LatexPurpleFoxModel.LAYER_LOCATION)),
                ArmorLatexWolfModel::new, ArmorLatexWolfModel.INNER_ARMOR, ArmorLatexWolfModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexPurpleFox p_114482_) {
        return Changed.modResource("textures/latex_purple_fox.png");
    }

    public static class Remodel extends LatexHumanoidRenderer<LatexPurpleFox, LatexPurpleFoxModel.Remodel, ArmorLatexWolfModel.RemodelMale<LatexPurpleFox>> {
        public Remodel(EntityRendererProvider.Context context) {
            super(context, new LatexPurpleFoxModel.Remodel(context.bakeLayer(LatexPurpleFoxModel.LAYER_LOCATION)),
                    ArmorLatexWolfModel.RemodelMale::new, ArmorLatexWolfModel.RemodelMale.INNER_ARMOR, ArmorLatexWolfModel.RemodelMale.OUTER_ARMOR, 0.5f);
        }

        @Override
        public ResourceLocation getTextureLocation(LatexPurpleFox p_114482_) {
            return Changed.modResource("textures/remodel/latex_purple_fox.png");
        }
    }
}