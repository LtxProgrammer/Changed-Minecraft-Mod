package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexSnowLeopardFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexSnowLeopardModel;
import net.ltxprogrammer.changed.entity.beast.LatexSnowLeopardFemale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSnowLeopardFemaleRenderer extends LatexHumanoidRenderer<LatexSnowLeopardFemale, LatexSnowLeopardFemaleModel, ArmorLatexSnowLeopardModel<LatexSnowLeopardFemale>> {
    public LatexSnowLeopardFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSnowLeopardFemaleModel(context.bakeLayer(LatexSnowLeopardFemaleModel.LAYER_LOCATION)),
                ArmorLatexSnowLeopardModel::new, ArmorLatexSnowLeopardModel.INNER_ARMOR, ArmorLatexSnowLeopardModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSnowLeopardFemale p_114482_) {
        return Changed.modResource("textures/latex_snow_leopard_female.png");
    }

    public static class Remodel extends LatexHumanoidRenderer<LatexSnowLeopardFemale, LatexSnowLeopardFemaleModel.Remodel, ArmorLatexSnowLeopardModel.RemodelFemale<LatexSnowLeopardFemale>> {
        public Remodel(EntityRendererProvider.Context context) {
            super(context, new LatexSnowLeopardFemaleModel.Remodel(context.bakeLayer(LatexSnowLeopardFemaleModel.LAYER_LOCATION)),
                    ArmorLatexSnowLeopardModel.RemodelFemale::new, ArmorLatexSnowLeopardModel.RemodelFemale.INNER_ARMOR, ArmorLatexSnowLeopardModel.RemodelFemale.OUTER_ARMOR, 0.5f);
        }

        @Override
        public ResourceLocation getTextureLocation(LatexSnowLeopardFemale p_114482_) {
            return Changed.modResource("textures/remodel/latex_snow_leopard_female.png");
        }
    }
}