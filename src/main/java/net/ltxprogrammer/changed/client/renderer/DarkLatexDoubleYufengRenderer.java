package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.DarkLatexDoubleYufengModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleDoubleHeadedWingedDragonModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWingedDragonModel;
import net.ltxprogrammer.changed.entity.beast.DarkLatexDoubleYufeng;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DarkLatexDoubleYufengRenderer extends AdvancedHumanoidRenderer<DarkLatexDoubleYufeng, DarkLatexDoubleYufengModel, ArmorLatexMaleDoubleHeadedWingedDragonModel<DarkLatexDoubleYufeng>> {
    public DarkLatexDoubleYufengRenderer(EntityRendererProvider.Context context) {
        super(context, new DarkLatexDoubleYufengModel(context.bakeLayer(DarkLatexDoubleYufengModel.LAYER_LOCATION)), ArmorLatexMaleDoubleHeadedWingedDragonModel.MODEL_SET, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel(), model::isPartNotMask));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withSclera(Color3.fromInt(0x242424))
                .withIris(CustomEyesLayer.fixedIfNotDarkLatexOverrideLeft(Color3.WHITE),
                        CustomEyesLayer.fixedIfNotDarkLatexOverrideRight(Color3.WHITE))
                .build());
    }

    @Override
    public ResourceLocation getTextureLocation(DarkLatexDoubleYufeng p_114482_) {
        return Changed.modResource("textures/dark_latex_yufeng.png");
    }
}