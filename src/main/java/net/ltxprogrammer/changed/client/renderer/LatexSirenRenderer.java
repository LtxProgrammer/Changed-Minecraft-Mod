package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexSirenModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorMermaidSharkFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorSirenAbdomenModel;
import net.ltxprogrammer.changed.entity.beast.LatexSiren;
import net.ltxprogrammer.changed.item.AbdomenArmor;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSirenRenderer extends AdvancedHumanoidRenderer<LatexSiren, LatexSirenModel, ArmorMermaidSharkFemaleModel<LatexSiren>> {
    public LatexSirenRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSirenModel(context.bakeLayer(LatexSirenModel.LAYER_LOCATION)),
                ArmorMermaidSharkFemaleModel::new, ArmorMermaidSharkFemaleModel.INNER_ARMOR, ArmorMermaidSharkFemaleModel.OUTER_ARMOR,
                ArmorSirenAbdomenModel::new, ArmorSirenAbdomenModel.INNER_ARMOR, ArmorSirenAbdomenModel.OUTER_ARMOR,
                AbdomenArmor::useAbdomenModel, AbdomenArmor::useInnerAbdomenModel, 0.5f);
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(GasMaskLayer.forLargeSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexSiren p_114482_) {
        return Changed.modResource("textures/latex_siren.png");
    }
}
