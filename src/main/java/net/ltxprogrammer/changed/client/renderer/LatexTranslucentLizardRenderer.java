package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexTranslucentLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexTranslucentLizardModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleDragonModel;
import net.ltxprogrammer.changed.entity.beast.LatexTranslucentLizard;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexTranslucentLizardRenderer extends LatexHumanoidRenderer<LatexTranslucentLizard, LatexTranslucentLizardModel, ArmorLatexMaleDragonModel<LatexTranslucentLizard>> {
    public LatexTranslucentLizardRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexTranslucentLizardModel(context.bakeLayer(LatexTranslucentLizardModel.LAYER_LOCATION)),
                ArmorLatexMaleDragonModel::new, ArmorLatexMaleDragonModel.INNER_ARMOR, ArmorLatexMaleDragonModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexTranslucentLayer<>(this, this.model, Changed.modResource("textures/latex_translucent_lizard_outer.png")));
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer.fixedColor(Color3.parseHex("#ffb84c"), 0.5f),CustomEyesLayer.fixedColor(Color3.parseHex("#a24b42"), 0.75f)));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexTranslucentLizard p_114482_) {
        return Changed.modResource("textures/latex_translucent_lizard_inner.png");
    }
}