package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.LatexSnowLeopardMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexSnowLeopardModel;
import net.ltxprogrammer.changed.entity.beast.LatexSnowLeopardMale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSnowLeopardMaleRenderer extends LatexHumanoidRenderer<LatexSnowLeopardMale, LatexSnowLeopardMaleModel, ArmorLatexSnowLeopardModel<LatexSnowLeopardMale>> {
    public LatexSnowLeopardMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSnowLeopardMaleModel(context.bakeLayer(LatexSnowLeopardMaleModel.LAYER_LOCATION)),
                ArmorLatexSnowLeopardModel::new, ArmorLatexSnowLeopardModel.INNER_ARMOR, ArmorLatexSnowLeopardModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSnowLeopardMale p_114482_) {
        return new ResourceLocation("changed:textures/latex_snow_leopard_male.png");
    }
}