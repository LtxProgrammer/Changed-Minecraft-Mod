package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.AerosolLatexWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.AerosolLatexWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class AerosolLatexWolfRenderer extends LatexHumanoidRenderer<AerosolLatexWolf, AerosolLatexWolfModel, ArmorLatexWolfModel<AerosolLatexWolf>> {
    public AerosolLatexWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new AerosolLatexWolfModel(context.bakeLayer( AerosolLatexWolfModel.LAYER_LOCATION)),
                ArmorLatexWolfModel::new, ArmorLatexWolfModel.INNER_ARMOR, ArmorLatexWolfModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(AerosolLatexWolf p_114482_) {
        return Changed.modResource("textures/latex_gas_wolf.png");
    }

    public static class Remodel extends LatexHumanoidRenderer<AerosolLatexWolf, AerosolLatexWolfModel.Remodel, ArmorLatexWolfModel.RemodelMale<AerosolLatexWolf>> {
        public Remodel(EntityRendererProvider.Context context) {
            super(context, new AerosolLatexWolfModel.Remodel(context.bakeLayer(AerosolLatexWolfModel.LAYER_LOCATION)),
                    ArmorLatexWolfModel.RemodelMale::new, ArmorLatexWolfModel.RemodelMale.INNER_ARMOR, ArmorLatexWolfModel.RemodelMale.OUTER_ARMOR, 0.5f);
        }

        @Override
        public ResourceLocation getTextureLocation(AerosolLatexWolf p_114482_) {
            return Changed.modResource("textures/remodel/gas_wolf.png");
        }
    }
}