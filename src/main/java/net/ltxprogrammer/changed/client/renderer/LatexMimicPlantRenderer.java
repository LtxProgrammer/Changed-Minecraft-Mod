package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexMimicPlantModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorNoTailModel;
import net.ltxprogrammer.changed.entity.beast.LatexMimicPlant;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMimicPlantRenderer extends AdvancedHumanoidRenderer<LatexMimicPlant, LatexMimicPlantModel, ArmorNoTailModel<LatexMimicPlant>> {
    public LatexMimicPlantRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMimicPlantModel(context.bakeLayer(LatexMimicPlantModel.LAYER_LOCATION)),
                ArmorNoTailModel::new, ArmorNoTailModel.INNER_ARMOR, ArmorNoTailModel.OUTER_ARMOR, 0.5f);
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMimicPlant p_114482_) {
        return Changed.modResource("textures/latex_mimic_plant.png");
    }
}