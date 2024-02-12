package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.GooOrcaModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleSharkModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexOrcaModel;
import net.ltxprogrammer.changed.entity.beast.GooOrca;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GooOrcaRenderer extends AdvancedHumanoidRenderer<GooOrca, GooOrcaModel, ArmorLatexOrcaModel<GooOrca>> {
    public GooOrcaRenderer(EntityRendererProvider.Context context) {
        super(context, new GooOrcaModel(context.bakeLayer(GooOrcaModel.LAYER_LOCATION)),
                ArmorLatexOrcaModel::new, ArmorLatexMaleSharkModel.INNER_ARMOR, ArmorLatexMaleSharkModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new GooParticlesLayer<>(this, this.model));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(GooOrca p_114482_) {
        return Changed.modResource("textures/latex_orca.png");
    }
}