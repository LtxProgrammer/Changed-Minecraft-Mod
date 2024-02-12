package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexCrystalWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.LatexCrystalWolf;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexCrystalWolfRenderer extends AdvancedHumanoidRenderer<LatexCrystalWolf, LatexCrystalWolfModel, ArmorMaleWolfModel<LatexCrystalWolf>> {
    public LatexCrystalWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexCrystalWolfModel(context.bakeLayer(LatexCrystalWolfModel.LAYER_LOCATION)),
                ArmorMaleWolfModel::new, ArmorMaleWolfModel.INNER_ARMOR, ArmorMaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(),
                CustomEyesLayer.fixedColor(Color3.parseHex("#5a5a5a")),
                CustomEyesLayer::irisColorLeft,
                CustomEyesLayer::irisColorRight));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexCrystalWolf p_114482_) {
        return Changed.modResource("textures/latex_crystal_wolf.png");
    }
}