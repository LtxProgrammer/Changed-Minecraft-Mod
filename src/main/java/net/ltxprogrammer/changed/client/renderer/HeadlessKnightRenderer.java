package net.ltxprogrammer.changed.client.renderer;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.CentaurChestPackLayer;
import net.ltxprogrammer.changed.client.renderer.model.HeadlessKnightModel;
import net.ltxprogrammer.changed.client.renderer.model.LightLatexCentaurModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorHeadlessKnightModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLightLatexCentaurModel;
import net.ltxprogrammer.changed.entity.beast.HeadlessKnight;
import net.ltxprogrammer.changed.entity.beast.LightLatexCentaur;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;

public class HeadlessKnightRenderer extends LatexHumanoidRenderer<HeadlessKnight, HeadlessKnightModel, ArmorHeadlessKnightModel> {
    public HeadlessKnightRenderer(EntityRendererProvider.Context context) {
        super(context, new HeadlessKnightModel(context.bakeLayer(HeadlessKnightModel.LAYER_LOCATION)),
                ArmorHeadlessKnightModel::new, ArmorHeadlessKnightModel.INNER_ARMOR, ArmorHeadlessKnightModel.OUTER_ARMOR, 0.7f);
        this.addLayer(new SaddleLayer<>(this, getModel(), Changed.modResource("textures/light_latex_centaur_saddle.png")));
    }

    @Override
    public ResourceLocation getTextureLocation(HeadlessKnight p_114482_) {
        return Changed.modResource("textures/light_latex_centaur.png");
    }
}