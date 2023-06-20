package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexSniperDogModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.LatexSniperDog;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSniperDogRenderer extends LatexHumanoidRenderer<LatexSniperDog, LatexSniperDogModel, ArmorLatexWolfModel<LatexSniperDog>> {
    public LatexSniperDogRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSniperDogModel(context.bakeLayer(LatexSniperDogModel.LAYER_LOCATION)),
                ArmorLatexWolfModel::new, ArmorLatexWolfModel.INNER_ARMOR, ArmorLatexWolfModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSniperDog p_114482_) {
        return Changed.modResource("textures/latex_sniper_dog.png");
    }

    public static class Remodel extends LatexHumanoidRenderer<LatexSniperDog, LatexSniperDogModel.Remodel, ArmorLatexWolfModel.RemodelMale<LatexSniperDog>> {
        public Remodel(EntityRendererProvider.Context context) {
            super(context, new LatexSniperDogModel.Remodel(context.bakeLayer(LatexSniperDogModel.LAYER_LOCATION)),
                    ArmorLatexWolfModel.RemodelMale::new, ArmorLatexWolfModel.RemodelMale.INNER_ARMOR, ArmorLatexWolfModel.RemodelMale.OUTER_ARMOR, 0.5f);
        }

        @Override
        public ResourceLocation getTextureLocation(LatexSniperDog p_114482_) {
            return Changed.modResource("textures/remodel/latex_sniper_dog.png");
        }
    }
}