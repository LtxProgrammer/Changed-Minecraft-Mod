package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.DoubleItemInHandLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexTranslucentLayer;
import net.ltxprogrammer.changed.client.renderer.model.GooBeeModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexBeeModel;
import net.ltxprogrammer.changed.entity.beast.GooBee;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GooBeeRenderer extends AdvancedHumanoidRenderer<GooBee, GooBeeModel, ArmorLatexBeeModel<GooBee>> {
    public GooBeeRenderer(EntityRendererProvider.Context context) {
        super(context, new GooBeeModel(context.bakeLayer(GooBeeModel.LAYER_LOCATION)),
                ArmorLatexBeeModel::new, ArmorLatexBeeModel.INNER_ARMOR, ArmorLatexBeeModel.OUTER_ARMOR, 0.5f);
        var translucent = new LatexTranslucentLayer<>(this, this.model, Changed.modResource("textures/latex_bee_translucent.png"));
        this.addLayer(translucent);
        this.addLayer(new DoubleItemInHandLayer<>(this));
        this.addLayer(new GooParticlesLayer<>(this, getModel(), translucent));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(),
                CustomEyesLayer.fixedColor(Color3.parseHex("#1b1b1b")),
                CustomEyesLayer::irisColorLeft, CustomEyesLayer::irisColorRight));
    }

    @Override
    public ResourceLocation getTextureLocation(GooBee p_114482_) {
        return Changed.modResource("textures/latex_bee.png");
    }
}