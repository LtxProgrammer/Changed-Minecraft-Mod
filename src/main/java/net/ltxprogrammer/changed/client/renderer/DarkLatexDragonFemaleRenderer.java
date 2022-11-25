package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.DarkLatexDragonFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexBlueDragonModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexDragonModel;
import net.ltxprogrammer.changed.entity.beast.DarkLatexDragonFemale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DarkLatexDragonFemaleRenderer extends LatexHumanoidRenderer<DarkLatexDragonFemale, DarkLatexDragonFemaleModel, ArmorLatexBlueDragonModel<DarkLatexDragonFemale>> {
    public DarkLatexDragonFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new DarkLatexDragonFemaleModel(context.bakeLayer(DarkLatexDragonFemaleModel.LAYER_LOCATION)),
                ArmorLatexBlueDragonModel::new, ArmorLatexBlueDragonModel.INNER_ARMOR, ArmorLatexBlueDragonModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(DarkLatexDragonFemale p_114482_) {
        return new ResourceLocation("changed:textures/dark_latex_dragon_female.png");
    }
}