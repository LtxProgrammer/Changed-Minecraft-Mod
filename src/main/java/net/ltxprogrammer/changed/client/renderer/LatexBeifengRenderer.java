package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexBeifengModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexBlueDragonModel;
import net.ltxprogrammer.changed.entity.beast.LatexBeifeng;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexBeifengRenderer extends LatexHumanoidRenderer<LatexBeifeng, LatexBeifengModel, ArmorLatexBlueDragonModel<LatexBeifeng>> {
    public LatexBeifengRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexBeifengModel(context.bakeLayer(LatexBeifengModel.LAYER_LOCATION)),
                ArmorLatexBlueDragonModel::new, ArmorLatexBlueDragonModel.INNER_ARMOR, ArmorLatexBlueDragonModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexBeifeng p_114482_) {
        return Changed.modResource("textures/latex_beifeng.png");
    }

    public static class Remodel extends LatexHumanoidRenderer<LatexBeifeng, LatexBeifengModel.Remodel, ArmorLatexBlueDragonModel.RemodelMale<LatexBeifeng>> {
        public Remodel(EntityRendererProvider.Context context) {
            super(context, new LatexBeifengModel.Remodel(context.bakeLayer(LatexBeifengModel.LAYER_LOCATION)),
                    ArmorLatexBlueDragonModel.RemodelMale::new, ArmorLatexBlueDragonModel.RemodelMale.INNER_ARMOR, ArmorLatexBlueDragonModel.RemodelMale.OUTER_ARMOR, 0.5f);
        }

        @Override
        public ResourceLocation getTextureLocation(LatexBeifeng p_114482_) {
            return Changed.modResource("textures/remodel/latex_beifeng.png");
        }
    }
}