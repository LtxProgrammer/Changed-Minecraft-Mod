package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.LatexPurpleFoxModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.GooPurpleFox;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexPurpleFoxRenderer extends AdvancedHumanoidRenderer<GooPurpleFox, LatexPurpleFoxModel, ArmorMaleWolfModel<GooPurpleFox>> {
    public LatexPurpleFoxRenderer(EntityRendererProvider.Context context) {
        super(context, new LatexPurpleFoxModel(context.bakeLayer(LatexPurpleFoxModel.LAYER_LOCATION)),
                ArmorMaleWolfModel::new, ArmorMaleWolfModel.INNER_ARMOR, ArmorMaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new GooParticlesLayer<>(this, getModel()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet(), CustomEyesLayer.fixedColor(Color3.WHITE),CustomEyesLayer.fixedColor(Color3.parseHex("#43b44e"))));
    }

    @Override
    public ResourceLocation getTextureLocation(GooPurpleFox p_114482_) {
        return Changed.modResource("textures/latex_purple_fox.png");
    }
}