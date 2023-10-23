package net.ltxprogrammer.changed.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.FormRenderHandler;
import net.ltxprogrammer.changed.client.renderer.layers.DarkLatexMaskLayer;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurProgressLayer;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public PlayerRendererMixin(EntityRendererProvider.Context p_174289_, PlayerModel<AbstractClientPlayer> p_174290_, float p_174291_) {
        super(p_174289_, p_174290_, p_174291_);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void PlayerRenderer(EntityRendererProvider.Context context, boolean slim, CallbackInfo callback) {
        this.addLayer(new TransfurProgressLayer(this, context.getModelSet(), slim));
        this.addLayer(new DarkLatexMaskLayer<>(this, context.getModelSet()));
    }

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void renderHand(PoseStack stack, MultiBufferSource buffer, int light, AbstractClientPlayer player, ModelPart arm, ModelPart armwear, CallbackInfo ci) {
        if (FormRenderHandler.renderHand(((PlayerRenderer)(Object)this), stack, buffer, light, player, arm, armwear)) {
            ci.cancel(); //cancel the call
        }
    }

    @Inject(method = "renderHand", at = @At("RETURN"))
    private void renderHandEnd(PoseStack stack, MultiBufferSource buffer, int light, AbstractClientPlayer player, ModelPart arm, ModelPart armwear, CallbackInfo ci) {
        if (!ProcessTransfur.isPlayerLatex(player)) {
            var progress = ProcessTransfur.getPlayerTransfurProgress(player);
            if (progress == null || progress.progress() <= 0)
                return;
            var color = TransfurProgressLayer.getProgressColor(progress.variant());

            arm.xRot = 0.0F;
            arm.render(stack, buffer.getBuffer(RenderType.entityCutoutNoCull(TransfurProgressLayer.getProgressTexture(progress.progress()))), light, OverlayTexture.NO_OVERLAY, color.red(), color.green(), color.blue(), 1.0F);
            armwear.xRot = 0.0F;
            armwear.render(stack, buffer.getBuffer(RenderType.entityTranslucent(TransfurProgressLayer.getProgressTexture(progress.progress()))), light, OverlayTexture.NO_OVERLAY, color.red(), color.green(), color.blue(), 1.0F);
        }
    }
}
