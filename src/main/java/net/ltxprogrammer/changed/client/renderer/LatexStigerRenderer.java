package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexStigerModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexStigerModel;
import net.ltxprogrammer.changed.entity.beast.LatexStiger;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexStigerRenderer extends AdvancedHumanoidRenderer<LatexStiger, LatexStigerModel, ArmorLatexStigerModel<LatexStiger>> {
    public LatexStigerRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexStigerModel(context.bakeLayer(LatexStigerModel.LAYER_LOCATION)),
                ArmorLatexStigerModel::new, ArmorLatexStigerModel.INNER_ARMOR, ArmorLatexStigerModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.WHITE).withIris(Color3.fromInt(0xe1517b)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexStiger p_114482_) {
        return Changed.modResource("textures/latex_stiger.png");
    }
}