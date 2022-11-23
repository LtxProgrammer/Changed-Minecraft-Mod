package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.DarkLatexDragonMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexBlueDragonModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexDragonModel;
import net.ltxprogrammer.changed.entity.beast.DarkLatexDragonMale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class DarkLatexDragonMaleRenderer extends LatexHumanoidRenderer<DarkLatexDragonMale, DarkLatexDragonMaleModel, ArmorLatexBlueDragonModel<DarkLatexDragonMale>> {
    public DarkLatexDragonMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new DarkLatexDragonMaleModel(context.bakeLayer(DarkLatexDragonMaleModel.LAYER_LOCATION)),
                ArmorLatexBlueDragonModel::new, ArmorLatexBlueDragonModel.INNER_ARMOR, ArmorLatexBlueDragonModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(DarkLatexDragonMale p_114482_) {
        return new ResourceLocation("changed:textures/dark_latex_dragon_male.png");
    }
}