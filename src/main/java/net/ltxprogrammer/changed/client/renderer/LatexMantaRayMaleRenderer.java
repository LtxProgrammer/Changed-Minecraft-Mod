package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexMantaRayMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexSharkModel;
import net.ltxprogrammer.changed.entity.beast.GooMantaRayMale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMantaRayMaleRenderer extends AdvancedHumanoidRenderer<GooMantaRayMale, LatexMantaRayMaleModel, ArmorLatexSharkModel<GooMantaRayMale>> {
    public LatexMantaRayMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMantaRayMaleModel(context.bakeLayer(LatexMantaRayMaleModel.LAYER_LOCATION)),
                ArmorLatexSharkModel::new, ArmorLatexSharkModel.INNER_ARMOR, ArmorLatexSharkModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(GooMantaRayMale p_114482_) {
        return Changed.modResource("textures/latex_manta_ray_male.png");
    }

    public static class Remodel extends AdvancedHumanoidRenderer<GooMantaRayMale, LatexMantaRayMaleModel.Remodel, ArmorLatexSharkModel.RemodelMale<GooMantaRayMale>> {
        public Remodel(EntityRendererProvider.Context context) {
            super(context, new LatexMantaRayMaleModel.Remodel(context.bakeLayer(LatexMantaRayMaleModel.LAYER_LOCATION)),
                    ArmorLatexSharkModel.RemodelMale::new, ArmorLatexSharkModel.RemodelMale.INNER_ARMOR, ArmorLatexSharkModel.RemodelMale.OUTER_ARMOR, 0.5f);
        }

        @Override
        public ResourceLocation getTextureLocation(GooMantaRayMale p_114482_) {
            return Changed.modResource("textures/remodel/latex_manta_ray_male.png");
        }
    }
}
