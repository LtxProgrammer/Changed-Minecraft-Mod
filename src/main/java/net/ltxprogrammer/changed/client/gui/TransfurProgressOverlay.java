package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.layers.TransfurProgressLayer;
import net.ltxprogrammer.changed.init.ChangedParticles;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
public class TransfurProgressOverlay {
    private static final ResourceLocation GOO_OUTLINE = Changed.modResource("textures/misc/goo_outline.png");

    public static void renderTextureOverlay(Gui gui, ResourceLocation texture, float zoom, ChangedParticles.Color3 color, float alpha) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(color.red(), color.green(), color.blue(), alpha);
        RenderSystem.setShaderTexture(0, texture);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        float aspect = (float)gui.screenWidth / (float)gui.screenHeight;
        bufferbuilder.vertex(-zoom * aspect, (double)gui.screenHeight + zoom, -90.0D).uv(0.0F, 1.0F).endVertex();
        bufferbuilder.vertex((double)gui.screenWidth + (zoom * aspect), (double)gui.screenHeight + zoom, -90.0D).uv(1.0F, 1.0F).endVertex();
        bufferbuilder.vertex((double)gui.screenWidth + (zoom * aspect), -zoom, -90.0D).uv(1.0F, 0.0F).endVertex();
        bufferbuilder.vertex(-zoom * aspect, -zoom, -90.0D).uv(0.0F, 0.0F).endVertex();
        tesselator.end();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public static void renderGooOverlay(Gui gui) {
        Player player = Minecraft.getInstance().player;
        if (player == null || ProcessTransfur.isPlayerLatex(player))
            return;
        var progress = ProcessTransfur.getPlayerTransfurProgress(player);
        if (progress.ticks() <= 0)
            return;

        float tickProgress = (float)progress.ticks() / (float)ProcessTransfur.TRANSFUR_PROGRESSION_TAKEOVER;
        float distance = (1.0f - tickProgress) * 20.0f;
        var color = TransfurProgressLayer.getProgressColor(progress.type());

        renderTextureOverlay(gui, GOO_OUTLINE, distance, color, tickProgress);
    }
}
