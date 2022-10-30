package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.LatexBeifengModel;
import net.ltxprogrammer.changed.client.renderer.model.LatexBlueDragonModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexBlueDragonModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexDragonModel;
import net.ltxprogrammer.changed.entity.beast.LatexBeifeng;
import net.ltxprogrammer.changed.entity.beast.LatexBlueDragon;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexBlueDragonRenderer extends LatexHumanoidRenderer<LatexBlueDragon, LatexBlueDragonModel, ArmorLatexBlueDragonModel> {
    public LatexBlueDragonRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexBlueDragonModel(context.bakeLayer(LatexBlueDragonModel.LAYER_LOCATION)),
                ArmorLatexBlueDragonModel::new, ArmorLatexBlueDragonModel.INNER_ARMOR, ArmorLatexBlueDragonModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexBlueDragon p_114482_) {
        return new ResourceLocation("changed:textures/latex_blue_dragon.png");
    }
}