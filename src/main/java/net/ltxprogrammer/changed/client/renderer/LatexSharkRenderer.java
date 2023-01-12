package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexSharkModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexSharkModel;
import net.ltxprogrammer.changed.entity.beast.LatexShark;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSharkRenderer extends LatexHumanoidRenderer<LatexShark, LatexSharkModel, ArmorLatexSharkModel<LatexShark>> {
    public LatexSharkRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSharkModel(context.bakeLayer(LatexSharkModel.LAYER_LOCATION)),
                ArmorLatexSharkModel::new, ArmorLatexSharkModel.INNER_ARMOR, ArmorLatexSharkModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexShark p_114482_) {
        return Changed.modResource("textures/latex_shark.png");
    }
}