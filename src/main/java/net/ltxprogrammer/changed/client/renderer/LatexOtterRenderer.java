package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexOtterModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexOtterModel;
import net.ltxprogrammer.changed.entity.beast.LatexOtter;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexOtterRenderer extends AdvancedHumanoidRenderer<LatexOtter, LatexOtterModel, ArmorLatexOtterModel<LatexOtter>> {
    public LatexOtterRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexOtterModel(context.bakeLayer(LatexOtterModel.LAYER_LOCATION)), ArmorLatexOtterModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexOtter p_114482_) {
        return Changed.modResource("textures/latex_otter.png");
    }
}