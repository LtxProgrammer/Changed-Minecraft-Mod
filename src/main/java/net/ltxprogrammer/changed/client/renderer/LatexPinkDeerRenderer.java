package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexPinkDeerModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexYuinModel;
import net.ltxprogrammer.changed.entity.beast.LatexPinkDeer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexPinkDeerRenderer extends LatexHumanoidRenderer<LatexPinkDeer, LatexPinkDeerModel, ArmorLatexYuinModel<LatexPinkDeer>> {
    public LatexPinkDeerRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexPinkDeerModel(context.bakeLayer(LatexPinkDeerModel.LAYER_LOCATION)),
                ArmorLatexYuinModel::new, ArmorLatexYuinModel.INNER_ARMOR, ArmorLatexYuinModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexPinkDeer p_114482_) {
        return Changed.modResource("textures/latex_pink_deer.png");
    }
}