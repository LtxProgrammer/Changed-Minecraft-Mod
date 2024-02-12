package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.DoubleItemInHandLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.GooSquidDogFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleSquidDogModel;
import net.ltxprogrammer.changed.entity.beast.GooSquidDogFemale;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GooSquidDogFemaleRenderer extends AdvancedHumanoidRenderer<GooSquidDogFemale, GooSquidDogFemaleModel, ArmorLatexFemaleSquidDogModel<GooSquidDogFemale>> {
    public GooSquidDogFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new GooSquidDogFemaleModel(context.bakeLayer(GooSquidDogFemaleModel.LAYER_LOCATION)),
                ArmorLatexFemaleSquidDogModel::new, ArmorLatexFemaleSquidDogModel.INNER_ARMOR, ArmorLatexFemaleSquidDogModel.OUTER_ARMOR, 0.65f);
        this.addLayer(new DoubleItemInHandLayer<>(this));
        this.addLayer(new GooParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(),
                CustomEyesLayer.fixedColor(Color3.parseHex("#1b1b1b")),
                CustomEyesLayer.fixedColor(Color3.parseHex("#dfdfdf"))));
    }

    @Override
    public ResourceLocation getTextureLocation(GooSquidDogFemale p_114482_) {
        return Changed.modResource("textures/latex_squid_dog_female.png");
    }
}