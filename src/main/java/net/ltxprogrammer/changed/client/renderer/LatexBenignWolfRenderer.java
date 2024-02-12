package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.GooParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.model.BenignGooWolfModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorMaleWolfModel;
import net.ltxprogrammer.changed.entity.beast.BenignGooWolf;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LatexBenignWolfRenderer extends AdvancedHumanoidRenderer<BenignGooWolf, BenignGooWolfModel, ArmorMaleWolfModel<BenignGooWolf>> {
    public LatexBenignWolfRenderer(EntityRendererProvider.Context context) {
        super(context, new BenignGooWolfModel(context.bakeLayer(BenignGooWolfModel.LAYER_LOCATION)),
                ArmorMaleWolfModel::new, ArmorMaleWolfModel.INNER_ARMOR, ArmorMaleWolfModel.OUTER_ARMOR, 0.5f);
        this.addLayer(new GooParticlesLayer<>(this, getModel()));
    }

    @Override
    public ResourceLocation getTextureLocation(BenignGooWolf p_114482_) {
        return Changed.modResource("textures/latex_benign_wolf.png");
    }
}