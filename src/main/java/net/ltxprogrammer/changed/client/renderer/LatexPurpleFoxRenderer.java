package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexPurpleFoxModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.LatexPurpleFox;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexPurpleFoxRenderer extends LatexHumanoidRenderer<LatexPurpleFox, LatexPurpleFoxModel, ArmorLatexMaleWolfModel<LatexPurpleFox>> {
    public LatexPurpleFoxRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexPurpleFoxModel(context.bakeLayer(LatexPurpleFoxModel.LAYER_LOCATION)),
                ArmorLatexMaleWolfModel::new, ArmorLatexMaleWolfModel.INNER_ARMOR, ArmorLatexMaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer.fixedColor(Color3.WHITE),CustomEyesLayer.fixedColor(Color3.parseHex("#43b44e"))));
    }

    @Override
    public ResourceLocation getTextureLocation(LatexPurpleFox p_114482_) {
        return Changed.modResource("textures/latex_purple_fox.png");
    }
}