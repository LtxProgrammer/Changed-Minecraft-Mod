package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexLeafModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexBigTailDragonModel;
import net.ltxprogrammer.changed.entity.beast.LatexLeaf;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexLeafRenderer extends LatexHumanoidRenderer<LatexLeaf, LatexLeafModel, ArmorLatexBigTailDragonModel<LatexLeaf>> {
    public LatexLeafRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexLeafModel(context.bakeLayer(LatexLeafModel.LAYER_LOCATION)),
                ArmorLatexBigTailDragonModel::new, ArmorLatexBigTailDragonModel.INNER_ARMOR, ArmorLatexBigTailDragonModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(),
                CustomEyesLayer.fixedColor(Color3.parseHex("#969696")),
                CustomEyesLayer.fixedColor(Color3.parseHex("#4e4e4e"))));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexLeaf p_114482_) {
        return Changed.modResource("textures/latex_leaf.png");
    }
}