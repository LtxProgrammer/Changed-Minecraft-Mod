package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.layers.LatexGelLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexTranslucentLizardModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexBlueDragonModel;
import net.ltxprogrammer.changed.entity.beast.LatexTranslucentLizard;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexTranslucentLizardRenderer extends LatexHumanoidRenderer<LatexTranslucentLizard, LatexTranslucentLizardModel, ArmorLatexBlueDragonModel<LatexTranslucentLizard>> {
    public LatexTranslucentLizardRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexTranslucentLizardModel(context.bakeLayer(LatexTranslucentLizardModel.LAYER_LOCATION)),
                ArmorLatexBlueDragonModel::new, ArmorLatexBlueDragonModel.INNER_ARMOR, ArmorLatexBlueDragonModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexGelLayer<>(this, new LatexTranslucentLizardModel((context.bakeLayer(LatexTranslucentLizardModel.LAYER_LOCATION_OUTER)))));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexTranslucentLizard p_114482_) {
        return new ResourceLocation("changed:textures/latex_translucent_lizard.png");
    }
}