package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.LatexParticlesLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TaurChestPackLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurCapeLayer;
import net.ltxprogrammer.changed.client.renderer.model.HeadlessKnightModel;
import net.ltxprogrammer.changed.client.renderer.model.armor.ArmorLatexCentaurLowerModel;
import net.ltxprogrammer.changed.entity.beast.HeadlessKnight;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.resources.ResourceLocation;

public class HeadlessKnightRenderer extends AdvancedHumanoidRenderer<HeadlessKnight, HeadlessKnightModel, ArmorLatexCentaurLowerModel<HeadlessKnight>> {
    public HeadlessKnightRenderer(EntityRendererProvider.Context context) {
        super(context, new HeadlessKnightModel(context.bakeLayer(HeadlessKnightModel.LAYER_LOCATION)),
                ArmorLatexCentaurLowerModel::new, ArmorLatexCentaurLowerModel.INNER_ARMOR, ArmorLatexCentaurLowerModel.OUTER_ARMOR, 0.7f);
        this.addLayer(new LatexParticlesLayer<>(this, getModel()));
        this.addLayer(TransfurCapeLayer.normalCape(this, context.getModelSet()));
        this.addLayer(new SaddleLayer<>(this, getModel(), Changed.modResource("textures/light_latex_centaur_saddle.png")));
        this.addLayer(new TaurChestPackLayer<>(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(HeadlessKnight p_114482_) {
        return Changed.modResource("textures/headless_knight.png");
    }

    @Override
    protected void scale(HeadlessKnight entity, PoseStack pose, float partialTick) {
        super.scale(entity, pose, partialTick);
        pose.scale(1.05f, 1.05f, 1.05f);
    }
}