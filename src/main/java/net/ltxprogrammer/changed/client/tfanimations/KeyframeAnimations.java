package net.ltxprogrammer.changed.client.tfanimations;

import com.mojang.math.Vector3f;
import net.minecraft.util.Mth;

public class KeyframeAnimations {
    public static Vector3f degreeVec(float x, float y, float z) {
        return new Vector3f(x * Mth.DEG_TO_RAD, y * Mth.DEG_TO_RAD, z * Mth.DEG_TO_RAD);
    }

    public static Vector3f radianVec(float x, float y, float z) {
        return new Vector3f(x, y, z);
    }

    public static Vector3f posVec(float x, float y, float z) {
        return new Vector3f(x, y, z);
    }
}
