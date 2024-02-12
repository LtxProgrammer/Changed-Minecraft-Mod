package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.AdditionalEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.WhiteGooKnightFusionModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.WhiteGooKnightFusion;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LightLatexKnightFusionRenderer extends AdvancedHumanoidRenderer<WhiteGooKnightFusion, WhiteGooKnightFusionModel, ArmorMaleWolfModel<WhiteGooKnightFusion>> {
    public LightLatexKnightFusionRenderer(EntityRendererProvider.Context context) {
        super(context, new WhiteGooKnightFusionModel(context.bakeLayer(WhiteGooKnightFusionModel.LAYER_LOCATION)),
                ArmorMaleWolfModel::new, ArmorMaleWolfModel.INNER_ARMOR, ArmorMaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new GooParticlesLayer<>(this, getModel()));
        this.addLayer(new AdditionalEyesLayer<>(this, context.getModelSet(), Changed.modResource("light_latex_knight_fusion")));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(),
                CustomEyesLayer.fixedColor(Color3.parseHex("#000000")),
                CustomEyesLayer::irisColorLeft,
                CustomEyesLayer::irisColorRight));
    }

    @Override
    public ResourceLocation getTextureLocation(WhiteGooKnightFusion p_114482_) {
        return Changed.modResource("textures/light_latex_knight_fusion.png");
    }
}