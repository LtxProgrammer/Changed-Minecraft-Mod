package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.WhiteLatexKnightModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWhiteKnightModel;
import net.ltxprogrammer.changed.entity.beast.WhiteLatexKnight;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class WhiteLatexKnightRenderer extends AdvancedHumanoidRenderer<WhiteLatexKnight, WhiteLatexKnightModel, ArmorLatexWhiteKnightModel<WhiteLatexKnight>> {
    public WhiteLatexKnightRenderer(EntityRendererProvider.Context context) {
        super(context, new WhiteLatexKnightModel(context.bakeLayer(WhiteLatexKnightModel.LAYER_LOCATION)),
                ArmorLatexWhiteKnightModel::new, ArmorLatexWhiteKnightModel.INNER_ARMOR, ArmorLatexWhiteKnightModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.fromInt(0x1b1b1b)).withIris(Color3.fromInt(0xdfdfdf)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(WhiteLatexKnight p_114482_) {
        return Changed.modResource("textures/white_latex_knight.png");
    }
}