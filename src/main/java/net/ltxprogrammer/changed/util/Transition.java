package net.ltxprogrammer.changed.util;

import net.minecraft.util.Mth;

public class Transition {
    public static float linear(float x) {
        return x;
    }

    public static float smoothStep(float x) {
        return (float)Mth.smoothstep(x);
    }

    public static float easeInOutSine(float x) {
        return -(Mth.cos(Mth.PI * x) - 1) / 2;
    }

    public static float easeInOutBack(float x) {
        final var c1 = 1.70158;
        final var c2 = c1 * 1.525;

        return (float)(x < 0.5
                ? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2
                : (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2);

    }

    public static float easeOutElastic(float x) {
        final var c4 = (2 * Math.PI) / 3;

        return (float)(x == 0
                ? 0
                : x == 1
                ? 1
                : Math.pow(2, -10 * x) * Math.sin((x * 10 - 0.75) * c4) + 1);

    }
}
