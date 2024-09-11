package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.*;
import net.ltxprogrammer.changed.client.renderer.model.LatexHyenaTaurModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexCentaurLowerModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexCentaurUpperModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorModelPicker;
import net.ltxprogrammer.changed.entity.beast.LatexHyenaTaur;
import net.ltxprogrammer.changed.item.QuadrupedalArmor;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;

public class LatexHyenaTaurRenderer extends AdvancedHumanoidRenderer<LatexHyenaTaur, LatexHyenaTaurModel, ArmorLatexCentaurUpperModel<LatexHyenaTaur>> {
    public LatexHyenaTaurRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexHyenaTaurModel(context.bakeLayer(LatexHyenaTaurModel.LAYER_LOCATION)),
                ArmorModelPicker.centaur(context.getModelSet(), ArmorLatexCentaurUpperModel.MODEL_SET, ArmorLatexCentaurLowerModel.MODEL_SET), 0.7f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(),
                CustomEyesLayer.fixedColor(Color3.parseHex("#ffffff")),
                CustomEyesLayer.fixedColor(Color3.parseHex("#b3e53a"))));
        this.addLayer(new SaddleLayer<>(this, getModel(), Changed.modResource("textures/latex_hyena_taur_saddle.png")));
        this.addLayer(new TaurChestPackLayer<>(this, context.getModelSet()));
        this.addLayer(TransfurCapeLayer.shortCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexHyenaTaur p_114482_) {
        return Changed.modResource("textures/latex_hyena_taur.png");
    }
}