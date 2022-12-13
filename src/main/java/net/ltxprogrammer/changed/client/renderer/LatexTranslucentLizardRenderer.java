package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.layers.LatexGelLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexTrafficConeDragonModel;
import net.ltxprogrammer.changed.client.renderer.model.LatexTranslucentLizardModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexDragonModel;
import net.ltxprogrammer.changed.entity.beast.LatexTrafficConeDragon;
import net.ltxprogrammer.changed.entity.beast.LatexTranslucentLizard;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexTranslucentLizardRenderer extends LatexHumanoidRenderer<LatexTranslucentLizard, LatexTranslucentLizardModel, ArmorLatexDragonModel<LatexTranslucentLizard>> {
    public LatexTranslucentLizardRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexTranslucentLizardModel(context.bakeLayer(LatexTranslucentLizardModel.LAYER_LOCATION)),
                ArmorLatexDragonModel::new, ArmorLatexDragonModel.INNER_ARMOR, ArmorLatexDragonModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexGelLayer<>(this, new LatexTranslucentLizardModel((context.bakeLayer(LatexTranslucentLizardModel.LAYER_LOCATION_OUTER)))));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexTranslucentLizard p_114482_) {
        return new ResourceLocation("changed:textures/latex_translucent_lizard.png");
    }
}