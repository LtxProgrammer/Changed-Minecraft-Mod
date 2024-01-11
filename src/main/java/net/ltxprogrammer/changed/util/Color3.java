package net.ltxprogrammer.changed.util;

import com.mojang.serialization.Codec;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public record Color3(float red, float green, float blue) {
    public static final Codec<Color3> CODEC = Codec.INT.xmap(Color3::fromInt, Color3::toInt);
    public static final Map<String, Color3> NAMED_COLORS = new HashMap<>();

    public int toInt() {
        return (int) (red * 255f) << 16 |
                (int) (green * 255f) << 8 |
                (int) (blue * 255f) << 0;
    }

    public static Color3 named(String name, float red, float green, float blue) {
        Color3 color3 = new Color3(red, green, blue);
        NAMED_COLORS.put(name, color3);
        return color3;
    }

    public static Color3 fromInt(int num) {
        return new Color3(
                ((num & 0xff0000) >> 16) / 255f,
                ((num & 0x00ff00) >> 8) / 255f,
                ((num & 0x0000ff) >> 0) / 255f
        );
    }

    @Nullable
    public static Color3 parseHex(String tag) {
        if (tag.length() > 0) {
            if (tag.charAt(0) == '#')
                tag = tag.substring(1);

            try {
                return fromInt(Integer.parseInt(tag, 16));
            } catch (Exception ignored) {
            }
        }

        return null;
    }

    public String toHexCode() {
        StringBuilder s = new StringBuilder(Integer.toString(toInt(), 16).toUpperCase());
        while (s.length() < 6)
            s.insert(0, "0");

        return "#" + s;
    }

    public static Color3 getColor(String color) {
        return NAMED_COLORS.computeIfAbsent(color.toLowerCase(), Color3::parseHex);
    }

    public static final Color3 WHITE = named("white", 1.0f, 1.0f, 1.0f);
    public static final Color3 BLACK = named("black", 0.0f, 0.0f, 0.0f);
    public static final Color3 GRAY = named("gray", 0.588f, 0.588f, 0.588f);
    public static final Color3 DARK = named("dark", 0.224f, 0.224f, 0.224f);
    public static final Color3 BROWN = named("brown", 0.365f, 0.278f, 0.263f);
    public static final Color3 BLUE = named("blue", 0.318f, 0.396f, 0.616f);
    public static final Color3 SILVER = named("silver", 0.584f, 0.612f, 0.647f);
    public static final Color3 YELLOW = named("yellow", 1.0f, 0.824f, 0.004f);
    public static final Color3 GREEN = named("green", 0.749f, 0.949f, 0.596f);

    public static final Color3 TSC_BLUE = named("tsc_blue", 0.31f, 0.76f, 1.0f);

    public float brightness() {
        return 0.2126f * red + 0.7152f * green + 0.0722f * blue;
    }

    public Color3 add(float v) {
        return new Color3(red + v, green + v, blue + v);
    }

    public Color3 mul(float v) {
        return new Color3(red * v, green * v, blue * v);
    }
}
