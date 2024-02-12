package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.GooSharkModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleSharkModel;
import net.ltxprogrammer.changed.entity.beast.GooShark;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class GooSharkRenderer extends AdvancedHumanoidRenderer<GooShark, GooSharkModel, ArmorLatexMaleSharkModel<GooShark>> {
    public GooSharkRenderer(EntityRendererProvider.Context context) {
        super(context, new GooSharkModel(context.bakeLayer(GooSharkModel.LAYER_LOCATION)),
                ArmorLatexMaleSharkModel::new, ArmorLatexMaleSharkModel.INNER_ARMOR, ArmorLatexMaleSharkModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new GooParticlesLayer<>(this, this.model));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(GooShark p_114482_) {
        return Changed.modResource("textures/latex_shark.png");
    }
}