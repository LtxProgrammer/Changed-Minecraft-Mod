package net.ltxprogrammer.changed.mixin.compatibility.Vivecraft;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.renderer.AdvancedHumanoidRenderer;
import net.ltxprogrammer.changed.client.renderer.model.AdvancedHumanoidModel;
import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.vivecraft.api.client.data.RenderPass;
import org.vivecraft.client.ClientVRPlayers;
import org.vivecraft.client_vr.ClientDataHolderVR;
import org.vivecraft.client_vr.render.helpers.VREffectsHelper;

@Mixin(value = AdvancedHumanoidRenderer.class, remap = false)
@RequiredMods("vivecraft")
public abstract class AdvancedHumanoidRendererMixin<T extends ChangedEntity, M extends AdvancedHumanoidModel<T>> extends MobRenderer<T, M> {
    @Shadow public abstract AdvancedHumanoidModel<T> getModel(ChangedEntity entity);

    public AdvancedHumanoidRendererMixin(EntityRendererProvider.Context p_174304_, M p_174305_, float p_174306_) {
        super(p_174304_, p_174305_, p_174306_);
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
                ClientDataHolderVR.getInstance().currentPass != RenderPass.GUI)
            poseStack.translate(0, 0, -getModel(entity).getAnimator(entity).forwardOffset / 16.0D);
    }

    @Override
    public Vec3 getRenderOffset(T entity, float partialTick) {
        // TODO adjust render offset to line up with GUI
        // TODO pup forward render offset to 4px behind head

        if (entity.getUnderlyingPlayer() instanceof AbstractClientPlayer player && ClientVRPlayers.getInstance().isVRPlayer(player)) {
            if (VREffectsHelper.isFirstPersonPlayer(player)) {
                return player.isVisuallySwimming() ? new Vec3(0.0F, -0.125F * ClientDataHolderVR.getInstance().vrPlayer.worldScale, 0.0F) : Vec3.ZERO;
            } else {
                return player.isVisuallySwimming() ? new Vec3(0.0F, -0.125F, 0.0F) : Vec3.ZERO;
            }
        }

        return super.getRenderOffset(entity, partialTick);
    }
}