package net.ltxprogrammer.changed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/// Wraps a render override when being returned for a player
public class WrappedPlayerRenderer extends PlayerRenderer {
    protected final EntityRenderer<? super AbstractClientPlayer> wrapped;

    public WrappedPlayerRenderer(EntityRendererProvider.Context context, EntityRenderer<? super AbstractClientPlayer> wrapped) {
        super(context, false);
        this.wrapped = wrapped;
    }

    public EntityRenderer<? super AbstractClientPlayer> getWrapped() {
        return wrapped;
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

    protected PartPose getArmPose(AbstractClientPlayer player, HumanoidArm hand) {
        PlayerModel<AbstractClientPlayer> playermodel = this.getModel();
        this.setModelProperties(player);
        playermodel.attackTime = 0.0F;
        playermodel.crouching = false;
        playermodel.swimAmount = 0.0F;
        playermodel.setupAnim(player, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);

        return (hand == HumanoidArm.LEFT ? playermodel.leftArm : playermodel.rightArm).storePose();
    }

    @Override
    public void renderRightHand(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, AbstractClientPlayer player) {
        if (!net.minecraftforge.client.ForgeHooksClient.renderSpecificFirstPersonArm(poseStack, bufferSource, packedLight, player, HumanoidArm.RIGHT) && wrapped instanceof HandRenderer handRenderer) {
            handRenderer.renderHand(poseStack, bufferSource, packedLight, player, HumanoidArm.RIGHT, getArmPose(player, HumanoidArm.RIGHT));
        }
    }

    @Override
    public void renderLeftHand(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, AbstractClientPlayer player) {
        if (!net.minecraftforge.client.ForgeHooksClient.renderSpecificFirstPersonArm(poseStack, bufferSource, packedLight, player, HumanoidArm.LEFT) && wrapped instanceof HandRenderer handRenderer) {
            handRenderer.renderHand(poseStack, bufferSource, packedLight, player, HumanoidArm.LEFT, getArmPose(player, HumanoidArm.LEFT));
        }
    }
}
