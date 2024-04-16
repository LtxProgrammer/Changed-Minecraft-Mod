package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexDeerModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexDeerModel;
import net.ltxprogrammer.changed.entity.beast.LatexDeer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexDeerRenderer extends AdvancedHumanoidRenderer<LatexDeer, LatexDeerModel, ArmorLatexDeerModel<LatexDeer>> {
    public LatexDeerRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexDeerModel(context.bakeLayer(LatexDeerModel.LAYER_LOCATION)),
                ArmorLatexDeerModel::new, ArmorLatexDeerModel.INNER_ARMOR, ArmorLatexDeerModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexDeer p_114482_) {
        return Changed.modResource("textures/latex_deer.png");
    }
}