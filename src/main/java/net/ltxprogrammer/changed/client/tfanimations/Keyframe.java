package net.ltxprogrammer.changed.client.tfanimations;

import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;

public class Keyframe {
    private final float time;
    private final Vector3f value;
    private final Float2FloatFunction interpolation;

    public Keyframe(float time, Vector3f value, Float2FloatFunction interpolation) {
        this.time = time;
        this.value = value;
        this.interpolation = interpolation;
    }

    public float getTime() {
        return time;
    }

    public Vector3f getValue() {
        return value;
    }

    public Float2FloatFunction getInterpolation() {
        return interpolation;
    }
}
