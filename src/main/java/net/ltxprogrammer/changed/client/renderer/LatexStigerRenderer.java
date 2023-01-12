package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexStigerModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexStigerModel;
import net.ltxprogrammer.changed.entity.beast.LatexStiger;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexStigerRenderer extends LatexHumanoidRenderer<LatexStiger, LatexStigerModel, ArmorLatexStigerModel<LatexStiger>> {
    public LatexStigerRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexStigerModel(context.bakeLayer(LatexStigerModel.LAYER_LOCATION)),
                ArmorLatexStigerModel::new, ArmorLatexStigerModel.INNER_ARMOR, ArmorLatexStigerModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexStiger p_114482_) {
        return Changed.modResource("textures/latex_stiger.png");
    }
}