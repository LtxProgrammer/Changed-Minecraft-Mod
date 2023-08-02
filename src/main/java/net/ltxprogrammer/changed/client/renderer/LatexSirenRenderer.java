package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.model.LatexSirenModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorAbdomenModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorMermaidSharkAbdomenModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorUpperBodyModel;
import net.ltxprogrammer.changed.entity.beast.LatexSiren;
import net.ltxprogrammer.changed.item.AbdomenArmor;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSirenRenderer extends LatexHumanoidRenderer<LatexSiren, LatexSirenModel, ArmorUpperBodyModel<LatexSiren>> {
    public LatexSirenRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSirenModel(context.bakeLayer(LatexSirenModel.LAYER_LOCATION)),
                ArmorUpperBodyModel::new, ArmorUpperBodyModel.INNER_ARMOR, ArmorUpperBodyModel.OUTER_ARMOR,
                ArmorAbdomenModel::new, ArmorAbdomenModel.INNER_ARMOR, ArmorAbdomenModel.OUTER_ARMOR,
                AbdomenArmor::useAbdomenModel, AbdomenArmor::useInnerAbdomenModel, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSiren p_114482_) {
        return Changed.modResource("textures/latex_siren.png");
    }

    public static class Remodel extends LatexHumanoidRenderer<LatexSiren, LatexSirenModel.Remodel, ArmorUpperBodyModel.RemodelFemale<LatexSiren>> {
        public Remodel(EntityRendererProvider.Context context) {
            super(context, new LatexSirenModel.Remodel(context.bakeLayer(LatexSirenModel.LAYER_LOCATION)),
                    ArmorUpperBodyModel.RemodelFemale::new, ArmorUpperBodyModel.RemodelFemale.INNER_ARMOR, ArmorUpperBodyModel.RemodelFemale.OUTER_ARMOR,
                    ArmorMermaidSharkAbdomenModel.Remodel::new, ArmorMermaidSharkAbdomenModel.Remodel.INNER_ARMOR, ArmorMermaidSharkAbdomenModel.Remodel.OUTER_ARMOR,
                    AbdomenArmor::useAbdomenModel, AbdomenArmor::useInnerAbdomenModel, 0.5f);
        }

        @Override
        public ResourceLocation getTextureLocation(LatexSiren p_114482_) {
            return Changed.modResource("textures/remodel/latex_mermaid_shark_female.png");
        }
    }
}
