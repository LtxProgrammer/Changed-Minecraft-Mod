package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexMantaRayFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorAbdomenModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorUpperBodyModel;
import net.ltxprogrammer.changed.entity.beast.LatexMantaRayFemale;
import net.ltxprogrammer.changed.item.AbdomenArmor;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMantaRayFemaleRenderer extends LatexHumanoidRenderer<LatexMantaRayFemale, LatexMantaRayFemaleModel, ArmorUpperBodyModel<LatexMantaRayFemale>> {
    public LatexMantaRayFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMantaRayFemaleModel(context.bakeLayer(LatexMantaRayFemaleModel.LAYER_LOCATION)),
                ArmorUpperBodyModel::new, ArmorUpperBodyModel.INNER_ARMOR, ArmorUpperBodyModel.OUTER_ARMOR,
                ArmorAbdomenModel::new, ArmorAbdomenModel.INNER_ARMOR, ArmorAbdomenModel.OUTER_ARMOR,
                AbdomenArmor::useAbdomenModel, AbdomenArmor::useInnerAbdomenModel, LatexSnakeRenderer::setVisibility, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMantaRayFemale p_114482_) {
        return Changed.modResource("textures/latex_manta_ray_female.png");
    }
}
