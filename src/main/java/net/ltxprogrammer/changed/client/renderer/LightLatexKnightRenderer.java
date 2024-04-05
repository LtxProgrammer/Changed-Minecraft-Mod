package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.LightLatexKnightModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWhiteKnightModel;
import net.ltxprogrammer.changed.entity.beast.LightLatexKnight;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LightLatexKnightRenderer extends AdvancedHumanoidRenderer<LightLatexKnight, LightLatexKnightModel, ArmorLatexWhiteKnightModel<LightLatexKnight>> {
    public LightLatexKnightRenderer(EntityRendererProvider.Context context) {
        super(context, new LightLatexKnightModel(context.bakeLayer(LightLatexKnightModel.LAYER_LOCATION)),
                ArmorLatexWhiteKnightModel::new, ArmorLatexWhiteKnightModel.INNER_ARMOR, ArmorLatexWhiteKnightModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(),
                CustomEyesLayer.fixedColor(Color3.parseHex("#1b1b1b")),
                CustomEyesLayer.fixedColor(Color3.parseHex("#dfdfdf"))));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LightLatexKnight p_114482_) {
        return Changed.modResource("textures/light_latex_knight.png");
    }
}