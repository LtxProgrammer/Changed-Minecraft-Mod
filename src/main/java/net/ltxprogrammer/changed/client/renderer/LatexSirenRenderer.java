package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.GooSirenModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorAbdomenModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorUpperBodyModel;
import net.ltxprogrammer.changed.entity.beast.GooSiren;
import net.ltxprogrammer.changed.item.AbdomenArmor;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSirenRenderer extends AdvancedHumanoidRenderer<GooSiren, GooSirenModel, ArmorUpperBodyModel<GooSiren>> {
    public LatexSirenRenderer(EntityRendererProvider.Context context) {
        super(context, new GooSirenModel(context.bakeLayer(GooSirenModel.LAYER_LOCATION)),
                ArmorUpperBodyModel::new, ArmorUpperBodyModel.INNER_ARMOR, ArmorUpperBodyModel.OUTER_ARMOR,
                ArmorAbdomenModel::new, ArmorAbdomenModel.INNER_ARMOR, ArmorAbdomenModel.OUTER_ARMOR,
                AbdomenArmor::useAbdomenModel, AbdomenArmor::useInnerAbdomenModel, 0.5f);
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(new GooParticlesLayer<>(this, this.model));
    }

    @Override
    public ResourceLocation getTextureLocation(GooSiren p_114482_) {
        return Changed.modResource("textures/latex_siren.png");
    }
}
