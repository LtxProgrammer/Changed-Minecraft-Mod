package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexMantaRayMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexSharkModel;
import net.ltxprogrammer.changed.entity.beast.LatexMantaRayMale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMantaRayMaleRenderer extends LatexHumanoidRenderer<LatexMantaRayMale, LatexMantaRayMaleModel, ArmorLatexSharkModel<LatexMantaRayMale>> {
    public LatexMantaRayMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMantaRayMaleModel(context.bakeLayer(LatexMantaRayMaleModel.LAYER_LOCATION)),
                ArmorLatexSharkModel::new, ArmorLatexSharkModel.INNER_ARMOR, ArmorLatexSharkModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMantaRayMale p_114482_) {
        return Changed.modResource("textures/latex_manta_ray_male.png");
    }
}
