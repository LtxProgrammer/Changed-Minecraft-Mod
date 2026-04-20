package net.ltxprogrammer.changed.computers;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.util.Color3;
import net.minecraft.resources.ResourceLocation;

public record UITheme(ResourceLocation desktopBackground, IconTheme iconTheme, Color3 fgColor, Color3 accentColor, Color3 bgColor) {
    public static final UITheme DEFAULT = new UITheme(Changed.modResource("textures/gui/computer/bg/lines.png"),
            IconTheme.STANDARD, Color3.fromInt(0xffffff), Color3.fromInt(0x15b9fa), Color3.fromInt(0x959595));
}
