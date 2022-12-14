package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.LatexMantaRayFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorNoneModel;
import net.ltxprogrammer.changed.entity.beast.LatexMantaRayFemale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMantaRayFemaleRenderer extends LatexHumanoidRenderer<LatexMantaRayFemale, LatexMantaRayFemaleModel, ArmorNoneModel<LatexMantaRayFemale>> {
    public LatexMantaRayFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMantaRayFemaleModel(context.bakeLayer(LatexMantaRayFemaleModel.LAYER_LOCATION)),
                ArmorNoneModel::new, ArmorNoneModel.INNER_ARMOR, ArmorNoneModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMantaRayFemale p_114482_) {
        return new ResourceLocation("changed:textures/latex_manta_ray_female.png");
    }
}
