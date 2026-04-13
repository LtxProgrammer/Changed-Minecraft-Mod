package net.ltxprogrammer.changed.client.gui.computer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.computers.UITheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Supplier;

public interface ApplicationScreen {
    ResourceLocation WIDGETS = Changed.modResource("widgets");

    static Function<Button.Builder, Button> iconButton(Supplier<UITheme> themeSupplier, ResourceLocation icon, int iconX, int iconY, int iconOffsetX, int iconOffsetY, int iconWidth, int iconHeight, int atlasWidth, int atlasHeight, int sectionHeight) {
        return builder -> new Button(builder) {
            private int getTextureY() {
                int i = 1;
                if (!this.active) {
                    i = 0;
                } else if (this.isHoveredOrFocused()) {
                    i = 2;
                }

                return i * 20;
            }

            @Override
            protected void renderWidget(@NotNull GuiGraphics graphics, int mx, int my, float partialTicks) {
                var theme = themeSupplier.get();

                var fg = theme.fgColor();
                var accent = theme.accentColor();
                var bg = theme.bgColor();

                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();

                {
                    var widgetsLocation = theme.iconTheme().getIconLocation(WIDGETS);
                    graphics.setColor(bg.red(), bg.green(), bg.blue(), this.alpha);
                    graphics.blitNineSliced(widgetsLocation, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY() + 128);
                    graphics.setColor(accent.red(), accent.green(), accent.blue(), this.alpha);
                    graphics.blitNineSliced(widgetsLocation, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY() + 64);
                    graphics.setColor(fg.red(), fg.green(), fg.blue(), this.alpha);
                    graphics.blitNineSliced(widgetsLocation, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
                }

                {
                    var iconLocation = theme.iconTheme().getIconLocation(icon);
                    graphics.setColor(bg.red(), bg.green(), bg.blue(), this.alpha);
                    this.renderTexture(graphics, iconLocation, this.getX() + iconOffsetX, this.getY() + iconOffsetY,
                            iconX, iconY + (sectionHeight * 2), /* yDiff */ 0,
                            iconWidth, iconHeight,
                            atlasWidth, atlasHeight);
                    graphics.setColor(accent.red(), accent.green(), accent.blue(), this.alpha);
                    this.renderTexture(graphics, iconLocation, this.getX() + iconOffsetX, this.getY() + iconOffsetY,
                            iconX, iconY + (sectionHeight), /* yDiff */ 0,
                            iconWidth, iconHeight,
                            atlasWidth, atlasHeight);
                    graphics.setColor(fg.red(), fg.green(), fg.blue(), this.alpha);
                    this.renderTexture(graphics, iconLocation, this.getX() + iconOffsetX, this.getY() + iconOffsetY,
                            iconX, iconY, /* yDiff */ 0,
                            iconWidth, iconHeight,
                            atlasWidth, atlasHeight);
                }

                graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            }
        };
    }

    static Function<Button.Builder, Button> iconButton(Supplier<UITheme> themeSupplier, int iconX, int iconY) {
        return iconButton(themeSupplier, WIDGETS, iconX, iconY,
                0, 0, 20, 20, 256, 256, 64);
    }

    static Function<Button.Builder, Button> listItemButton(Supplier<UITheme> themeSupplier, ResourceLocation icon, int iconX, int iconY, int iconOffsetX, int iconOffsetY, int iconWidth, int iconHeight, int atlasWidth, int atlasHeight, int sectionHeight) {
        return builder -> new Button(builder) {
            private int getTextureY() {
                int i = 1;
                if (!this.active) {
                    i = 0;
                } else if (this.isHoveredOrFocused()) {
                    i = 2;
                }

                return i * 20;
            }

            @Override
            protected void renderWidget(@NotNull GuiGraphics graphics, int mx, int my, float partialTicks) {
                var theme = themeSupplier.get();

                var fg = theme.fgColor();
                var accent = theme.accentColor();
                var bg = theme.bgColor();

                Minecraft minecraft = Minecraft.getInstance();
                graphics.setColor(1.0F, 1.0F, 1.0F, this.alpha);
                RenderSystem.enableBlend();
                RenderSystem.enableDepthTest();


                {
                    var widgetsLocation = theme.iconTheme().getIconLocation(WIDGETS);
                    graphics.setColor(bg.red(), bg.green(), bg.blue(), this.alpha);
                    graphics.blitNineSliced(widgetsLocation, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY() + 128);
                    graphics.setColor(accent.red(), accent.green(), accent.blue(), this.alpha);
                    graphics.blitNineSliced(widgetsLocation, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY() + 64);
                    graphics.setColor(fg.red(), fg.green(), fg.blue(), this.alpha);
                    graphics.blitNineSliced(widgetsLocation, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());
                }

                {
                    var iconLocation = theme.iconTheme().getIconLocation(icon);
                    graphics.setColor(bg.red(), bg.green(), bg.blue(), this.alpha);
                    this.renderTexture(graphics, iconLocation, this.getX() + iconOffsetX, this.getY() + iconOffsetY,
                            iconX, iconY + (sectionHeight * 2), /* yDiff */ 0,
                            iconWidth, iconHeight,
                            atlasWidth, atlasHeight);
                    graphics.setColor(accent.red(), accent.green(), accent.blue(), this.alpha);
                    this.renderTexture(graphics, iconLocation, this.getX() + iconOffsetX, this.getY() + iconOffsetY,
                            iconX, iconY + (sectionHeight), /* yDiff */ 0,
                            iconWidth, iconHeight,
                            atlasWidth, atlasHeight);
                    graphics.setColor(fg.red(), fg.green(), fg.blue(), this.alpha);
                    this.renderTexture(graphics, iconLocation, this.getX() + iconOffsetX, this.getY() + iconOffsetY,
                            iconX, iconY, /* yDiff */ 0,
                            iconWidth, iconHeight,
                            atlasWidth, atlasHeight);
                }

                graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                int i = getFGColor();
                int j = (this.getY() + (this.getY() + this.getHeight()) - 9) / 2 + 1;
                graphics.drawString(minecraft.font, this.getMessage(), this.getX() + 20, j,
                        i | Mth.ceil(this.alpha * 255.0F) << 24);
            }
        };
    }

    void initialize(int desktopLeft, int desktopTop, int desktopWidth, int desktopHeight);

    default void render(GuiGraphics graphics, int cursorX, int cursorY, float partialTicks) {

    }

    default void tick() {

    }

    default void opened() {

    }

    default void restored() {

    }

    default void closed() {

    }
}
