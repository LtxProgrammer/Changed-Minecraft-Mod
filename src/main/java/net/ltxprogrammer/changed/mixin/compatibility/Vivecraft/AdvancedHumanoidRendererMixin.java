package net.ltxprogrammer.changed.mixin.compatibility.Vivecraft;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.ltxprogrammer.changed.extension.vivecraft.RendererScaleAccessor;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.vivecraft.api.client.data.RenderPass;
import org.vivecraft.client.ClientVRPlayers;
import org.vivecraft.client.utils.ScaleHelper;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.VRState;
import org.vivecraft.client_vr.render.helpers.VREffectsHelper;

@Mixin(value = AdvancedHumanoidRenderer.class, remap = false)
@RequiredMods("vivecraft")
public abstract class AdvancedHumanoidRendererMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends MobRenderer<T, M> implements RendererScaleAccessor<T> {
    @Shadow public abstract AdvancedHumanoidModel<T> getModel(ChangedEntity entity);

    @Shadow protected abstract void scale(T entity, PoseStack poseStack, float partialTicks);

    public AdvancedHumanoidRendererMixin(EntityRendererProvider.Context p_174304_, M p_174305_, float p_174306_) {
        super(p_174304_, p_174305_, p_174306_);
    }

    @Override
    public void vivecraft$scale(T entity, PoseStack poseStack, float partialTick) {
        this.scale(entity, poseStack, partialTick);
    }

    @WrapMethod(method = "render(Lnet/ltxprogrammer/changed/entity/ChangedEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V")
    private void vivecraft$setupScale(T entity, float yRot, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, Operation<Void> original) {
        if (entity.getUnderlyingPlayer() instanceof AbstractClientPlayer player && ClientVRPlayers.getInstance().isVRPlayer(player)) {
            poseStack.pushPose();
            ClientVRPlayers.RotInfo rotInfo = ClientVRPlayers.getInstance().getRotationsForPlayer(player.getUUID());
            if (rotInfo != null) {
                float scale = rotInfo.heightScale;
                if (VRState.VR_RUNNING && player == Minecraft.getInstance().player || ClientDataHolderVR.getInstance().vrSettings.applyPlayerWorldscale) {
                    scale *= rotInfo.worldScale / ScaleHelper.getEntityEyeHeightScale(player, partialTicks);
                }

                if (player.isAutoSpinAttack()) {
                    float offset = player.getViewXRot(partialTicks) / 90.0F * 0.2F;
                    poseStack.translate(0.0F, rotInfo.headPos.y() + offset, 0.0F);
                }

                poseStack.scale(scale, scale, scale);
            }

            original.call(entity, yRot, partialTicks, poseStack, bufferSource, packedLight);

            poseStack.popPose();
        } else {
            original.call(entity, yRot, partialTicks, poseStack, bufferSource, packedLight);
        }
    }

    // Copied From VRPlayerRenderer$setupRotations
    @WrapMethod(method = "setupRotations(Lnet/ltxprogrammer/changed/entity/ChangedEntity;Lcom/mojang/blaze3d/vertex/PoseStack;FFF)V")
    private void vivecraft$setupRotations(@NotNull T entity, PoseStack poseStack, float bob, float rotationYaw, float partialTicks, Operation<Void> original) {
        if (entity.getUnderlyingPlayer() instanceof AbstractClientPlayer player) {
            if (ClientDataHolderVR.getInstance().currentPass != RenderPass.GUI && ClientVRPlayers.getInstance().isVRPlayer(player)) {
                if (player == Minecraft.getInstance().player) {
                    rotationYaw = ClientDataHolderVR.getInstance().vrPlayer.getVRDataWorld().getBodyYaw();
                } else {
                    ClientVRPlayers.RotInfo rotInfo = ClientVRPlayers.getInstance().getRotationsForPlayer(player.getUUID());
                    rotationYaw = (180F / (float)Math.PI) * rotInfo.getBodyYawRad();
                }
            }

        }

        original.call(entity, poseStack, bob, rotationYaw, partialTicks);

        if (entity.getUnderlyingPlayer() instanceof AbstractClientPlayer player &&
                ClientVRPlayers.getInstance().isVRPlayer(player) &&
                VREffectsHelper.isFirstPersonPlayer(player) &&
                ClientDataHolderVR.getInstance().currentPass != RenderPass.GUI) {
            poseStack.translate(0, 0,
                    ProcessTransfur.getPlayerTransfurVariant(player).getParent().cameraZOffset);
        }
    }

    // Copied From VRPlayerRenderer$getRenderOffset
    @WrapMethod(method = "getRenderOffset(Lnet/ltxprogrammer/changed/entity/ChangedEntity;F)Lnet/minecraft/world/phys/Vec3;")
    public Vec3 vivecraft$getRenderOffset(T entity, float partialTick, Operation<Vec3> original) {
        // TODO adjust render offset to line up with GUI

        if (entity.getUnderlyingPlayer() instanceof AbstractClientPlayer player && ClientVRPlayers.getInstance().isVRPlayer(player)) {
            if (VREffectsHelper.isFirstPersonPlayer(player)) {
                return player.isVisuallySwimming() ? new Vec3(0.0F, -0.125F * ClientDataHolderVR.getInstance().vrPlayer.worldScale, 0.0F) : Vec3.ZERO;
            } else {
                return player.isVisuallySwimming() ? new Vec3(0.0F, -0.125F, 0.0F) : Vec3.ZERO;
            }
        }

        return original.call(entity, partialTick);
    }
}