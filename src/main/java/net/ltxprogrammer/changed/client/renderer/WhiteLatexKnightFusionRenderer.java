package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.*;
import net.ltxprogrammer.changed.client.renderer.model.WhiteLatexKnightFusionModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.WhiteLatexKnightFusion;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class WhiteLatexKnightFusionRenderer extends AdvancedHumanoidRenderer<WhiteLatexKnightFusion, WhiteLatexKnightFusionModel, ArmorLatexMaleWolfModel<WhiteLatexKnightFusion>> {
    public WhiteLatexKnightFusionRenderer(EntityRendererProvider.Context context) {
        super(context, new WhiteLatexKnightFusionModel(context.bakeLayer(WhiteLatexKnightFusionModel.LAYER_LOCATION)),
                ArmorLatexMaleWolfModel::new, ArmorLatexMaleWolfModel.INNER_ARMOR, ArmorLatexMaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new AdditionalEyesLayer<>(this, context.getModelSet(), Changed.modResource("white_latex_knight_fusion")));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.fromInt(0x000000)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(WhiteLatexKnightFusion p_114482_) {
        return Changed.modResource("textures/white_latex_knight_fusion.png");
    }
}