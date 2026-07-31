package net.ltxprogrammer.changed.client.gui.computer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.computers.UITheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ApplicationScreen extends GuiEventListener {
    ResourceLocation WIDGETS = Changed.modResource("widgets");
    ResourceLocation WIDGETS2 = Changed.modResource("widgets2");

    Component COMPONENT_EXIT = Component.translatable("text.changed.exit");
    Component COMPONENT_SAVE = Component.translatable("text.changed.save");
    Component COMPONENT_EDIT = Component.translatable("text.changed.edit");

    static StringWidget shadowlessString(int x, int y, int width, int height, Component text, Font font) {
        return new StringWidget(x, y, width, height, text, font) {
            private float alignX = 0.5f;

            private StringWidget horizontalAlignment(float alignX) {
                this.alignX = alignX;
                return this;
            }

            @Override
            public StringWidget alignLeft() {
                return this.horizontalAlignment(0.0F);
            }

            @Override
            public StringWidget alignCenter() {
                return this.horizontalAlignment(0.5F);
            }

            @Override
            public StringWidget alignRight() {
                return this.horizontalAlignment(1.0F);
            }

            @Override
            public void renderWidget(@NotNull GuiGraphics graphics, int mx, int my, float partialTicks) {
                Component component = this.getMessage();
                Font font = this.getFont();
                int i = this.getX() + Math.round(this.alignX * (float)(this.getWidth() - font.width(component)));
                int j = this.getY() + (this.getHeight() - 9) / 2;
                graphics.drawString(font, component, i, j, this.getColor(), false);
            }
        };
    }

    static Function<Button.Builder, Button> textButton(Supplier<UITheme> themeSupplier) {
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

                graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                Minecraft minecraft = Minecraft.getInstance();
                int i = getFGColor();
                this.renderString(graphics, minecraft.font, i | Mth.ceil(this.alpha * 255.0F) << 24);
            }
        };
    }

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

    static Function<Button.Builder, Button> iconButton2(Supplier<UITheme> themeSupplier, int iconX, int iconY) {
        return iconButton(themeSupplier, WIDGETS2, iconX, iconY,
                0, 0, 20, 20, 256, 256, 64);
    }

    static Function<Button.Builder, Button> listItemButtonThemed(Supplier<UITheme> themeSupplier, ResourceLocation icon, int iconX, int iconY, int iconOffsetX, int iconOffsetY, int iconWidth, int iconHeight, int atlasWidth, int atlasHeight, int sectionHeight) {
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

    static Function<Button.Builder, Button> listItemButtonStatic(Supplier<UITheme> themeSupplier, ResourceLocation iconLocation, int iconX, int iconY, int iconOffsetX, int iconOffsetY, int iconWidth, int iconHeight, int atlasWidth, int atlasHeight, int sectionHeight) {
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
                    graphics.setColor(1.0f, 1.0f, 1.0f, this.alpha);
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

    static Checkbox checkBox(Supplier<UITheme> themeSupplier, int x, int y, int width, int height, Component label, boolean selected, Consumer<Checkbox> onPress) {
        return checkBox(themeSupplier, x, y, width, height, label, selected, true, onPress);
    }

    static Checkbox checkBox(Supplier<UITheme> themeSupplier, int x, int y, int width, int height, Component label, boolean selected, boolean showLabel, Consumer<Checkbox> onPress) {
        return new Checkbox(x, y, width, height, label, selected, showLabel) {
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
            public void onPress() {
                super.onPress();
                onPress.accept(this);
            }

            @Override
            public void renderWidget(@NotNull GuiGraphics graphics, int mx, int my, float partialTicks) {
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
                    graphics.blitNineSliced(widgetsLocation, this.getX(), this.getY(), 20, this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY() + 128);
                    graphics.setColor(accent.red(), accent.green(), accent.blue(), this.alpha);
                    graphics.blitNineSliced(widgetsLocation, this.getX(), this.getY(), 20, this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY() + 64);
                    graphics.setColor(fg.red(), fg.green(), fg.blue(), this.alpha);
                    graphics.blitNineSliced(widgetsLocation, this.getX(), this.getY(), 20, this.getHeight(), 20, 4, 200, 20, 0, this.getTextureY());

                    if (this.selected()) {
                        graphics.setColor(bg.red(), bg.green(), bg.blue(), this.alpha);
                        this.renderTexture(graphics, widgetsLocation, this.getX(), this.getY(),
                                220, 40 + 128, /* yDiff */ 0,
                                20, 20,
                                256, 256);
                        graphics.setColor(accent.red(), accent.green(), accent.blue(), this.alpha);
                        this.renderTexture(graphics, widgetsLocation, this.getX(), this.getY(),
                                220, 40 + 64, /* yDiff */ 0,
                                20, 20,
                                256, 256);
                        graphics.setColor(fg.red(), fg.green(), fg.blue(), this.alpha);
                        this.renderTexture(graphics, widgetsLocation, this.getX(), this.getY(),
                                220, 40, /* yDiff */ 0,
                                20, 20,
                                256, 256);
                    }
                }

                Font font = minecraft.font;
                graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
                if (showLabel) {
                    graphics.drawString(font, this.getMessage(), this.getX() + 24, this.getY() + (this.height - 8) / 2, 14737632 | Mth.ceil(this.alpha * 255.0F) << 24);
                }
            }
        };
    }

    static ScrollBarVerticalStepped verticalScrollBarStepped(Supplier<UITheme> themeSupplier, int x, int y, int width, int height) {
        return new ScrollBarVerticalStepped(themeSupplier, x, y, width, height, Component.empty());
    }

    void initialize(int desktopLeft, int desktopTop, int desktopWidth, int desktopHeight);

    default void render(GuiGraphics graphics, int cursorX, int cursorY, float partialTicks) {

    }

    default void tick(int desktopLeft, int desktopTop, int desktopWidth, int desktopHeight) {

    }

    default void opened() {

    }

    default void restored() {

    }

    default void closed() {

    }

    default void setFocused(boolean focused) {

    }

    default boolean isFocused() {
        return true;
    }
}
