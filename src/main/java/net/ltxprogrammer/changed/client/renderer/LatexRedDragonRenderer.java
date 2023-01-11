package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.DarkLatexYufengModel;
import net.ltxprogrammer.changed.client.renderer.model.LatexRedDragonModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexBlueDragonModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexDragonModel;
import net.ltxprogrammer.changed.entity.beast.DarkLatexYufeng;
import net.ltxprogrammer.changed.entity.beast.LatexRedDragon;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexRedDragonRenderer extends LatexHumanoidRenderer<LatexRedDragon, LatexRedDragonModel, ArmorLatexBlueDragonModel<LatexRedDragon>>  {
           public LatexRedDragonRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexRedDragonModel(context.bakeLayer(LatexRedDragonModel.LAYER_LOCATION)),
                ArmorLatexBlueDragonModel::new, ArmorLatexBlueDragonModel.INNER_ARMOR, ArmorLatexBlueDragonModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexRedDragon p_114482_) {
        return Changed.modResource("textures/latex_red_dragon.png");
    }
}