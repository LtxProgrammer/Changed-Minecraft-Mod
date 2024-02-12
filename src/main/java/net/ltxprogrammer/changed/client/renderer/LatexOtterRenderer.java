package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.GooOtterModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexOtterModel;
import net.ltxprogrammer.changed.entity.beast.GooOtter;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexOtterRenderer extends AdvancedHumanoidRenderer<GooOtter, GooOtterModel, ArmorLatexOtterModel<GooOtter>> {
    public LatexOtterRenderer(EntityRendererProvider.Context context) {
        super(context, new GooOtterModel(context.bakeLayer(GooOtterModel.LAYER_LOCATION)),
                ArmorLatexOtterModel::new, ArmorLatexOtterModel.INNER_ARMOR, ArmorLatexOtterModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(GooOtter p_114482_) {
        return Changed.modResource("textures/latex_otter.png");
    }
}