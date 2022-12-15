package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.client.renderer.model.LatexMermaidSharkModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorUpperBodyModel;
import net.ltxprogrammer.changed.entity.beast.LatexMermaidShark;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMermaidSharkRenderer extends LatexHumanoidRenderer<LatexMermaidShark, LatexMermaidSharkModel, ArmorUpperBodyModel<LatexMermaidShark>> {
    public LatexMermaidSharkRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMermaidSharkModel(context.bakeLayer(LatexMermaidSharkModel.LAYER_LOCATION)),
                ArmorUpperBodyModel::new, ArmorUpperBodyModel.INNER_ARMOR, ArmorUpperBodyModel.OUTER_ARMOR, 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMermaidShark p_114482_) {
        return new ResourceLocation("changed:textures/latex_mermaid_shark.png");
    }
}
