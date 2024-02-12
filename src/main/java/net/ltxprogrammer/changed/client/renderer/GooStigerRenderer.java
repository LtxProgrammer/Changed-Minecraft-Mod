package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.GooStigerModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexStigerModel;
import net.ltxprogrammer.changed.entity.beast.GooStiger;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GooStigerRenderer extends AdvancedHumanoidRenderer<GooStiger, GooStigerModel, ArmorLatexStigerModel<GooStiger>> {
    public GooStigerRenderer(EntityRendererProvider.Context context) {
        super(context, new GooStigerModel(context.bakeLayer(GooStigerModel.LAYER_LOCATION)),
                ArmorLatexStigerModel::new, ArmorLatexStigerModel.INNER_ARMOR, ArmorLatexStigerModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(GooStiger p_114482_) {
        return Changed.modResource("textures/latex_stiger.png");
    }
}