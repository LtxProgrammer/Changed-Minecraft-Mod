package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.GooCrocodileModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexCrocodileModel;
import net.ltxprogrammer.changed.entity.beast.GooCrocodile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexCrocodileRenderer extends AdvancedHumanoidRenderer<GooCrocodile, GooCrocodileModel, ArmorLatexCrocodileModel<GooCrocodile>> {
    public LatexCrocodileRenderer(EntityRendererProvider.Context context) {
        super(context, new GooCrocodileModel(context.bakeLayer(GooCrocodileModel.LAYER_LOCATION)),
                ArmorLatexCrocodileModel::new, ArmorLatexCrocodileModel.INNER_ARMOR, ArmorLatexCrocodileModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(GooCrocodile p_114482_) {
        return Changed.modResource("textures/latex_crocodile.png");
    }
}