package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.LatexMimicPlantModel;
import net.ltxprogrammer.changed.client.renderer.model.LatexSilverFoxModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexWolfModel;
import net.ltxprogrammer.changed.entity.beast.LatexMimicPlant;
import net.ltxprogrammer.changed.entity.beast.LatexPurpleFox;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMimicPlantRenderer extends LatexHumanoidRenderer<LatexMimicPlant, LatexMimicPlantModel, ArmorLatexWolfModel<LatexMimicPlant>> {
    public LatexMimicPlantRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMimicPlantModel(context.bakeLayer(LatexMimicPlantModel.LAYER_LOCATION)),
                ArmorLatexWolfModel::new, ArmorLatexWolfModel.INNER_ARMOR, ArmorLatexWolfModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMimicPlant p_114482_) {
        return new ResourceLocation("changed:textures/latex_mimic_plant.png");
    }
}