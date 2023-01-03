package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexTigerSharkModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexSharkModel;
import net.ltxprogrammer.changed.entity.beast.LatexTigerShark;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexTigerSharkRenderer extends LatexHumanoidRenderer<LatexTigerShark, LatexTigerSharkModel, ArmorLatexSharkModel<LatexTigerShark>> {
    public LatexTigerSharkRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexTigerSharkModel(context.bakeLayer(LatexTigerSharkModel.LAYER_LOCATION)),
                ArmorLatexSharkModel::new, ArmorLatexSharkModel.INNER_ARMOR, ArmorLatexSharkModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexTigerShark p_114482_) {
        return Changed.modResource("textures/latex_tiger_shark.png");
    }
}