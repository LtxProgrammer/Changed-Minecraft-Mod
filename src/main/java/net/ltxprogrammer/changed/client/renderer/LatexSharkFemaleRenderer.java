package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.GooSharkFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexBuffSharkModel;
import net.ltxprogrammer.changed.entity.beast.GooSharkFemale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSharkFemaleRenderer extends AdvancedHumanoidRenderer<GooSharkFemale, GooSharkFemaleModel, ArmorLatexBuffSharkModel<GooSharkFemale>> {
    public LatexSharkFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new GooSharkFemaleModel(context.bakeLayer(GooSharkFemaleModel.LAYER_LOCATION)),
                ArmorLatexBuffSharkModel::new, ArmorLatexBuffSharkModel.INNER_ARMOR, ArmorLatexBuffSharkModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(GooSharkFemale p_114482_) {
        return Changed.modResource("textures/latex_shark_female.png");
    }

    public static class Remodel extends AdvancedHumanoidRenderer<GooSharkFemale, GooSharkFemaleModel.Remodel, ArmorLatexBuffSharkModel<GooSharkFemale>> {
        public Remodel(EntityRendererProvider.Context context) {
            super(context, new GooSharkFemaleModel.Remodel(context.bakeLayer(GooSharkFemaleModel.LAYER_LOCATION)),
                    ArmorLatexBuffSharkModel::new, ArmorLatexBuffSharkModel.INNER_ARMOR, ArmorLatexBuffSharkModel.OUTER_ARMOR, 0.5f);
        }

        @Override
        public ResourceLocation getTextureLocation(GooSharkFemale p_114482_) {
            return Changed.modResource("textures/remodel/latex_shark_buff_female.png");
        }
    }
}