package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexWatermelonCatModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleCatModel;
import net.ltxprogrammer.changed.entity.beast.LatexWatermelonCat;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexWatermelonCatRenderer extends AdvancedHumanoidRenderer<LatexWatermelonCat, LatexWatermelonCatModel, ArmorLatexFemaleCatModel<LatexWatermelonCat>> {
    public LatexWatermelonCatRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexWatermelonCatModel(context.bakeLayer(LatexWatermelonCatModel.LAYER_LOCATION)), ArmorLatexFemaleCatModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.BLACK).withIris(Color3.fromInt(0x67fd2a)).withEyebrows(Color3.fromInt(0x91ad3f)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexWatermelonCat p_114482_) {
        return Changed.modResource("textures/latex_watermelon_cat.png");
    }
}