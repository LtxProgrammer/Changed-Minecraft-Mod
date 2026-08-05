package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.*;
import net.ltxprogrammer.changed.client.renderer.model.LatexSquidDogFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.LatexSquidDogMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleSquidDogModel;
import net.ltxprogrammer.changed.entity.beast.LatexSquidDogFemale;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSquidDogFemaleRenderer extends AdvancedHumanoidRenderer<LatexSquidDogFemale, LatexSquidDogFemaleModel> {
    public static final ResourceLocation DEFAULT_SKIN_LOCATION = Changed.modResource("textures/latex_squid_dog_female.png");

    public LatexSquidDogFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSquidDogFemaleModel(context.bakeLayer(LatexSquidDogFemaleModel.LAYER_LOCATION)), ArmorLatexFemaleSquidDogModel.MODEL_SET, 0.65f);
        this.addLayer(new DoubleItemInHandLayer<>(this, context.getItemInHandRenderer()));
        this.addLayer(new ExtraItemInHandLayer<>(this, context.getItemInHandRenderer(), 1, LatexSquidDogFemaleModel::translateToLowerTentapaw));
        this.addLayer(new ExtraItemInHandLayer<>(this, context.getItemInHandRenderer(), 2, LatexSquidDogFemaleModel::translateToUpperTentapaw));
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.fromInt(0x1b1b1b)).withIris(Color3.fromInt(0xdfdfdf)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSquidDogFemale entity) {
        return DEFAULT_SKIN_LOCATION;
    }
}