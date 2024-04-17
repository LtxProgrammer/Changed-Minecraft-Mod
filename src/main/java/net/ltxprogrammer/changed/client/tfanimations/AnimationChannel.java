package net.ltxprogrammer.changed.client.tfanimations;

import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

public class AnimationChannel {
    private final Targets target;
    private final List<Keyframe> keyframes;

    public Targets getTarget() {
        return target;
    }

    public AnimationChannel(Targets target, Keyframe... keyframes) {
        this.target = target;
        this.keyframes = Arrays.asList(keyframes);
        this.keyframes.sort((k1, k2) -> Float.compare(k1.getTime(), k2.getTime()));
    }

    private Pair<Pair<Keyframe, Keyframe>, Pair<Keyframe, Keyframe>> getKeyframePair(float time) {
        Keyframe k1 = null, k2 = null, k3 = null, k4 = null;
        float dt1 = -9999.0f, dt2 = 9999.0f;
        for (var k : keyframes) {
            float dt = k.getTime() - time;
            if (dt <= 0.0f) {
                if (k2 == null || dt > dt1) {
                    k1 = k2;
                    k2 = k;
                    dt1 = dt;
                }
            } else {
                if (k3 == null || dt < dt2) {
                    k3 = k;
                    dt2 = dt;
                } else if (k4 == null) {
                    k4 = k;
                }
            }
        }

        return Pair.of(Pair.of(k1, k2), Pair.of(k3, k4));
    }

    public void animate(AnimationDefinition definition, ModelPart part, float time) {
        var p = getKeyframePair(time);
        final var p1 = p.left().left();
        final var p2 = p.left().right();
        // The point we are interpolating for is between p2 and p3
        final var p3 = p.right().left();
        final var p4 = p.right().right();

        final var v1 = (p1 != null ? p1.getValue() : Vector3f.ZERO).copy();
        final var v2 = (p2 != null ? p2.getValue() : Vector3f.ZERO).copy();
        final var v3 = (p3 != null ? p3.getValue() : Vector3f.ZERO).copy();
        final var v4 = (p4 != null ? p4.getValue() : Vector3f.ZERO).copy();

        final AtomicReference<Vector3f> atomic = new AtomicReference<>(Vector3f.ZERO);
        final Float4Consumer float4Consumer = (b1, b2, b3, b4) -> {
            v1.mul(b1);
            v2.mul(b2);
            v3.mul(b3);
            v4.mul(b4);

            v1.add(v2);
            v1.add(v3);
            v1.add(v4);

            atomic.set(v1);
        };

        if (p2 != null && p3 != null) {
            p2.getInterpolation().accept(Mth.map(time, p2.getTime(), p3.getTime(), 0.0f, 1.0f), float4Consumer);
        } else if (p2 != null) {
            p2.getInterpolation().accept(Mth.map(time, p2.getTime(), definition.length, 0.0f, 1.0f), float4Consumer);
        } else if (p3 != null) {
            p3.getInterpolation().accept(Mth.map(time, 0.0f, p3.getTime(), 0.0f, 1.0f), float4Consumer);
        }

        final Vector3f result = atomic.getAcquire();

        switch (target) {
            case POSITION:
                part.x = result.x();
                part.y = result.y();
                part.z = result.z();
            case ROTATION:
                part.xRot = result.x();
                part.yRot = result.y();
                part.zRot = result.z();
        }
    }

    public enum Targets {
        POSITION,
        ROTATION
    }

    public enum Interpolations implements BiConsumer<Float, AnimationChannel.Float4Consumer> {
        LINEAR((u, consumer) -> consumer.accept(0.0f, 1.0f - u, u, 0.0f)),
        CATMULLROM((u, consumer) -> {
            final float T = 0.5f;
            final float u2 = u * u;
            final float u3 = u2 * u;

            consumer.accept(
                    -T * u + 2f * T * u2 - T * u3,
                    1f + (T - 3f) * u2 + (2f - T) * u3,
                    T * u + (3f - 2f * T) * u2 + (T - 2f) * u3,
                    -T * u2 + T * u3
            );
        });

        private final BiConsumer<Float, Float4Consumer> fn;

        Interpolations(BiConsumer<Float, Float4Consumer> fn) {
            this.fn = fn;
        }

        @Override
        public void accept(Float aFloat, Float4Consumer float4Consumer) {
            fn.accept(aFloat, float4Consumer);
        }
    }

    public interface Float4Consumer {
        void accept(float f1, float f2, float f3, float f4);
    }
}
