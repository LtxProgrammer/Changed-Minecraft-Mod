package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.EmissiveBodyLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexHypnoCatModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleCatModel;
import net.ltxprogrammer.changed.entity.beast.LatexHypnoCat;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexHypnoCatRenderer extends LatexHumanoidRenderer<LatexHypnoCat, LatexHypnoCatModel, ArmorLatexMaleCatModel<LatexHypnoCat>> {
    public LatexHypnoCatRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexHypnoCatModel(context.bakeLayer(LatexHypnoCatModel.LAYER_LOCATION)),
                ArmorLatexMaleCatModel::new, ArmorLatexMaleCatModel.INNER_ARMOR, ArmorLatexMaleCatModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new EmissiveBodyLayer<>(this, Changed.modResource("textures/latex_hypno_cat_emissive.png")));
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer.fixedColor(Color3.parseHex("#52596d")),CustomEyesLayer.fixedColorGlowing(Color3.parseHex("#d7ff46"))));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexHypnoCat p_114482_) {
        return Changed.modResource("textures/latex_hypno_cat.png");
    }
}