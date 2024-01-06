package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexWatermelonCatModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleCatModel;
import net.ltxprogrammer.changed.entity.beast.LatexWatermelonCat;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexWatermelonCatRenderer extends LatexHumanoidRenderer<LatexWatermelonCat, LatexWatermelonCatModel, ArmorLatexFemaleCatModel<LatexWatermelonCat>> {
    public LatexWatermelonCatRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexWatermelonCatModel(context.bakeLayer(LatexWatermelonCatModel.LAYER_LOCATION)),
                ArmorLatexFemaleCatModel::new, ArmorLatexFemaleCatModel.INNER_ARMOR, ArmorLatexFemaleCatModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(),
                CustomEyesLayer.fixedColor(Color3.BLACK),
                CustomEyesLayer.fixedColor(Color3.parseHex("#67fd2a")),
                CustomEyesLayer.fixedColor(Color3.parseHex("#91ad3f"))));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexWatermelonCat p_114482_) {
        return Changed.modResource("textures/latex_watermelon_cat.png");
    }
}