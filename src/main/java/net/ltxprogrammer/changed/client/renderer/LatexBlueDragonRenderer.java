package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexBlueDragonModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexBlueDragonModel;
import net.ltxprogrammer.changed.entity.beast.LatexBlueDragon;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexBlueDragonRenderer extends LatexHumanoidRenderer<LatexBlueDragon, LatexBlueDragonModel, ArmorLatexBlueDragonModel<LatexBlueDragon>> {
    public LatexBlueDragonRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexBlueDragonModel(context.bakeLayer(LatexBlueDragonModel.LAYER_LOCATION)),
                ArmorLatexBlueDragonModel::new, ArmorLatexBlueDragonModel.INNER_ARMOR, ArmorLatexBlueDragonModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexBlueDragon p_114482_) {
        return Changed.modResource("textures/latex_blue_dragon.png");
    }
}