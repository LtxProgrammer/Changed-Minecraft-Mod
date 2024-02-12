package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.GooSharkMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexBuffSharkModel;
import net.ltxprogrammer.changed.entity.beast.GooSharkMale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSharkMaleRenderer extends AdvancedHumanoidRenderer<GooSharkMale, GooSharkMaleModel, ArmorLatexBuffSharkModel<GooSharkMale>> {
    public LatexSharkMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new GooSharkMaleModel(context.bakeLayer(GooSharkMaleModel.LAYER_LOCATION)),
                ArmorLatexBuffSharkModel::new, ArmorLatexBuffSharkModel.INNER_ARMOR, ArmorLatexBuffSharkModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(GooSharkMale p_114482_) {
        return Changed.modResource("textures/latex_shark_male.png");
    }

    public static class Remodel extends AdvancedHumanoidRenderer<GooSharkMale, GooSharkMaleModel.Remodel, ArmorLatexBuffSharkModel<GooSharkMale>> {
        public Remodel(EntityRendererProvider.Context context) {
            super(context, new GooSharkMaleModel.Remodel(context.bakeLayer(GooSharkMaleModel.LAYER_LOCATION)),
                    ArmorLatexBuffSharkModel::new, ArmorLatexBuffSharkModel.INNER_ARMOR, ArmorLatexBuffSharkModel.OUTER_ARMOR, 0.5f);
        }

        @Override
        public ResourceLocation getTextureLocation(GooSharkMale p_114482_) {
            return Changed.modResource("textures/remodel/latex_shark_buff_male.png");
        }
    }
}