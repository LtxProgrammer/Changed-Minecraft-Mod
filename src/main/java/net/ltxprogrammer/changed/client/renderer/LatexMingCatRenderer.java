package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexMingCatModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexSnowLeopardModel;
import net.ltxprogrammer.changed.entity.beast.LatexMingCat;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMingCatRenderer extends LatexHumanoidRenderer<LatexMingCat, LatexMingCatModel, ArmorLatexSnowLeopardModel<LatexMingCat>> {
    public LatexMingCatRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMingCatModel(context.bakeLayer(LatexMingCatModel.LAYER_LOCATION)),
                ArmorLatexSnowLeopardModel::new, ArmorLatexSnowLeopardModel.INNER_ARMOR, ArmorLatexSnowLeopardModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMingCat p_114482_) {
        return Changed.modResource("textures/latex_ming_cat.png");
    }
}