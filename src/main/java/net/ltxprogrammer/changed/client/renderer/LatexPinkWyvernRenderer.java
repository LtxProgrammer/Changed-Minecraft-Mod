package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexPinkWyvernModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexBlueDragonModel;
import net.ltxprogrammer.changed.entity.beast.LatexPinkWyvern;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexPinkWyvernRenderer extends LatexHumanoidRenderer<LatexPinkWyvern, LatexPinkWyvernModel, ArmorLatexBlueDragonModel<LatexPinkWyvern>> {
    public LatexPinkWyvernRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexPinkWyvernModel(context.bakeLayer(LatexPinkWyvernModel.LAYER_LOCATION)),
                ArmorLatexBlueDragonModel::new, ArmorLatexBlueDragonModel.INNER_ARMOR, ArmorLatexBlueDragonModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexPinkWyvern p_114482_) {
        return Changed.modResource("textures/latex_pink_wyvern.png");
    }
}