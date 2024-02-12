package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.GooYuinModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexYuinModel;
import net.ltxprogrammer.changed.entity.beast.GooYuin;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexYuinRenderer extends AdvancedHumanoidRenderer<GooYuin, GooYuinModel, ArmorLatexYuinModel<GooYuin>> {
    public LatexYuinRenderer(EntityRendererProvider.Context context) {
        super(context, new GooYuinModel(context.bakeLayer(GooYuinModel.LAYER_LOCATION)),
                ArmorLatexYuinModel::new, ArmorLatexYuinModel.INNER_ARMOR, ArmorLatexYuinModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(GooYuin p_114482_) {
        return Changed.modResource("textures/latex_yuin.png");
    }
}