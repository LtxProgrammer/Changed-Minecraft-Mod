package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.LatexTranslucentLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexTranslucentLizardModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexBlueDragonModel;
import net.ltxprogrammer.changed.entity.beast.LatexTranslucentLizard;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexTranslucentLizardRenderer extends LatexHumanoidRenderer<LatexTranslucentLizard, LatexTranslucentLizardModel, ArmorLatexBlueDragonModel<LatexTranslucentLizard>> {
    public LatexTranslucentLizardRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexTranslucentLizardModel(context.bakeLayer(LatexTranslucentLizardModel.LAYER_LOCATION)),
                ArmorLatexBlueDragonModel::new, ArmorLatexBlueDragonModel.INNER_ARMOR, ArmorLatexBlueDragonModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexTranslucentLayer<>(this, new LatexTranslucentLizardModel(context.bakeLayer(LatexTranslucentLizardModel.LAYER_LOCATION_OUTER))));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexTranslucentLizard p_114482_) {
        return Changed.modResource("textures/latex_translucent_lizard.png");
    }

    public static class Remodel extends LatexHumanoidRenderer<LatexTranslucentLizard, LatexTranslucentLizardModel.Remodel, ArmorLatexBlueDragonModel.RemodelMale<LatexTranslucentLizard>> {
        public Remodel(EntityRendererProvider.Context context) {
            super(context, new LatexTranslucentLizardModel.Remodel(context.bakeLayer(LatexTranslucentLizardModel.LAYER_LOCATION)),
                    ArmorLatexBlueDragonModel.RemodelMale::new, ArmorLatexBlueDragonModel.RemodelMale.INNER_ARMOR, ArmorLatexBlueDragonModel.RemodelMale.OUTER_ARMOR, 0.5f);
            this.addLayer(new LatexTranslucentLayer<>(this, new LatexTranslucentLizardModel.Remodel(context.bakeLayer(LatexTranslucentLizardModel.LAYER_LOCATION_OUTER))));
        }

        @Override
        public ResourceLocation getTextureLocation(LatexTranslucentLizard p_114482_) {
            return Changed.modResource("textures/remodel/latex_translucent_lizard.png");
        }
    }
}