package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.*;
import net.ltxprogrammer.changed.client.renderer.model.LatexHypnoCatModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleCatModel;
import net.ltxprogrammer.changed.entity.beast.LatexHypnoCat;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexHypnoCatRenderer extends AdvancedHumanoidRenderer<LatexHypnoCat, LatexHypnoCatModel, ArmorLatexMaleCatModel<LatexHypnoCat>> {
    public LatexHypnoCatRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexHypnoCatModel(context.bakeLayer(LatexHypnoCatModel.LAYER_LOCATION)), ArmorLatexMaleCatModel.MODEL_SET, 0.5f);
        this.addLayer(new EmissiveBodyLayer<>(this, Changed.modResource("textures/latex_hypno_cat_emissive.png")));
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.fromInt(0x52596d)).withIris(CustomEyesLayer.fixedColorGlowing(Color3.fromInt(0xd7ff46))).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexHypnoCat p_114482_) {
        return Changed.modResource("textures/latex_hypno_cat.png");
    }
}