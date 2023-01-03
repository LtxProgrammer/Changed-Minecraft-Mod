package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexBlueWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.LightLatexWolfFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.LatexBlueWolf;
import net.ltxprogrammer.changed.entity.beast.LightLatexWolfFemale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexBlueWolfRenderer extends LatexHumanoidRenderer<LatexBlueWolf, LatexBlueWolfModel, ArmorLatexWolfModel<LatexBlueWolf>> {
    public LatexBlueWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexBlueWolfModel(context.bakeLayer(LatexBlueWolfModel.LAYER_LOCATION)),
                ArmorLatexWolfModel::new, ArmorLatexWolfModel.INNER_ARMOR, ArmorLatexWolfModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexBlueWolf p_114482_) {
        return Changed.modResource("textures/latex_blue_wolf.png");
    }
}