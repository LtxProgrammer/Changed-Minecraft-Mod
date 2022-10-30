package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.LatexSharkFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexSharkModel;
import net.ltxprogrammer.changed.entity.beast.LatexSharkFemale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSharkFemaleRenderer extends LatexHumanoidRenderer<LatexSharkFemale, LatexSharkFemaleModel, ArmorLatexSharkModel<LatexSharkFemale>> {
    public LatexSharkFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSharkFemaleModel(context.bakeLayer(LatexSharkFemaleModel.LAYER_LOCATION)),
                ArmorLatexSharkModel::new, ArmorLatexSharkModel.INNER_ARMOR, ArmorLatexSharkModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSharkFemale p_114482_) {
        return new ResourceLocation("changed:textures/latex_shark_female.png");
    }
}