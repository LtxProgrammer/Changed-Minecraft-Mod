package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexOtterModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexOtterModel;
import net.ltxprogrammer.changed.entity.beast.LatexOtter;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexOtterRenderer extends LatexHumanoidRenderer<LatexOtter, LatexOtterModel, ArmorLatexOtterModel<LatexOtter>> {
    public LatexOtterRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexOtterModel(context.bakeLayer(LatexOtterModel.LAYER_LOCATION)),
                ArmorLatexOtterModel::new, ArmorLatexOtterModel.INNER_ARMOR, ArmorLatexOtterModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexOtter p_114482_) {
        return Changed.modResource("textures/latex_otter.png");
    }
}