package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.BlueGooDragonModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleDragonModel;
import net.ltxprogrammer.changed.entity.beast.BlueGooDragon;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexBlueDragonRenderer extends AdvancedHumanoidRenderer<BlueGooDragon, BlueGooDragonModel, ArmorLatexMaleDragonModel<BlueGooDragon>> {
    public LatexBlueDragonRenderer(EntityRendererProvider.Context context) {
        super(context, new BlueGooDragonModel(context.bakeLayer(BlueGooDragonModel.LAYER_LOCATION)),
                ArmorLatexMaleDragonModel::new, ArmorLatexMaleDragonModel.INNER_ARMOR, ArmorLatexMaleDragonModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new GooParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer.fixedColor(Color3.WHITE),CustomEyesLayer.fixedColor(Color3.parseHex("#26c841"))));
    }

    @Override
    public ResourceLocation getTextureLocation(BlueGooDragon p_114482_) {
        return Changed.modResource("textures/latex_blue_dragon.png");
    }
}