package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexRaccoonModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleCatModel;
import net.ltxprogrammer.changed.entity.beast.LatexRaccoon;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexRaccoonRenderer extends AdvancedHumanoidRenderer<LatexRaccoon, LatexRaccoonModel, ArmorLatexMaleCatModel<LatexRaccoon>> {
    public LatexRaccoonRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexRaccoonModel(context.bakeLayer(LatexRaccoonModel.LAYER_LOCATION)),
                ArmorLatexMaleCatModel::new, ArmorLatexMaleCatModel.INNER_ARMOR, ArmorLatexMaleCatModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexRaccoon p_114482_) {
        return Changed.modResource("textures/latex_raccoon.png");
    }
}