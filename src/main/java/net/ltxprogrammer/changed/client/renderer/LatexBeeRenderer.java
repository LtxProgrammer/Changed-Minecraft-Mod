package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexDoubleItemInHandLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexTranslucentLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexBeeModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorNoTailModel;
import net.ltxprogrammer.changed.entity.beast.LatexBee;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexBeeRenderer extends LatexHumanoidRenderer<LatexBee, LatexBeeModel, ArmorNoTailModel<LatexBee>> {
    public LatexBeeRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexBeeModel(context.bakeLayer(LatexBeeModel.LAYER_LOCATION)),
                ArmorNoTailModel::new, ArmorNoTailModel.INNER_ARMOR, ArmorNoTailModel.OUTER_ARMOR, 0.5f);
        var translucent = new LatexTranslucentLayer<>(this, this.model, Changed.modResource("textures/latex_bee_translucent.png"));
        this.addLayer(translucent);
        this.addLayer(new LatexDoubleItemInHandLayer<>(this));
        this.addLayer(new LatexParticlesLayer<>(this, getModel(), translucent));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(),
                CustomEyesLayer.fixedColor(Color3.parseHex("#1b1b1b")),
                CustomEyesLayer::irisColorLeft, CustomEyesLayer::irisColorRight));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexBee p_114482_) {
        return Changed.modResource("textures/latex_bee.png");
    }
}