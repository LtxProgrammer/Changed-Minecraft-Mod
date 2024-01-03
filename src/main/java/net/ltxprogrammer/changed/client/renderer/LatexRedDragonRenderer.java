package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexRedDragonModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWingedDragonModel;
import net.ltxprogrammer.changed.entity.beast.LatexRedDragon;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexRedDragonRenderer extends LatexHumanoidRenderer<LatexRedDragon, LatexRedDragonModel, ArmorLatexMaleWingedDragonModel<LatexRedDragon>>  {
           public LatexRedDragonRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexRedDragonModel(context.bakeLayer(LatexRedDragonModel.LAYER_LOCATION)),
                ArmorLatexMaleWingedDragonModel::new, ArmorLatexMaleWingedDragonModel.INNER_ARMOR, ArmorLatexMaleWingedDragonModel.OUTER_ARMOR, 0.5f);
               this.addLayer(new LatexParticlesLayer<>(this, getModel()));
               this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer.fixedColor(Color3.WHITE),CustomEyesLayer.fixedColor(Color3.parseHex("#ffe93f"))));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexRedDragon p_114482_) {
        return Changed.modResource("textures/latex_red_dragon.png");
    }
}