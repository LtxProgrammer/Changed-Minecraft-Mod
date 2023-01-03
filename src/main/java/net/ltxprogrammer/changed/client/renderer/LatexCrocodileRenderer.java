package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexCrocodileModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexCrocodileModel;
import net.ltxprogrammer.changed.entity.beast.LatexCrocodile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexCrocodileRenderer extends LatexHumanoidRenderer<LatexCrocodile, LatexCrocodileModel, ArmorLatexCrocodileModel<LatexCrocodile>> {
    public LatexCrocodileRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexCrocodileModel(context.bakeLayer(LatexCrocodileModel.LAYER_LOCATION)),
                ArmorLatexCrocodileModel::new, ArmorLatexCrocodileModel.INNER_ARMOR, ArmorLatexCrocodileModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexCrocodile p_114482_) {
        return Changed.modResource("textures/latex_crocodile.png");
    }
}