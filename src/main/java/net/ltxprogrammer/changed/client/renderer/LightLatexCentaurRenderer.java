package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.*;
import net.ltxprogrammer.changed.client.renderer.model.LightLatexCentaurModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexCentaurLowerModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexCentaurUpperModel;
import net.ltxprogrammer.changed.entity.beast.LightLatexCentaur;
import net.ltxprogrammer.changed.item.QuadrupedalArmor;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;

public class LightLatexCentaurRenderer extends AdvancedHumanoidRenderer<LightLatexCentaur, LightLatexCentaurModel, ArmorLatexCentaurUpperModel> {
    public LightLatexCentaurRenderer(EntityRendererProvider.Context context) {
        super(context, new LightLatexCentaurModel(context.bakeLayer(LightLatexCentaurModel.LAYER_LOCATION)),
                ArmorLatexCentaurUpperModel::new, ArmorLatexCentaurUpperModel.INNER_ARMOR, ArmorLatexCentaurUpperModel.OUTER_ARMOR,
                ArmorLatexCentaurLowerModel::new, ArmorLatexCentaurLowerModel.INNER_ARMOR, ArmorLatexCentaurLowerModel.OUTER_ARMOR,
                QuadrupedalArmor::useQuadrupedalModel, QuadrupedalArmor::useInnerQuadrupedalModel, 0.7f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(),
                CustomEyesLayer.fixedColor(Color3.parseHex("#1b1b1b")),
                CustomEyesLayer.fixedColor(Color3.parseHex("#dfdfdf"))));
        this.addLayer(new SaddleLayer<>(this, getModel(), Changed.modResource("textures/light_latex_centaur_saddle.png")));
        this.addLayer(new TaurChestPackLayer<>(this, context.getModelSet()));
        this.addLayer(TransfurCapeLayer.shortCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LightLatexCentaur p_114482_) {
        return Changed.modResource("textures/light_latex_centaur.png");
    }
}