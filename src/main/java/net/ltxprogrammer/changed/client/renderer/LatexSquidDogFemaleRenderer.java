package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.*;
import net.ltxprogrammer.changed.client.renderer.model.LatexSquidDogFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleSquidDogModel;
import net.ltxprogrammer.changed.entity.beast.LatexSquidDogFemale;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSquidDogFemaleRenderer extends AdvancedHumanoidRenderer<LatexSquidDogFemale, LatexSquidDogFemaleModel, ArmorLatexFemaleSquidDogModel<LatexSquidDogFemale>> {
    public LatexSquidDogFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSquidDogFemaleModel(context.bakeLayer(LatexSquidDogFemaleModel.LAYER_LOCATION)), ArmorLatexFemaleSquidDogModel.MODEL_SET, 0.65f);
        this.addLayer(new DoubleItemInHandLayer<>(this));
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.fromInt(0x1b1b1b)).withIris(Color3.fromInt(0xdfdfdf)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSquidDogFemale p_114482_) {
        return Changed.modResource("textures/latex_squid_dog_female.png");
    }


    @Override
    protected void scale(LatexSquidDogFemale entity, PoseStack pose, float partialTick) {
        float f = 1.0525F;
        pose.scale(1.0525F, 1.0525F, 1.0525F);
    }
}