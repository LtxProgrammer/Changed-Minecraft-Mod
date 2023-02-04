package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexWhiteTigerModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexSnowLeopardModel;
import net.ltxprogrammer.changed.entity.beast.LatexWhiteTiger;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexWhiteTigerRenderer extends LatexHumanoidRenderer<LatexWhiteTiger, LatexWhiteTigerModel, ArmorLatexSnowLeopardModel<LatexWhiteTiger>> {
    public LatexWhiteTigerRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexWhiteTigerModel(context.bakeLayer(LatexWhiteTigerModel.LAYER_LOCATION)),
                ArmorLatexSnowLeopardModel::new, ArmorLatexSnowLeopardModel.INNER_ARMOR, ArmorLatexSnowLeopardModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexWhiteTiger p_114482_) {
        return Changed.modResource("textures/latex_white_tiger.png");
    }
}