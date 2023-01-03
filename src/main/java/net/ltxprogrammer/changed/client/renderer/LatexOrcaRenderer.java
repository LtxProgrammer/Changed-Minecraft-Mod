package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexOrcaModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexSharkModel;
import net.ltxprogrammer.changed.entity.beast.LatexOrca;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexOrcaRenderer extends LatexHumanoidRenderer<LatexOrca, LatexOrcaModel, ArmorLatexSharkModel<LatexOrca>> {
    public LatexOrcaRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexOrcaModel(context.bakeLayer(LatexOrcaModel.LAYER_LOCATION)),
                ArmorLatexSharkModel::new, ArmorLatexSharkModel.INNER_ARMOR, ArmorLatexSharkModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexOrca p_114482_) {
        return Changed.modResource("textures/latex_orca.png");
    }
}