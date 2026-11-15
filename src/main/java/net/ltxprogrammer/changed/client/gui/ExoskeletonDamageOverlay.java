package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.entity.robot.Exoskeleton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;

public class ExoskeletonDamageOverlay {
    private static final ResourceLocation TEXTURE = Changed.modResource("textures/misc/exoskeleton_damage_outline.png");

    public static void eventHandler(RenderGuiOverlayEvent.Pre event) {
        if (/*event.getType() == RenderGuiOverlayEvent.ElementType.ALL &&*/ Minecraft.getInstance().getCameraEntity() instanceof LivingEntity wearer) {
            var exoOpt = Exoskeleton.getEntityExoskeleton(wearer);
            if (exoOpt.isEmpty())
                return;

            float width = Mth.clamp((float)((wearer.hurtTime - 1) + (1f - event.getPartialTick())) / (float)wearer.hurtDuration, 0f, 1f);
            if (width <= 0f)
                return;

            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1, 1, 1, 1);
            int screenWidth = event.getWindow().getGuiScaledWidth();
            int screenHeight = event.getWindow().getGuiScaledHeight();
            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder bufferbuilder = tesselator.getBuilder();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

            float uvXLeft = width * 0.5f;
            float uvXRight = 1f - (width * 0.5f);

            bufferbuilder.vertex(0.0D, (double)screenHeight, -90).uv(0.0F, 1.0F).color(1f, 1f, 1f, 1f).endVertex();
            bufferbuilder.vertex((double)screenWidth * uvXLeft, (double)screenHeight, -90).uv(uvXLeft, 1.0F).color(1f, 1f, 1f, 1f).endVertex();
            bufferbuilder.vertex((double)screenWidth * uvXLeft, 0.0D, -90).uv(uvXLeft, 0.0F).color(1f, 1f, 1f, 1f).endVertex();
            bufferbuilder.vertex(0.0D, 0.0D, -90).uv(0.0F, 0.0F).color(1f, 1f, 1f, 1f).endVertex();

            bufferbuilder.vertex((double)screenWidth * uvXRight, (double)screenHeight, -90).uv(uvXRight, 1.0F).color(1f, 1f, 1f, 1f).endVertex();
            bufferbuilder.vertex((double)screenWidth, (double)screenHeight, -90).uv(1.0F, 1.0F).color(1f, 1f, 1f, 1f).endVertex();
            bufferbuilder.vertex((double)screenWidth, 0.0D, -90).uv(1.0F, 0.0F).color(1f, 1f, 1f, 1f).endVertex();
            bufferbuilder.vertex((double)screenWidth * uvXRight, 0.0D, -90).uv(uvXRight, 0.0F).color(1f, 1f, 1f, 1f).endVertex();

            tesselator.end();
            RenderSystem.depthMask(true);
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
    }
}
