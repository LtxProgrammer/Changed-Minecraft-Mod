package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.LatexSharkMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexBuffSharkModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexSharkModel;
import net.ltxprogrammer.changed.entity.beast.LatexShark;
import net.ltxprogrammer.changed.entity.beast.LatexSharkMale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSharkMaleRenderer extends LatexHumanoidRenderer<LatexSharkMale, LatexSharkMaleModel, ArmorLatexBuffSharkModel<LatexSharkMale>> {
    public LatexSharkMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSharkMaleModel(context.bakeLayer(LatexSharkMaleModel.LAYER_LOCATION)),
                ArmorLatexBuffSharkModel::new, ArmorLatexBuffSharkModel.INNER_ARMOR, ArmorLatexBuffSharkModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSharkMale p_114482_) {
        return new ResourceLocation("changed:textures/latex_shark_male.png");
    }
}