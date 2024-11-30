package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.WhiteLatexWolfFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.WhiteWolfFemaleModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexFemaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.WhiteWolfFemale;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class WhiteWolfFemaleRenderer extends AdvancedHumanoidRenderer<WhiteWolfFemale, WhiteWolfFemaleModel, ArmorLatexFemaleWolfModel<WhiteWolfFemale>> {
    public WhiteWolfFemaleRenderer(EntityRendererProvider.Context context) {
        super(context, new WhiteWolfFemaleModel(context.bakeLayer(WhiteLatexWolfFemaleModel.LAYER_LOCATION)), ArmorLatexFemaleWolfModel.MODEL_SET, 0.5f);
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(WhiteWolfFemale p_114482_) {
        return Changed.modResource("textures/white_wolf_female.png");
    }
}