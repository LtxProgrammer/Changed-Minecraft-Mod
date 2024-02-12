package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexMermaidSharkModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorMermaidSharkAbdomenModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorUpperBodyModel;
import net.ltxprogrammer.changed.entity.beast.GooMermaidShark;
import net.ltxprogrammer.changed.item.AbdomenArmor;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMermaidSharkRenderer extends AdvancedHumanoidRenderer<GooMermaidShark, LatexMermaidSharkModel, ArmorUpperBodyModel<GooMermaidShark>> {
    public LatexMermaidSharkRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMermaidSharkModel(context.bakeLayer(LatexMermaidSharkModel.LAYER_LOCATION)),
                ArmorUpperBodyModel::new, ArmorUpperBodyModel.INNER_ARMOR, ArmorUpperBodyModel.OUTER_ARMOR,
                ArmorMermaidSharkAbdomenModel::new, ArmorMermaidSharkAbdomenModel.INNER_ARMOR, ArmorMermaidSharkAbdomenModel.OUTER_ARMOR,
                AbdomenArmor::useAbdomenModel, AbdomenArmor::useInnerAbdomenModel, 0.5f);
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(new GooParticlesLayer<>(this, this.model));
    }

    @Override
    public ResourceLocation getTextureLocation(GooMermaidShark p_114482_) {
        return Changed.modResource("textures/latex_mermaid_shark.png");
    }
}
