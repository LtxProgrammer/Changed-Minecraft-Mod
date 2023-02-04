package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.ltxprogrammer.changed.entity.LatexType;
import net.ltxprogrammer.changed.entity.beast.DarkLatexEntity;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class VariantBlindnessOverlay {
    private static final ResourceLocation TEXTURE = new ResourceLocation("textures/misc/white.png");
    private static final float ALPHA = 0.65F;
    private static float alphaO = 0.0F;

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void eventHandler(RenderGameOverlayEvent.Pre event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            Player player = Minecraft.getInstance().player;
            if (player == null || !ProcessTransfur.isPlayerLatex(player))
                return;
            var variant = ProcessTransfur.getPlayerLatexVariant(player);
            if (variant == null || variant.getLatexType() == LatexType.NEUTRAL)
                return;
            if (variant.getLatexEntity() instanceof DarkLatexEntity darkLatex && darkLatex.isMaskless())
                return;
            float color = variant.getLatexType() == LatexType.DARK_LATEX ? 0.0F : 1.0F;
            float darkness = (15 - player.level.getRawBrightness(player.eyeBlockPosition(), 0)) / 15.0f;
            float alpha;
            if (variant.getLatexType() == LatexType.DARK_LATEX)
                alpha = Mth.lerp(0.65F, alphaO, darkness * ALPHA);
            else
                alpha = ALPHA;
            alphaO = alpha;

            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            int i1 = event.getWindow().getScreenWidth();
            int j1 = event.getWindow().getScreenHeight();
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
            bufferbuilder.vertex(0.0D, (double)j1, -90).uv(0.0F, 1.0F).color(color, color, color, alpha).endVertex();
            bufferbuilder.vertex((double)i1, (double)j1, -90).uv(1.0F, 1.0F).color(color, color, color, alpha).endVertex();
            bufferbuilder.vertex((double)i1, 0.0D, -90).uv(1.0F, 0.0F).color(color, color, color, alpha).endVertex();
            bufferbuilder.vertex(0.0D, 0.0D, -90).uv(0.0F, 0.0F).color(color, color, color, alpha).endVertex();
            tesselator.end();
            RenderSystem.depthMask(true);
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
    }
}
