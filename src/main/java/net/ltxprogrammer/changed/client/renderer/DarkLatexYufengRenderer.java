package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.DarkLatexYufengModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexBlueDragonModel;
import net.ltxprogrammer.changed.entity.beast.DarkLatexYufeng;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DarkLatexYufengRenderer extends LatexHumanoidRenderer<DarkLatexYufeng, DarkLatexYufengModel, ArmorLatexBlueDragonModel<DarkLatexYufeng>> {
    public DarkLatexYufengRenderer(EntityRendererProvider.Context context) {
        super(context, new DarkLatexYufengModel(context.bakeLayer(DarkLatexYufengModel.LAYER_LOCATION)),
                ArmorLatexBlueDragonModel::new, ArmorLatexBlueDragonModel.INNER_ARMOR, ArmorLatexBlueDragonModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(DarkLatexYufeng p_114482_) {
        return Changed.modResource("textures/dark_latex_yufeng.png");
    }
}