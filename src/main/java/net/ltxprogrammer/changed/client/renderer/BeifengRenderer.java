package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexBeifengModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleDragonModel;
import net.ltxprogrammer.changed.entity.beast.LatexBeifeng;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexBeifengRenderer extends LatexHumanoidRenderer<LatexBeifeng, LatexBeifengModel, ArmorLatexMaleDragonModel<LatexBeifeng>> {
    public LatexBeifengRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexBeifengModel(context.bakeLayer(LatexBeifengModel.LAYER_LOCATION)),
                ArmorLatexMaleDragonModel::new, ArmorLatexMaleDragonModel.INNER_ARMOR, ArmorLatexMaleDragonModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer.fixedColor(Color3.WHITE),CustomEyesLayer.fixedColor(Color3.parseHex("#ffe852"))));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexBeifeng p_114482_) {
        return Changed.modResource("textures/latex_beifeng.png");
    }
}