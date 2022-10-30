package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.AerosolLatexWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.AerosolLatexWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class AerosolLatexWolfRenderer extends LatexHumanoidRenderer<AerosolLatexWolf, AerosolLatexWolfModel, ArmorLatexWolfModel<AerosolLatexWolf>> {
    public AerosolLatexWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new AerosolLatexWolfModel(context.bakeLayer( AerosolLatexWolfModel.LAYER_LOCATION)),
                ArmorLatexWolfModel::new, ArmorLatexWolfModel.INNER_ARMOR, ArmorLatexWolfModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(AerosolLatexWolf p_114482_) {
        return new ResourceLocation("changed:textures/aerosol_latex_wolf.png");
    }
}