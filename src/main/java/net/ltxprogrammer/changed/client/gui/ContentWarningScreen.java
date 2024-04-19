package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class ContentWarningScreen extends Screen {
    private static final ResourceLocation BACKGROUND_LOCATION = Changed.modResource("textures/gui/warning_background.png");
    private static final ResourceLocation ICON_LOCATION = Changed.modResource("textures/gui/warning_icon.png");
    private static final TranslatableComponent WARNING = new TranslatableComponent("text.changed.warning");
    private static final TranslatableComponent WARNING_CONTENT1 = new TranslatableComponent("text.changed.warning.content1");
    private static final TranslatableComponent WARNING_CONTENT2 = new TranslatableComponent("text.changed.warning.content2");

    public ContentWarningScreen() {
        super(new TextComponent("Content Warning"));
    }

    @Override
    public void renderBackground(PoseStack stack, int v) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, BACKGROUND_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(0.0D, (double)this.height, 0.0D).uv(0.0F, (float)this.height / f + (float)v).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex((double)this.width, (double)this.height, 0.0D).uv((float)this.width / f, (float)this.height / 32.0F + (float)v).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex((double)this.width, 0.0D, 0.0D).uv((float)this.width / f, (float)v).color(64, 64, 64, 255).endVertex();
        bufferbuilder.vertex(0.0D, 0.0D, 0.0D).uv(0.0F, (float)v).color(64, 64, 64, 255).endVertex();
        tesselator.end();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, ICON_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        blit(stack, this.width / 2 - 45, 20, 0, 0, 90, 90, 90, 90);
    }

    @Override
    protected void init() {
        super.init();
        int l = this.height / 4 + 48 + 72;
        this.addRenderableWidget(new Button(this.width / 2 - 100, l, 200, 20, new TranslatableComponent("gui.proceed"), (p_96776_) -> {
            Changed.config.client.showContentWarning.set(false);
            this.minecraft.setScreen(new TitleScreen(true));
        }));
    }

    @Override
    public void render(PoseStack pose, int x, int y, float partialTick) {
        renderBackground(pose);
        super.render(pose, x, y, partialTick);

        pose.pushPose();
        pose.translate(this.width / 2.0, 130.0, 0.0);

        pose.pushPose();
        pose.scale(1.5f, 1.5f, 1.5f);
        drawCenteredString(pose, this.font, WARNING, 0, -8, 0xFFFFFFFF);
        pose.popPose();

        pose.translate(0.0, 16.0, 0.0);
        drawCenteredString(pose, this.font, WARNING_CONTENT1, 0, -8, 0xFFFFFFFF);
        pose.translate(0.0, 8.0, 0.0);
        drawCenteredString(pose, this.font, WARNING_CONTENT2, 0, -8, 0xFFFFFFFF);
        pose.popPose();
    }
}
