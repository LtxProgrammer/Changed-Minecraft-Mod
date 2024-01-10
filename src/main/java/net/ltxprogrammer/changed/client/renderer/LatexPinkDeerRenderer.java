package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexPinkDeerModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleDragonModel;
import net.ltxprogrammer.changed.entity.beast.LatexPinkDeer;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexPinkDeerRenderer extends LatexHumanoidRenderer<LatexPinkDeer, LatexPinkDeerModel, ArmorLatexMaleDragonModel<LatexPinkDeer>> {
    public LatexPinkDeerRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexPinkDeerModel(context.bakeLayer(LatexPinkDeerModel.LAYER_LOCATION)),
                ArmorLatexMaleDragonModel::new, ArmorLatexMaleDragonModel.INNER_ARMOR, ArmorLatexMaleDragonModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer.fixedColor(Color3.WHITE),CustomEyesLayer.fixedColor(Color3.parseHex("#7889f3"))));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexPinkDeer p_114482_) {
        return Changed.modResource("textures/latex_pink_deer.png");
    }
}