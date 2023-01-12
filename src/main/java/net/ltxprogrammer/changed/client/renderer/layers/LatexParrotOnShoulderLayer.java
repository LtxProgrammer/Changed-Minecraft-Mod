package net.ltxprogrammer.changed.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.renderer.model.LatexHumanoidModel;
import net.ltxprogrammer.changed.entity.LatexEntity;
import net.minecraft.client.model.ParrotModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;

public class LatexParrotOnShoulderLayer<T extends LatexEntity, M extends LatexHumanoidModel<T>> extends RenderLayer<T, M> {
    private final ParrotModel model;

    public LatexParrotOnShoulderLayer(RenderLayerParent<T, M> p_174511_, EntityModelSet p_174512_) {
        super(p_174511_);
        this.model = new ParrotModel(p_174512_.bakeLayer(ModelLayers.PARROT));
    }

    public void render(PoseStack p_117307_, MultiBufferSource p_117308_, int p_117309_, T p_117310_, float p_117311_, float p_117312_, float p_117313_, float p_117314_, float p_117315_, float p_117316_) {
        this.render(p_117307_, p_117308_, p_117309_, p_117310_, p_117311_, p_117312_, p_117315_, p_117316_, true);
        this.render(p_117307_, p_117308_, p_117309_, p_117310_, p_117311_, p_117312_, p_117315_, p_117316_, false);
    }

    private void render(PoseStack p_117318_, MultiBufferSource p_117319_, int p_117320_, T p_117321_, float p_117322_, float p_117323_, float p_117324_, float p_117325_, boolean p_117326_) {
        if (p_117321_.getUnderlyingPlayer() instanceof AbstractClientPlayer player) {
            CompoundTag compoundtag = p_117326_ ? player.getShoulderEntityLeft() : player.getShoulderEntityRight();
            EntityType.byString(compoundtag.getString("id")).filter((p_117294_) -> {
                return p_117294_ == EntityType.PARROT;
            }).ifPresent((p_117338_) -> {
                p_117318_.pushPose();
                p_117318_.translate(p_117326_ ? (double) 0.4F : (double) -0.4F, player.isCrouching() ? (double) -1.3F : -1.5D, 0.0D);
                VertexConsumer vertexconsumer = p_117319_.getBuffer(this.model.renderType(ParrotRenderer.PARROT_LOCATIONS[compoundtag.getInt("Variant")]));
                this.model.renderOnShoulder(p_117318_, vertexconsumer, p_117320_, OverlayTexture.NO_OVERLAY, p_117322_, p_117323_, p_117324_, p_117325_, player.tickCount);
                p_117318_.popPose();
            });
        }
    }
}