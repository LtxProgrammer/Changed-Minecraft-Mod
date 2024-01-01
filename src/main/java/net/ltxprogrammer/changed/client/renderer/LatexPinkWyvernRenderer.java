package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexTranslucentLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexPinkWyvernModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexBlueDragonModel;
import net.ltxprogrammer.changed.entity.beast.LatexPinkWyvern;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexPinkWyvernRenderer extends LatexHumanoidRenderer<LatexPinkWyvern, LatexPinkWyvernModel, ArmorLatexBlueDragonModel<LatexPinkWyvern>> {
    public LatexPinkWyvernRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexPinkWyvernModel(context.bakeLayer(LatexPinkWyvernModel.LAYER_LOCATION)),
                ArmorLatexBlueDragonModel::new, ArmorLatexBlueDragonModel.INNER_ARMOR, ArmorLatexBlueDragonModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexTranslucentLayer<>(this, this.model, Changed.modResource("textures/latex_pink_wyvern_translucent.png")));
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer.fixedColor(Color3.WHITE),CustomEyesLayer.fixedColor(Color3.parseHex("#7889f3"))));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexPinkWyvern p_114482_) {
        return Changed.modResource("textures/latex_pink_wyvern.png");
    }
}