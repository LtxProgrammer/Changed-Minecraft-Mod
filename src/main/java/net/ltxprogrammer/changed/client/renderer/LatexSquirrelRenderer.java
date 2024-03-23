package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexSquirrelModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorNoTailModel;
import net.ltxprogrammer.changed.entity.beast.LatexSquirrel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSquirrelRenderer extends AdvancedHumanoidRenderer<LatexSquirrel, LatexSquirrelModel, ArmorNoTailModel<LatexSquirrel>> {
    public LatexSquirrelRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSquirrelModel(context.bakeLayer(LatexSquirrelModel.LAYER_LOCATION)),
                ArmorNoTailModel::new, ArmorNoTailModel.INNER_ARMOR, ArmorNoTailModel.OUTER_ARMOR, 0.5f);
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSquirrel p_114482_) {
        return Changed.modResource("textures/latex_squirrel.png");
    }
}