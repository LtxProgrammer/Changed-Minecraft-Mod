package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexYuinModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexDeerModel;
import net.ltxprogrammer.changed.entity.beast.LatexYuin;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexYuinRenderer extends AdvancedHumanoidRenderer<LatexYuin, LatexYuinModel, ArmorLatexDeerModel<LatexYuin>> {
    public LatexYuinRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexYuinModel(context.bakeLayer(LatexYuinModel.LAYER_LOCATION)),
                ArmorLatexDeerModel::new, ArmorLatexDeerModel.INNER_ARMOR, ArmorLatexDeerModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer.fixedColor(Color3.WHITE),CustomEyesLayer.fixedColor(Color3.parseHex("#ffc301"))));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexYuin p_114482_) {
        return Changed.modResource("textures/latex_yuin.png");
    }
}