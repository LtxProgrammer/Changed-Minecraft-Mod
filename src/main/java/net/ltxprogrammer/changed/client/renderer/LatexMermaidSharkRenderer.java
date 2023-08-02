package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexMermaidSharkModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorMermaidSharkAbdomenModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorUpperBodyModel;
import net.ltxprogrammer.changed.entity.beast.LatexMermaidShark;
import net.ltxprogrammer.changed.item.AbdomenArmor;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMermaidSharkRenderer extends LatexHumanoidRenderer<LatexMermaidShark, LatexMermaidSharkModel, ArmorUpperBodyModel<LatexMermaidShark>> {
    public LatexMermaidSharkRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMermaidSharkModel(context.bakeLayer(LatexMermaidSharkModel.LAYER_LOCATION)),
                ArmorUpperBodyModel::new, ArmorUpperBodyModel.INNER_ARMOR, ArmorUpperBodyModel.OUTER_ARMOR,
                ArmorMermaidSharkAbdomenModel::new, ArmorMermaidSharkAbdomenModel.INNER_ARMOR, ArmorMermaidSharkAbdomenModel.OUTER_ARMOR,
                AbdomenArmor::useAbdomenModel, AbdomenArmor::useInnerAbdomenModel, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMermaidShark p_114482_) {
        return Changed.modResource("textures/latex_mermaid_shark.png");
    }

    public static class Remodel extends LatexHumanoidRenderer<LatexMermaidShark, LatexMermaidSharkModel.Remodel, ArmorUpperBodyModel.RemodelMale<LatexMermaidShark>> {
        public Remodel(EntityRendererProvider.Context context) {
            super(context, new LatexMermaidSharkModel.Remodel(context.bakeLayer(LatexMermaidSharkModel.LAYER_LOCATION)),
                    ArmorUpperBodyModel.RemodelMale::new, ArmorUpperBodyModel.RemodelMale.INNER_ARMOR, ArmorUpperBodyModel.RemodelMale.OUTER_ARMOR,
                    ArmorMermaidSharkAbdomenModel.Remodel::new, ArmorMermaidSharkAbdomenModel.Remodel.INNER_ARMOR, ArmorMermaidSharkAbdomenModel.Remodel.OUTER_ARMOR,
                    AbdomenArmor::useAbdomenModel, AbdomenArmor::useInnerAbdomenModel, 0.5f);
        }

        @Override
        public ResourceLocation getTextureLocation(LatexMermaidShark p_114482_) {
            return Changed.modResource("textures/remodel/latex_mermaid_shark_male.png");
        }
    }
}
