package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CustomEyesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.GasMaskLayer;
import net.ltxprogrammer.changed.client.renderer.model.WhiteGooWolfMaleModel;
import net.ltxprogrammer.changed.client.renderer.model.LightLatexWolfOrganicModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.WhiteWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LightLatexWolfOrganicRenderer extends AdvancedHumanoidRenderer<WhiteWolf, LightLatexWolfOrganicModel, ArmorMaleWolfModel<WhiteWolf>> {
    public LightLatexWolfOrganicRenderer(EntityRendererProvider.Context context) {
        super(context, new LightLatexWolfOrganicModel(context.bakeLayer(WhiteGooWolfMaleModel.LAYER_LOCATION)),
                ArmorMaleWolfModel::new, ArmorMaleWolfModel.INNER_ARMOR, ArmorMaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new CustomEyesLayer<>(this, context.getModelSet()));
        this.addLayer(GasMaskLayer.forSnouted(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(WhiteWolf p_114482_) {
        return Changed.modResource("textures/white_wolf.png");
    }
}