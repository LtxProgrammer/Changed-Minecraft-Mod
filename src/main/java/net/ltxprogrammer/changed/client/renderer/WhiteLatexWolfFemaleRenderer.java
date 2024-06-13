package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.WhiteLatexWolfFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.WhiteLatexWolfFemale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class WhiteLatexWolfFemaleRenderer extends AdvancedHumanoidRenderer<WhiteLatexWolfFemale, WhiteLatexWolfFemaleModel, ArmorLatexFemaleWolfModel<WhiteLatexWolfFemale>> {
    public WhiteLatexWolfFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new WhiteLatexWolfFemaleModel(context.bakeLayer(WhiteLatexWolfFemaleModel.LAYER_LOCATION)),
                ArmorLatexFemaleWolfModel::new, ArmorLatexFemaleWolfModel.INNER_ARMOR, ArmorLatexFemaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(WhiteLatexWolfFemale p_114482_) {
        return Changed.modResource("textures/white_latex_wolf_female.png");
    }
}