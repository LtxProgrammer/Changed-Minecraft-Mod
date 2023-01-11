package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexDeerModel;
import net.ltxprogrammer.changed.client.renderer.model.LatexYuinModel;
import net.ltxprogrammer.changed.client.renderer.model.LightLatexWolfMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexYuinModel;
import net.ltxprogrammer.changed.entity.beast.LatexDeer;
import net.ltxprogrammer.changed.entity.beast.LatexYuin;
import net.ltxprogrammer.changed.entity.beast.LightLatexWolfMale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexDeerRenderer extends LatexHumanoidRenderer<LatexDeer, LatexDeerModel, ArmorLatexYuinModel<LatexDeer>> {
    public LatexDeerRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexDeerModel(context.bakeLayer(LatexDeerModel.LAYER_LOCATION)),
                ArmorLatexYuinModel::new, ArmorLatexYuinModel.INNER_ARMOR, ArmorLatexYuinModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexDeer p_114482_) {
        return Changed.modResource("textures/latex_deer.png");
    }
}