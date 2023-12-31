package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexSharkModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleSharkModel;
import net.ltxprogrammer.changed.entity.beast.LatexShark;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexSharkRenderer extends LatexHumanoidRenderer<LatexShark, LatexSharkModel, ArmorLatexMaleSharkModel<LatexShark>> {
    public LatexSharkRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexSharkModel(context.bakeLayer(LatexSharkModel.LAYER_LOCATION)),
                ArmorLatexMaleSharkModel::new, ArmorLatexMaleSharkModel.INNER_ARMOR, ArmorLatexMaleSharkModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexShark p_114482_) {
        return Changed.modResource("textures/latex_shark.png");
    }
}