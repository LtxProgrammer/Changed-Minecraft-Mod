package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexBenignWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.WhiteLatexWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.LatexBenignWolf;
import net.ltxprogrammer.changed.entity.beast.WhiteLatexWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexBenignWolfRenderer extends LatexHumanoidRenderer<LatexBenignWolf, LatexBenignWolfModel, ArmorLatexWolfModel<LatexBenignWolf>> {
    public LatexBenignWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexBenignWolfModel(context.bakeLayer(LatexBenignWolfModel.LAYER_LOCATION)),
                ArmorLatexWolfModel::new, ArmorLatexWolfModel.INNER_ARMOR, ArmorLatexWolfModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexBenignWolf p_114482_) {
        return Changed.modResource("textures/latex_benign_wolf.png");
    }
}