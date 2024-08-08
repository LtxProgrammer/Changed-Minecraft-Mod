package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexMantaRayFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorFemaleMantaRayAbdomenModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorFemaleMantaRayUpperBodyModel;
import net.ltxprogrammer.changed.entity.beast.LatexMantaRayFemale;
import net.ltxprogrammer.changed.item.AbdomenArmor;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexMantaRayFemaleRenderer extends AdvancedHumanoidRenderer<LatexMantaRayFemale, LatexMantaRayFemaleModel, ArmorFemaleMantaRayUpperBodyModel<LatexMantaRayFemale>> {
    public LatexMantaRayFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexMantaRayFemaleModel(context.bakeLayer(LatexMantaRayFemaleModel.LAYER_LOCATION)),
                ArmorFemaleMantaRayUpperBodyModel::new, ArmorFemaleMantaRayUpperBodyModel.INNER_ARMOR, ArmorFemaleMantaRayUpperBodyModel.OUTER_ARMOR,
                ArmorFemaleMantaRayAbdomenModel::new, ArmorFemaleMantaRayAbdomenModel.INNER_ARMOR, ArmorFemaleMantaRayAbdomenModel.OUTER_ARMOR,
                AbdomenArmor::useAbdomenModel, AbdomenArmor::useInnerAbdomenModel, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, this.model));
        this.addLayer(CustomEyesLayer.builder(this, context.getModelSet())
                .withEyelashes(Color3.fromInt(0x1a1a1b)).build());
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forLargeSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexMantaRayFemale p_114482_) {
        return Changed.modResource("textures/latex_manta_ray_female.png");
    }
}
