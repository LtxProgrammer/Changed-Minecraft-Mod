package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexMermaidSharkModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorMermaidSharkAbdomenModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorMermaidSharkMaleModel;
import net.ltxprogrammer.changed.entity.beast.LatexMermaidShark;
import net.ltxprogrammer.changed.item.AbdomenArmor;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMermaidSharkRenderer extends AdvancedHumanoidRenderer<LatexMermaidShark, LatexMermaidSharkModel, ArmorMermaidSharkMaleModel<LatexMermaidShark>> {
    public LatexMermaidSharkRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMermaidSharkModel(context.bakeLayer(LatexMermaidSharkModel.LAYER_LOCATION)),
                ArmorMermaidSharkMaleModel::new, ArmorMermaidSharkMaleModel.INNER_ARMOR, ArmorMermaidSharkMaleModel.OUTER_ARMOR,
                ArmorMermaidSharkAbdomenModel::new, ArmorMermaidSharkAbdomenModel.INNER_ARMOR, ArmorMermaidSharkAbdomenModel.OUTER_ARMOR,
                AbdomenArmor::useAbdomenModel, AbdomenArmor::useInnerAbdomenModel, 0.5f);
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMermaidShark p_114482_) {
        return Changed.modResource("textures/latex_mermaid_shark.png");
    }
}
