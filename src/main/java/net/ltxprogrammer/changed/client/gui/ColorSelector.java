package net.ltxprogrammer.changed.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class ColorSelector extends EditBox {
    private static final int padding = 5;
    private static final int fieldName = 12;
    private final Supplier<Color3> colorGetter;
    private final Consumer<Color3> colorSetter;
    private final Font font;
    private final Component name;

    private static final int COLOR_GOOD = 14737632;
    private static final int COLOR_ERROR = 16733525;

    public ColorSelector(Font font, int x, int y, int width, int height, Component name, Supplier<Color3> colorGetter, Consumer<Color3> colorSetter) {
        super(font, x, y + fieldName, width - (height - padding), height - fieldName, name);
        this.font = font;
        this.name = name;
        this.colorGetter = colorGetter;
        this.colorSetter = colorSetter;

        this.insertText(colorGetter.get().toHexCode());
        this.setResponder(this::onValueChange);
        this.setFilter(this::validColor);
    }

    private void onValueChange(String text) {
        try {
            var color = Color3.parseHex(text);
            if (color != null) {
                colorSetter.accept(color);
                this.setTextColor(COLOR_GOOD);
            }

            else {
                this.setTextColor(COLOR_ERROR);
            }
        } catch (Exception ex) {
            this.setTextColor(COLOR_ERROR);
        }
    }

    private boolean validColor(String text) {
        if (text.isEmpty())
            return true;
        if (text.length() == 1 && text.charAt(0) == '#')
            return true;
        return Color3.parseHex(text) != null;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float deltaTime) {
        super.renderButton(poseStack, mouseX, mouseY, deltaTime);

        if (this.isVisible()) {
            this.font.draw(poseStack, this.name, this.x, this.y - fieldName, 0xFFFFFFFF);

            // Render color preview
            int startX = this.x + this.width + padding;
            int startY = this.y;
            int endX = startX + this.height;
            int endY = startY + this.height;

            // Border
            {
                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferbuilder = tesselator.getBuilder();
                RenderSystem.setShader(GameRenderer::getPositionShader);
                RenderSystem.setShaderColor(0.0F, 0.0F, 0.0F, 1.0F);
                RenderSystem.disableTexture();
                bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
                bufferbuilder.vertex(endX + 1, startY - 1, 0.0D).endVertex();
                bufferbuilder.vertex(startX - 1, startY - 1, 0.0D).endVertex();
                bufferbuilder.vertex(startX - 1, endY + 1, 0.0D).endVertex();
                bufferbuilder.vertex(endX + 1, endY + 1, 0.0D).endVertex();
                tesselator.end();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.enableTexture();
            }

            // Color
            {
                var color = colorGetter.get();
                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferbuilder = tesselator.getBuilder();
                RenderSystem.setShader(GameRenderer::getPositionShader);
                RenderSystem.setShaderColor(color.red(), color.green(), color.blue(), 1.0F);
                RenderSystem.disableTexture();
                bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
                bufferbuilder.vertex(endX, startY, 0.0D).endVertex();
                bufferbuilder.vertex(startX, startY, 0.0D).endVertex();
                bufferbuilder.vertex(startX, endY, 0.0D).endVertex();
                bufferbuilder.vertex(endX, endY, 0.0D).endVertex();
                tesselator.end();
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                RenderSystem.enableTexture();
            }
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput output) {

    }
}
