package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.*;
import net.ltxprogrammer.changed.client.renderer.model.LightLatexKnightFusionModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.LightLatexKnightFusion;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LightLatexKnightFusionRenderer extends AdvancedHumanoidRenderer<LightLatexKnightFusion, LightLatexKnightFusionModel, ArmorLatexMaleWolfModel<LightLatexKnightFusion>> {
    public LightLatexKnightFusionRenderer(EntityRendererProvider.Context context) {
        super(context, new LightLatexKnightFusionModel(context.bakeLayer(LightLatexKnightFusionModel.LAYER_LOCATION)),
                ArmorLatexMaleWolfModel::new, ArmorLatexMaleWolfModel.INNER_ARMOR, ArmorLatexMaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new AdditionalEyesLayer<>(this, context.getModelSet(), Changed.modResource("light_latex_knight_fusion")));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(),
                CustomEyesLayer.fixedColor(Color3.parseHex("#000000")),
                CustomEyesLayer::irisColorLeft,
                CustomEyesLayer::irisColorRight));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LightLatexKnightFusion p_114482_) {
        return Changed.modResource("textures/light_latex_knight_fusion.png");
    }
}