package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexMimicPlantModel;
import net.ltxprogrammer.changed.client.renderer.model.LatexSquirrelModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorNoTailModel;
import net.ltxprogrammer.changed.entity.beast.LatexMimicPlant;
import net.ltxprogrammer.changed.entity.beast.LatexSquirrel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSquirrelRenderer extends LatexHumanoidRenderer<LatexSquirrel, LatexSquirrelModel, ArmorNoTailModel<LatexSquirrel>> {
    public LatexSquirrelRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSquirrelModel(context.bakeLayer(LatexSquirrelModel.LAYER_LOCATION)),
                ArmorNoTailModel::new, ArmorNoTailModel.INNER_ARMOR, ArmorNoTailModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSquirrel p_114482_) {
        return Changed.modResource("textures/latex_squirrel.png");
    }
}