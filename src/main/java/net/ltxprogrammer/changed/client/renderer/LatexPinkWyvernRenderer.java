package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.*;
import net.ltxprogrammer.changed.client.renderer.model.LatexPinkWyvernModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleDragonModel;
import net.ltxprogrammer.changed.entity.beast.LatexPinkWyvern;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexPinkWyvernRenderer extends AdvancedHumanoidRenderer<LatexPinkWyvern, LatexPinkWyvernModel, ArmorLatexMaleDragonModel<LatexPinkWyvern>> {
    public LatexPinkWyvernRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexPinkWyvernModel(context.bakeLayer(LatexPinkWyvernModel.LAYER_LOCATION)),
                ArmorLatexMaleDragonModel::new, ArmorLatexMaleDragonModel.INNER_ARMOR, ArmorLatexMaleDragonModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexTranslucentLayer<>(this, this.model, Changed.modResource("textures/latex_pink_wyvern_translucent.png")));
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.WHITE).withIris(Color3.fromInt(0x7889f3)).build());
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexPinkWyvern p_114482_) {
        return Changed.modResource("textures/latex_pink_wyvern.png");
    }
}