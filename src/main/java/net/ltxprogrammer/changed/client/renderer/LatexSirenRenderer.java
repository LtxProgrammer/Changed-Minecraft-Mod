package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
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
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(GasMaskLayer.forLargeSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSiren p_114482_) {
        return Changed.modResource("textures/latex_siren.png");
    }
}
