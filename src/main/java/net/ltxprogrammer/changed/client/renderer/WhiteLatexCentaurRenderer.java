package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.*;
import net.ltxprogrammer.changed.client.renderer.model.WhiteLatexCentaurModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexCentaurLowerModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexCentaurUpperModel;
import net.ltxprogrammer.changed.entity.beast.WhiteLatexCentaur;
import net.ltxprogrammer.changed.item.QuadrupedalArmor;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;

public class WhiteLatexCentaurRenderer extends AdvancedHumanoidRenderer<WhiteLatexCentaur, WhiteLatexCentaurModel, ArmorLatexCentaurUpperModel> {
    public WhiteLatexCentaurRenderer(EntityRendererProvider.Context context) {
        super(context, new WhiteLatexCentaurModel(context.bakeLayer(WhiteLatexCentaurModel.LAYER_LOCATION)),
                ArmorLatexCentaurUpperModel::new, ArmorLatexCentaurUpperModel.INNER_ARMOR, ArmorLatexCentaurUpperModel.OUTER_ARMOR,
                ArmorLatexCentaurLowerModel::new, ArmorLatexCentaurLowerModel.INNER_ARMOR, ArmorLatexCentaurLowerModel.OUTER_ARMOR,
                QuadrupedalArmor::useQuadrupedalModel, QuadrupedalArmor::useInnerQuadrupedalModel, 0.7f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.fromInt(0x1b1b1b)).withIris(Color3.fromInt(0xdfdfdf)).build());
        this.addLayer(new SaddleLayer<>(this, getModel(), Changed.modResource("textures/white_latex_centaur_saddle.png")));
        this.addLayer(new TaurChestPackLayer<>(this, context.getModelSet()));
        this.addLayer(TransfurCapeLayer.shortCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(WhiteLatexCentaur p_114482_) {
        return Changed.modResource("textures/white_latex_centaur.png");
    }
}