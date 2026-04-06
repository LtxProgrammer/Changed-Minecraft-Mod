package net.ltxprogrammer.changed.mixin.compatibility.SG2;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ltxprogrammer.changed.client.FormRenderHandler;
import net.ltxprogrammer.changed.extension.RequiredMods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import top.ribs.scguns.client.render.gun.animated.AnimatedGunRenderer;

@Mixin(value = AnimatedGunRenderer.class, remap = false)
@RequiredMods("scguns")
public abstract class AnimatedGunRendererMixin {
    @Shadow private MultiBufferSource bufferSource;

    @Unique
    private boolean changed$skipSleeve = false;

    @WrapOperation(method = { "renderRightArm", "renderLeftArm" },
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/geom/ModelPart;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V", remap = true))
    public void changed$renderPlayerArm(ModelPart instance, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, Operation<Void> original,
                                               @Local(argsOnly = true) PlayerModel<AbstractClientPlayer> playerModel) {
        if ((playerModel.rightSleeve == instance ||
                playerModel.leftSleeve == instance) && changed$skipSleeve) {
            changed$skipSleeve = false;
            return;
        }

        if (playerModel.rightArm == instance ||
                playerModel.leftArm == instance) {
            Minecraft client = Minecraft.getInstance();
            PlayerRenderer playerEntityRenderer = (PlayerRenderer)client.getEntityRenderDispatcher().getRenderer(client.player);
            if (FormRenderHandler.maybeRenderHand(playerEntityRenderer, poseStack, this.bufferSource, packedLight, client.player, instance,
                    playerModel.rightArm == instance ? playerModel.rightSleeve : playerModel.leftSleeve)) {
                changed$skipSleeve = true;
                return;
            }
        }

        original.call(instance, poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
