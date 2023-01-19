package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexMedusaCatModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexSnowLeopardModel;
import net.ltxprogrammer.changed.entity.beast.LatexMedusaCat;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMedusaCatRenderer extends LatexHumanoidRenderer<LatexMedusaCat, LatexMedusaCatModel, ArmorLatexSnowLeopardModel<LatexMedusaCat>> {
    public LatexMedusaCatRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMedusaCatModel(context.bakeLayer(LatexMedusaCatModel.LAYER_LOCATION)),
                ArmorLatexSnowLeopardModel::new, ArmorLatexSnowLeopardModel.INNER_ARMOR, ArmorLatexSnowLeopardModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMedusaCat p_114482_) {
        return Changed.modResource("textures/latex_medusa_cat.png");
    }
}