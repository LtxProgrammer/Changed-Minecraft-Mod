package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexRaccoonModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexSnowLeopardModel;
import net.ltxprogrammer.changed.entity.beast.LatexRaccoon;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexRaccoonRenderer extends LatexHumanoidRenderer<LatexRaccoon, LatexRaccoonModel, ArmorLatexSnowLeopardModel<LatexRaccoon>> {
    public LatexRaccoonRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexRaccoonModel(context.bakeLayer(LatexRaccoonModel.LAYER_LOCATION)),
                ArmorLatexSnowLeopardModel::new, ArmorLatexSnowLeopardModel.INNER_ARMOR, ArmorLatexSnowLeopardModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexRaccoon p_114482_) {
        return Changed.modResource("textures/latex_raccoon.png");
    }
}