package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.ltxprogrammer.changed.entity.VisionType;
import net.ltxprogrammer.changed.init.ChangedLatexTypes;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public class VariantBlindnessOverlay {
    private static final ResourceLocation TEXTURE = ResourceLocation.parse("textures/misc/white.png");
    private static final float ALPHA = 0.45F;
    private static float alphaO = 0.0F;

    public static void render(Gui gui, GuiGraphics graphics, float partialTicks, int screenWidth, int screenHeight) {
        Player player = Minecraft.getInstance().player;
        if (!ProcessTransfur.isPlayerTransfurred(player))
            return;
        var variant = ProcessTransfur.getPlayerTransfurVariant(player);
        if (variant == null)
            return;
        if (player.hasEffect(MobEffects.NIGHT_VISION))
            return; // Override effect
        if (variant.visionType != VisionType.REDUCED)
            return;
        Color3 color = variant.getColors().getFirst();
        var eyePosition = player.getEyePosition(partialTicks);
        float darkness = (15 - player.level().getRawBrightness(new BlockPos(Mth.floor(eyePosition.x), Mth.floor(eyePosition.y), Mth.floor(eyePosition.z)), 0)) / 15.0f;
        float alpha;
        if (variant.getLatexType() == ChangedLatexTypes.DARK_LATEX.get())
            alpha = Mth.lerp(0.65F, alphaO, darkness * ALPHA);
        else
            alpha = ALPHA * Minecraft.getInstance().options.screenEffectScale().get().floatValue();
        alphaO = alpha;

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        graphics.setColor(1, 1, 1, 1);
        int i1 = screenWidth;
        int j1 = screenHeight;
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(0.0D, (double)j1, -90).uv(0.0F, 1.0F).color(color.red(), color.green(), color.blue(), alpha).endVertex();
        bufferbuilder.vertex((double)i1, (double)j1, -90).uv(1.0F, 1.0F).color(color.red(), color.green(), color.blue(), alpha).endVertex();
        bufferbuilder.vertex((double)i1, 0.0D, -90).uv(1.0F, 0.0F).color(color.red(), color.green(), color.blue(), alpha).endVertex();
        bufferbuilder.vertex(0.0D, 0.0D, -90).uv(0.0F, 0.0F).color(color.red(), color.green(), color.blue(), alpha).endVertex();
        tesselator.end();
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        graphics.setColor(1, 1, 1, 1);
    }
}
