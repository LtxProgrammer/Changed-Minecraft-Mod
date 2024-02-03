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
}
