package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.LatexMantaRayFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.LatexSirenModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorUpperBodyModel;
import net.ltxprogrammer.changed.entity.beast.LatexMantaRayFemale;
import net.ltxprogrammer.changed.entity.beast.LatexSiren;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSirenRenderer extends LatexHumanoidRenderer<LatexSiren, LatexSirenModel, ArmorUpperBodyModel<LatexSiren>> {
    public LatexSirenRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSirenModel(context.bakeLayer(LatexSirenModel.LAYER_LOCATION)),
                ArmorUpperBodyModel::new, ArmorUpperBodyModel.INNER_ARMOR, ArmorUpperBodyModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSiren p_114482_) {
        return new ResourceLocation("changed:textures/latex_siren.png");
    }
}
