package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/// Wraps a render override when being returned for a player
public class WrappedPlayerRenderer extends PlayerRenderer {
    protected final EntityRenderer<? super AbstractClientPlayer> wrapped;

    public WrappedPlayerRenderer(EntityRendererProvider.Context context, EntityRenderer<? super AbstractClientPlayer> wrapped) {
        super(context, false);
        this.wrapped = wrapped;
    }

    @Override
    public void render(AbstractClientPlayer player, float yRot, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        wrapped.render(player, yRot, partialTicks, poseStack, bufferSource, packedLight);
        shadowRadius = wrapped.shadowRadius;
        shadowStrength = wrapped.shadowStrength;
    }

    @Override
    public @NotNull Vec3 getRenderOffset(AbstractClientPlayer player, float partialTicks) {
        return wrapped.getRenderOffset(player, partialTicks);
    }

    @Override
    public void renderRightHand(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, AbstractClientPlayer player) {}

    @Override
    public void renderLeftHand(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, AbstractClientPlayer player) {}
}
