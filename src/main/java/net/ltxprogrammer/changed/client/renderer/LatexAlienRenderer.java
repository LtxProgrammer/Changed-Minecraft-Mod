package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.EmissiveBodyLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexAlienModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexAlienModel;
import net.ltxprogrammer.changed.entity.beast.LatexAlien;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexAlienRenderer extends LatexHumanoidRenderer<LatexAlien, LatexAlienModel, ArmorLatexAlienModel<LatexAlien>> {
    public LatexAlienRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexAlienModel(context.bakeLayer(LatexAlienModel.LAYER_LOCATION)),
                ArmorLatexAlienModel::new, ArmorLatexAlienModel.INNER_ARMOR, ArmorLatexAlienModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new EmissiveBodyLayer<>(this, Changed.modResource("textures/latex_alien_emissive.png")));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexAlien p_114482_) {
        return Changed.modResource("textures/latex_alien.png");
    }
}