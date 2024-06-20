package net.ltxprogrammer.changed.client.tfanimations;

import com.mojang.math.Vector3f;

import java.util.function.BiConsumer;

public class Keyframe {
    private final float time;
    private final Vector3f value;
    private final BiConsumer<Float, AnimationChannel.Float4Consumer> interpolation;

    public Keyframe(float time, Vector3f value, BiConsumer<Float, AnimationChannel.Float4Consumer> interpolation) {
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

    public BiConsumer<Float, AnimationChannel.Float4Consumer> getInterpolation() {
        return interpolation;
    }
}
