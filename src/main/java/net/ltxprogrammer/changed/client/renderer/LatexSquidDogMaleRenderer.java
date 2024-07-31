package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.*;
import net.ltxprogrammer.changed.client.renderer.model.LatexSquidDogMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleSquidDogModel;
import net.ltxprogrammer.changed.entity.beast.LatexSquidDogMale;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSquidDogMaleRenderer extends AdvancedHumanoidRenderer<LatexSquidDogMale, LatexSquidDogMaleModel, ArmorLatexMaleSquidDogModel<LatexSquidDogMale>> {
    public LatexSquidDogMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSquidDogMaleModel(context.bakeLayer(LatexSquidDogMaleModel.LAYER_LOCATION)),
                ArmorLatexMaleSquidDogModel::new, ArmorLatexMaleSquidDogModel.INNER_ARMOR, ArmorLatexMaleSquidDogModel.OUTER_ARMOR, 0.65f);
        this.addLayer(new DoubleItemInHandLayer<>(this));
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.fromInt(0x1b1b1b)).withIris(Color3.fromInt(0xdfdfdf)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSquidDogMale p_114482_) {
        return Changed.modResource("textures/latex_squid_dog_male.png");
    }

    @Override
    protected void scale(LatexSquidDogMale entity, PoseStack pose, float partialTick) {
        float f = 1.0525F;
        pose.scale(1.0525F, 1.0525F, 1.0525F);
    }
}