package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.DoubleItemInHandLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.GooSquidDogMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleSquidDogModel;
import net.ltxprogrammer.changed.entity.beast.GooSquidDogMale;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GooSquidDogMaleRenderer extends AdvancedHumanoidRenderer<GooSquidDogMale, GooSquidDogMaleModel, ArmorLatexMaleSquidDogModel<GooSquidDogMale>> {
    public GooSquidDogMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new GooSquidDogMaleModel(context.bakeLayer(GooSquidDogMaleModel.LAYER_LOCATION)),
                ArmorLatexMaleSquidDogModel::new, ArmorLatexMaleSquidDogModel.INNER_ARMOR, ArmorLatexMaleSquidDogModel.OUTER_ARMOR, 0.65f);
        this.addLayer(new DoubleItemInHandLayer<>(this));
        this.addLayer(new GooParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(),
                CustomEyesLayer.fixedColor(Color3.parseHex("#1b1b1b")),
                CustomEyesLayer.fixedColor(Color3.parseHex("#dfdfdf"))));
    }

    @Override
    public ResourceLocation getTextureLocation(GooSquidDogMale p_114482_) {
        return Changed.modResource("textures/latex_squid_dog_male.png");
    }
}