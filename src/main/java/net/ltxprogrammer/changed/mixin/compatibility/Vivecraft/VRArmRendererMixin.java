package net.ltxprogrammer.changed.mixin.compatibility.Vivecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.renderer.HandRenderer;
import net.ltxprogrammer.changed.client.renderer.StackAwareRenderer;
import net.ltxprogrammer.changed.client.renderer.WrappedPlayerRenderer;
import net.ltxprogrammer.changed.client.renderer.layers.FirstPersonLayer;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.vivecraft.client_vr.provider.ControllerType;
import org.vivecraft.client_vr.render.VRArmRenderer;

@Mixin(value = VRArmRenderer.class, remap = false)
@RequiredMods("vivecraft")
public abstract class VRArmRendererMixin extends PlayerRenderer {
    private VRArmRendererMixin(EntityRendererProvider.Context p_174557_, boolean p_174558_) {
        super(p_174557_, p_174558_);
    }

    @Unique
    private boolean changed$skipSleeve = false;
    @Unique
    private boolean changed$handRendered = false;
    @Unique
    private boolean changed$renderingHand = false;

    @WrapOperation(method = {"renderHand", "renderItem"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V"), require = 0)
    private void changed$renderHandOverride(ModelPart instance, PoseStack matrixStackIn, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha, Operation<Void> original,
                                    @Local(argsOnly = true) ControllerType side,
                                    @Local(argsOnly = true) MultiBufferSource bufferSource,
                                    @Local(argsOnly = true, ordinal = 1) ModelPart sleeve) {
        if (changed$renderingHand) {
            original.call(instance, matrixStackIn, buffer, light, overlay, red, green, blue, alpha);
            return;
        }

        if (changed$skipSleeve) {
            changed$skipSleeve = false;
            return;
        }

        Minecraft client = Minecraft.getInstance();
        EntityRenderer<? super AbstractClientPlayer> playerEntityRenderer = client.getEntityRenderDispatcher().getRenderer(client.player);
        if (playerEntityRenderer instanceof WrappedPlayerRenderer wrapper)
            playerEntityRenderer = wrapper.getWrapped(); // Unwrap

        if (playerEntityRenderer instanceof HandRenderer handRenderer) {
            if (handRenderer instanceof StackAwareRenderer stackAwareRenderer)
                stackAwareRenderer.setShadowedRenderer(this);

            changed$renderingHand = true;
            handRenderer.renderHand(matrixStackIn, bufferSource, light, client.player,
                    side == ControllerType.RIGHT ? HumanoidArm.RIGHT : HumanoidArm.LEFT, instance.storePose());
            changed$renderingHand = false;

            changed$skipSleeve = true;
            changed$handRendered = true;
        } else {
            original.call(instance, matrixStackIn, buffer, light, overlay, red, green, blue, alpha);
        }
    }

    @Inject(method = {"renderHand", "renderItem"}, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableBlend()V"), require = 0)
    private void changed$renderItemLayers(ControllerType side, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn,
                                   AbstractClientPlayer playerIn, ModelPart rendererArmIn, ModelPart rendererArmwearIn, CallbackInfo callback) {
        if (changed$handRendered) {
            changed$handRendered = false;
            return;
        }

        for (var layer : layers) {
            if (layer instanceof FirstPersonLayer firstPersonLayer)
                firstPersonLayer.renderFirstPersonOnArms(
                        matrixStackIn, bufferIn, combinedLightIn, playerIn, getModel().rightArm != rendererArmIn ? HumanoidArm.LEFT : HumanoidArm.RIGHT,
                        rendererArmIn.storePose(), Minecraft.getInstance().getPartialTick());
        }
    }
}
