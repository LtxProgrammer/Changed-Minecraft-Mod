package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.WhiteLatexWolfMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.WhiteLatexWolfMale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class WhiteLatexWolfMaleRenderer extends AdvancedHumanoidRenderer<WhiteLatexWolfMale, WhiteLatexWolfMaleModel, ArmorLatexMaleWolfModel<WhiteLatexWolfMale>> {
    public WhiteLatexWolfMaleRenderer(EntityRendererProvider.Context context) {
        super(context, new WhiteLatexWolfMaleModel(context.bakeLayer(WhiteLatexWolfMaleModel.LAYER_LOCATION)),
                ArmorLatexMaleWolfModel::new, ArmorLatexMaleWolfModel.INNER_ARMOR, ArmorLatexMaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(WhiteLatexWolfMale p_114482_) {
        return Changed.modResource("textures/white_latex_wolf_male.png");
    }
}