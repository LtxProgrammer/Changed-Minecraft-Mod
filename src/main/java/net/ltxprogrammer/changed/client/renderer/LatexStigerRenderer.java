package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.LatexStigerModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorNoneModel;
import net.ltxprogrammer.changed.entity.beast.LatexStiger;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexStigerRenderer extends LatexHumanoidRenderer<LatexStiger, LatexStigerModel, ArmorNoneModel<LatexStiger>> {
    public LatexStigerRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexStigerModel(context.bakeLayer(LatexStigerModel.LAYER_LOCATION)),
                ArmorNoneModel::new, ArmorNoneModel.INNER_ARMOR, ArmorNoneModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexStiger p_114482_) {
        return new ResourceLocation("changed:textures/latex_stiger.png");
    }
}