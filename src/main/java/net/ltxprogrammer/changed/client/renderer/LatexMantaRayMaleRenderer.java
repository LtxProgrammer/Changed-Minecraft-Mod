package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexMantaRayMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleSharkModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexOrcaModel;
import net.ltxprogrammer.changed.entity.beast.LatexMantaRayMale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMantaRayMaleRenderer extends AdvancedHumanoidRenderer<LatexMantaRayMale, LatexMantaRayMaleModel, ArmorLatexOrcaModel<LatexMantaRayMale>> {
    public LatexMantaRayMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMantaRayMaleModel(context.bakeLayer(LatexMantaRayMaleModel.LAYER_LOCATION)),
                ArmorLatexOrcaModel::new, ArmorLatexMaleSharkModel.INNER_ARMOR, ArmorLatexMaleSharkModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forLargeSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMantaRayMale p_114482_) {
        return Changed.modResource("textures/latex_manta_ray_male.png");
    }
}
