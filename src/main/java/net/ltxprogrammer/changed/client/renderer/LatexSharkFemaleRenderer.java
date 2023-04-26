package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexSharkFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexBuffSharkModel;
import net.ltxprogrammer.changed.entity.beast.LatexSharkFemale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSharkFemaleRenderer extends LatexHumanoidRenderer<LatexSharkFemale, LatexSharkFemaleModel, ArmorLatexBuffSharkModel<LatexSharkFemale>> {
    public LatexSharkFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSharkFemaleModel(context.bakeLayer(LatexSharkFemaleModel.LAYER_LOCATION)),
                ArmorLatexBuffSharkModel::new, ArmorLatexBuffSharkModel.INNER_ARMOR, ArmorLatexBuffSharkModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSharkFemale p_114482_) {
        return Changed.modResource("textures/latex_shark_female.png");
    }

    public static class Remodel extends LatexHumanoidRenderer<LatexSharkFemale, LatexSharkFemaleModel.Remodel, ArmorLatexBuffSharkModel<LatexSharkFemale>> {
        public Remodel(EntityRendererProvider.Context context) {
            super(context, new LatexSharkFemaleModel.Remodel(context.bakeLayer(LatexSharkFemaleModel.LAYER_LOCATION)),
                    ArmorLatexBuffSharkModel::new, ArmorLatexBuffSharkModel.INNER_ARMOR, ArmorLatexBuffSharkModel.OUTER_ARMOR, 0.5f);
        }

        @Override
        public ResourceLocation getTextureLocation(LatexSharkFemale p_114482_) {
            return Changed.modResource("textures/remodel/latex_shark_buff_female.png");
        }
    }
}