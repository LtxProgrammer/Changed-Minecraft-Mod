package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.*;
import net.ltxprogrammer.changed.client.renderer.model.LatexTranslucentLizardModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleDragonModel;
import net.ltxprogrammer.changed.entity.beast.LatexTranslucentLizard;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexTranslucentLizardRenderer extends AdvancedHumanoidRenderer<LatexTranslucentLizard, LatexTranslucentLizardModel, ArmorLatexMaleDragonModel<LatexTranslucentLizard>> {
    public LatexTranslucentLizardRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexTranslucentLizardModel(context.bakeLayer(LatexTranslucentLizardModel.LAYER_LOCATION)),
                ArmorLatexMaleDragonModel::new, ArmorLatexMaleDragonModel.INNER_ARMOR, ArmorLatexMaleDragonModel.OUTER_ARMOR, 0.5f);
        var translucent = new LatexTranslucentLayer<>(this, this.model, Changed.modResource("textures/latex_translucent_lizard_outer.png"));
        this.addLayer(translucent);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()).addModel(translucent.getModel(), entity -> translucent.getTexture()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(CustomEyesLayer.fixedColor(Color3.fromInt(0xffb84c), 0.5f)).withIris(CustomEyesLayer.fixedColor(Color3.fromInt(0xa24b42), 0.75f)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexTranslucentLizard p_114482_) {
        return Changed.modResource("textures/latex_translucent_lizard_inner.png");
    }
}