package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.EmissiveBodyLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexLeafModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexTrafficConeDragonModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.LatexHypnoCat;
import net.ltxprogrammer.changed.entity.beast.LatexLeaf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexLeafRenderer extends LatexHumanoidRenderer<LatexLeaf, LatexLeafModel, ArmorLatexTrafficConeDragonModel<LatexLeaf>> {
    public LatexLeafRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexLeafModel(context.bakeLayer(LatexLeafModel.LAYER_LOCATION)),
                ArmorLatexTrafficConeDragonModel::new, ArmorLatexTrafficConeDragonModel.INNER_ARMOR, ArmorLatexTrafficConeDragonModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexLeaf p_114482_) {
        return new ResourceLocation("changed:textures/latex_leaf.png");
    }
}