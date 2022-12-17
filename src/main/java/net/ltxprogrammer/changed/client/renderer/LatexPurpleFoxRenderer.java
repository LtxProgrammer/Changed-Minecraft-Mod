package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.LatexPurpleFoxModel;
import net.ltxprogrammer.changed.client.renderer.model.LatexSilverFoxModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.LatexPurpleFox;
import net.ltxprogrammer.changed.entity.beast.LatexSilverFox;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexPurpleFoxRenderer extends LatexHumanoidRenderer<LatexPurpleFox, LatexPurpleFoxModel, ArmorLatexWolfModel<LatexPurpleFox>> {
    public LatexPurpleFoxRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexPurpleFoxModel(context.bakeLayer(LatexPurpleFoxModel.LAYER_LOCATION)),
                ArmorLatexWolfModel::new, ArmorLatexWolfModel.INNER_ARMOR, ArmorLatexWolfModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexPurpleFox p_114482_) {
        return new ResourceLocation("changed:textures/latex_purple_fox.png");
    }
}