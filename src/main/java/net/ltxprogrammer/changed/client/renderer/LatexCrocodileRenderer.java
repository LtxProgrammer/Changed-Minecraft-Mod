package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexCrocodileModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleSharkModel;
import net.ltxprogrammer.changed.entity.beast.LatexCrocodile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexCrocodileRenderer extends AdvancedHumanoidRenderer<LatexCrocodile, LatexCrocodileModel, ArmorLatexMaleSharkModel<LatexCrocodile>> {
    public LatexCrocodileRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexCrocodileModel(context.bakeLayer(LatexCrocodileModel.LAYER_LOCATION)),
                ArmorLatexMaleSharkModel::new, ArmorLatexMaleSharkModel.INNER_ARMOR, ArmorLatexMaleSharkModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forLargeSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexCrocodile p_114482_) {
        return Changed.modResource("textures/latex_crocodile.png");
    }

    @Override
    protected void scale(LatexCrocodile entity, PoseStack pose, float partialTick) {
        float modelScale = 1.025F;
        pose.scale(modelScale, modelScale, modelScale);
    }
}