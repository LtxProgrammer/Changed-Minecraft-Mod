package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexSniperDogModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.LatexSniperDog;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSniperDogRenderer extends AdvancedHumanoidRenderer<LatexSniperDog, LatexSniperDogModel, ArmorMaleWolfModel<LatexSniperDog>> {
    public LatexSniperDogRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSniperDogModel(context.bakeLayer(LatexSniperDogModel.LAYER_LOCATION)),
                ArmorMaleWolfModel::new, ArmorMaleWolfModel.INNER_ARMOR, ArmorMaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer::scleraColor, CustomEyesLayer::irisColorLeft, CustomEyesLayer::irisColorRight,
                CustomEyesLayer.fixedColor(Color3.parseHex("#713e35"))));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSniperDog p_114482_) {
        return Changed.modResource("textures/latex_sniper_dog.png");
    }
}