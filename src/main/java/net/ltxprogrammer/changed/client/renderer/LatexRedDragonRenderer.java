package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexRedDragonModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWingedDragonModel;
import net.ltxprogrammer.changed.entity.beast.LatexRedDragon;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexRedDragonRenderer extends AdvancedHumanoidRenderer<LatexRedDragon, LatexRedDragonModel, ArmorLatexMaleWingedDragonModel<LatexRedDragon>> {
    public LatexRedDragonRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexRedDragonModel(context.bakeLayer(LatexRedDragonModel.LAYER_LOCATION)), ArmorLatexMaleWingedDragonModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.WHITE).withIris(Color3.fromInt(0xffe93f)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexRedDragon p_114482_) {
        return Changed.modResource("textures/latex_red_dragon.png");
    }
}