package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.WhiteGooKnightModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.WhiteGooKnight;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LightLatexKnightRenderer extends AdvancedHumanoidRenderer<WhiteGooKnight, WhiteGooKnightModel, ArmorMaleWolfModel<WhiteGooKnight>> {
    public LightLatexKnightRenderer(EntityRendererProvider.Context context) {
        super(context, new WhiteGooKnightModel(context.bakeLayer(WhiteGooKnightModel.LAYER_LOCATION)),
                ArmorMaleWolfModel::new, ArmorMaleWolfModel.INNER_ARMOR, ArmorMaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new GooParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(),
                CustomEyesLayer.fixedColor(Color3.parseHex("#1b1b1b")),
                CustomEyesLayer.fixedColor(Color3.parseHex("#dfdfdf"))));
    }

    @Override
    public ResourceLocation getTextureLocation(WhiteGooKnight p_114482_) {
        return Changed.modResource("textures/light_latex_knight.png");
    }
}