package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexSquidDogMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexSquidDogModel;
import net.ltxprogrammer.changed.entity.beast.LatexSquidDogMale;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSquidDogMaleRenderer extends LatexHumanoidRenderer<LatexSquidDogMale, LatexSquidDogMaleModel, ArmorLatexSquidDogModel<LatexSquidDogMale>> {
    public LatexSquidDogMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSquidDogMaleModel(context.bakeLayer(LatexSquidDogMaleModel.LAYER_LOCATION)),
                ArmorLatexSquidDogModel::new, ArmorLatexSquidDogModel.INNER_ARMOR, ArmorLatexSquidDogModel.OUTER_ARMOR, 0.65f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(),
                CustomEyesLayer.fixedColor(Color3.parseHex("#1b1b1b")),
                CustomEyesLayer.fixedColor(Color3.parseHex("#dfdfdf"))));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSquidDogMale p_114482_) {
        return Changed.modResource("textures/latex_squid_dog_male.png");
    }
}