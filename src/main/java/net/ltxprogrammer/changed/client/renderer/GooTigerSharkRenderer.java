package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.GooTigerSharkModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleSharkModel;
import net.ltxprogrammer.changed.entity.beast.GooTigerShark;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GooTigerSharkRenderer extends AdvancedHumanoidRenderer<GooTigerShark, GooTigerSharkModel, ArmorLatexMaleSharkModel<GooTigerShark>> {
    public GooTigerSharkRenderer(EntityRendererProvider.Context context) {
        super(context, new GooTigerSharkModel(context.bakeLayer(GooTigerSharkModel.LAYER_LOCATION)),
                ArmorLatexMaleSharkModel::new, ArmorLatexMaleSharkModel.INNER_ARMOR, ArmorLatexMaleSharkModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new GooParticlesLayer<>(this, this.model));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(GooTigerShark p_114482_) {
        return Changed.modResource("textures/latex_tiger_shark.png");
    }
}