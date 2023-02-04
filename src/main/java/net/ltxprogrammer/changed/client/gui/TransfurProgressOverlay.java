package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class TransfurProgressOverlay {
    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void eventHandler(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            Player player = Minecraft.getInstance().player;
            if (player == null || ProcessTransfur.isPlayerLatex(player))
                return;
            var progress = ProcessTransfur.getPlayerTransfurProgress(player);
            if (progress.ticks() <= 0)
                return;

            float tick_progress = (float)progress.ticks() / (float)ProcessTransfur.TRANSFUR_PROGRESSION_TAKEOVER;
            int vertSize = (int) (tick_progress * 26);

            int w = event.getWindow().getGuiScaledWidth();
            int h = event.getWindow().getGuiScaledHeight();

            int posX = w / 2;
            int posY = h / 2;

            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            RenderSystem.setShaderColor(1, 1, 1, 1);

            RenderSystem.setShaderTexture(0, Changed.modResource("textures/gui/transfur_overlay_bottom.png"));
            Minecraft.getInstance().gui.blit(event.getMatrixStack(), posX + 96, posY + 92, 0, 0, 8, 26, 8, 26);

            RenderSystem.setShaderTexture(0, Changed.modResource("textures/gui/transfur_overlay_top.png"));
            Minecraft.getInstance().gui.blit(event.getMatrixStack(), posX + 96, posY + 118 - vertSize, 0, 26 - vertSize, 8, vertSize, 8, 26);

            RenderSystem.depthMask(true);
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
    }

}
