package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexMothModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorNoTailModel;
import net.ltxprogrammer.changed.entity.beast.LatexMoth;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMothRenderer extends AdvancedHumanoidRenderer<LatexMoth, LatexMothModel, ArmorNoTailModel<LatexMoth>> {
    public LatexMothRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMothModel(context.bakeLayer(LatexMothModel.LAYER_LOCATION)),
                ArmorNoTailModel::new, ArmorNoTailModel.INNER_ARMOR, ArmorNoTailModel.OUTER_ARMOR, 0.5f);
        this.addLayer(GasMaskLayer.forNormal(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMoth p_114482_) {
        return Changed.modResource("textures/latex_moth.png");
    }
}