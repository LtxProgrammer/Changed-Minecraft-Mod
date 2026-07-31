package net.ltxprogrammer.changed.computers;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.resources.ResourceLocation;

public record UITheme(ResourceLocation desktopBackground, IconTheme iconTheme, Color3 fgColor, Color3 accentColor, Color3 bgColor) {
    public static final UITheme DEFAULT = new UITheme(Changed.modResource("textures/gui/computer/bg/lines.png"),
            IconTheme.STANDARD, Color3.fromInt(0xffffff), Color3.fromInt(0x15b9fa), Color3.fromInt(0x959595));

    public int getFGColor(int alpha) {
        return fgColor.toInt() + (alpha << 24);
    }

    /// Returns an int representing the foreground color with full alpha
    public int getFGColor() {
        return getFGColor(0xFF);
    }

    public int getAccentColor(int alpha) {
        return accentColor.toInt() + (alpha << 24);
    }

    /// Returns an int representing the accent color with full alpha
    public int getAccentColor() {
        return getAccentColor(0xFF);
    }

    public int getBGColor(int alpha) {
        return bgColor.toInt() + (alpha << 24);
    }

    /// Returns an int representing the background color with full alpha
    public int getBGColor() {
        return getBGColor(0xFF);
    }
}
