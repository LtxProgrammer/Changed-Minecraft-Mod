package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexRedPandaModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexSnowLeopardModel;
import net.ltxprogrammer.changed.entity.beast.LatexRedPanda;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexRedPandaRenderer extends LatexHumanoidRenderer<LatexRedPanda, LatexRedPandaModel, ArmorLatexSnowLeopardModel<LatexRedPanda>> {
    public LatexRedPandaRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexRedPandaModel(context.bakeLayer(LatexRedPandaModel.LAYER_LOCATION)),
                ArmorLatexSnowLeopardModel::new, ArmorLatexSnowLeopardModel.INNER_ARMOR, ArmorLatexSnowLeopardModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexRedPanda p_114482_) {
        return Changed.modResource("textures/latex_red_panda.png");
    }

    public static class Remodel extends LatexHumanoidRenderer<LatexRedPanda, LatexRedPandaModel.Remodel, ArmorLatexSnowLeopardModel.RemodelMale<LatexRedPanda>> {
        public Remodel(EntityRendererProvider.Context context) {
            super(context, new LatexRedPandaModel.Remodel(context.bakeLayer(LatexRedPandaModel.LAYER_LOCATION)),
                    ArmorLatexSnowLeopardModel.RemodelMale::new, ArmorLatexSnowLeopardModel.RemodelMale.INNER_ARMOR, ArmorLatexSnowLeopardModel.RemodelMale.OUTER_ARMOR, 0.5f);
        }

        @Override
        public ResourceLocation getTextureLocation(LatexRedPanda p_114482_) {
            return Changed.modResource("textures/remodel/latex_red_panda.png");
        }
    }
}