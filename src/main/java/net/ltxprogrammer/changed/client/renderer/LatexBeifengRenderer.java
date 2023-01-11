package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexBeifengModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexDragonModel;
import net.ltxprogrammer.changed.entity.beast.LatexBeifeng;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexBeifengRenderer extends LatexHumanoidRenderer<LatexBeifeng, LatexBeifengModel, ArmorLatexDragonModel<LatexBeifeng>> {
    public LatexBeifengRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexBeifengModel(context.bakeLayer(LatexBeifengModel.LAYER_LOCATION)),
                ArmorLatexDragonModel::new, ArmorLatexDragonModel.INNER_ARMOR, ArmorLatexDragonModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexBeifeng p_114482_) {
        return Changed.modResource("textures/latex_beifeng.png");
    }
}