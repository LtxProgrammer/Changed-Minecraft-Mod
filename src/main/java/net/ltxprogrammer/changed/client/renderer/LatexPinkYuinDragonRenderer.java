package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexPinkYuinDragonModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWingedDragonModel;
import net.ltxprogrammer.changed.entity.beast.LatexPinkYuinDragon;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexPinkYuinDragonRenderer extends LatexHumanoidRenderer<LatexPinkYuinDragon, LatexPinkYuinDragonModel, ArmorLatexMaleWingedDragonModel<LatexPinkYuinDragon>>  {
    public LatexPinkYuinDragonRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexPinkYuinDragonModel(context.bakeLayer(LatexPinkYuinDragonModel.LAYER_LOCATION)),
                ArmorLatexMaleWingedDragonModel::new, ArmorLatexMaleWingedDragonModel.INNER_ARMOR, ArmorLatexMaleWingedDragonModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer.fixedColor(Color3.WHITE),CustomEyesLayer.fixedColor(Color3.parseHex("#7889f3"))));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexPinkYuinDragon p_114482_) {
        return Changed.modResource("textures/latex_pink_yuin_dragon.png");
    }
}