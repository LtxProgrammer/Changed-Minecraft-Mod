package net.ltxprogrammer.changed.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.ltxprogrammer.changed.client.FormRenderHandler;
import net.ltxprogrammer.changed.util.PatreonBenefits;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
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

    @Inject(method = "renderHand", at = @At("HEAD"), cancellable = true)
    private void renderHand(PoseStack stack, MultiBufferSource buffer, int light, AbstractClientPlayer player, ModelPart arm, ModelPart armwear, CallbackInfo ci) {
        if (FormRenderHandler.renderHand(((PlayerRenderer)(Object)this), stack, buffer, light, player, arm, armwear)) {
            ci.cancel(); //cancel the call
        }
    }
    @Inject(method = "renderNameTag*", at = @At("HEAD"))
    private void renderNameTag(AbstractClientPlayer player, Component name, PoseStack pose, MultiBufferSource source, int p_114502_, CallbackInfo ci) {
        if (Minecraft.getInstance().player == player)
            return;

        double d0 = this.entityRenderDispatcher.distanceToSqr(player);
        pose.pushPose();
        if (d0 < 100.0D) {
            Scoreboard scoreboard = player.getScoreboard();
            Objective objective = scoreboard.getDisplayObjective(2);
            if (objective != null) {
                Score score = scoreboard.getOrCreatePlayerScore(player.getScoreboardName(), objective);
                super.renderNameTag(player, (new TextComponent(Integer.toString(score.getScore()))).append(" ").append(objective.getDisplayName()), pose, source, p_114502_);
                pose.translate(0.0D, (double)(9.0F * 1.15F * 0.025F), 0.0D);
            }
        }

        super.renderNameTag(player, PatreonBenefits.getPlayerName(player), pose, source, p_114502_);

        /*float f = player.getBbHeight() + 0.5F;
        pose.translate(0.0D, (double)f, 0.0D);
        pose.mulPose(this.entityRenderDispatcher.cameraOrientation());
        pose.scale(-0.025F, -0.025F, 0.025F);
        Matrix4f matrix4f = pose.last().pose();
        float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
        int j = (int)(f1 * 255.0F) << 24;
        float f2 = (float)(-font.width(p_114499_) / 2);

        switch (PatreonBenefits.getPlayerTier(player)) {
            case LEVEL1 -> {
                Font font = this.getFont();
                font.drawInBatch("Tier 1 Supporter", )
            }
        }*/

        pose.popPose();
    }
}
