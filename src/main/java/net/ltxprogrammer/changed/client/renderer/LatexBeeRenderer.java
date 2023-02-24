package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexBeeModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorNoTailModel;
import net.ltxprogrammer.changed.entity.beast.LatexBee;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexBeeRenderer extends LatexHumanoidRenderer<LatexBee, LatexBeeModel, ArmorNoTailModel<LatexBee>> {
    public LatexBeeRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexBeeModel(context.bakeLayer(LatexBeeModel.LAYER_LOCATION)),
                ArmorNoTailModel::new, ArmorNoTailModel.INNER_ARMOR, ArmorNoTailModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexBee p_114482_) {
        return Changed.modResource("textures/latex_bee.png");
    }
}