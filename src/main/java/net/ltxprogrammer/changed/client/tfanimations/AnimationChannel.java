package net.ltxprogrammer.changed.client.tfanimations;

import com.mojang.math.Vector3f;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.floats.Float2FloatFunction;
import net.ltxprogrammer.changed.util.Transition;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

import java.util.Arrays;
import java.util.List;

public class AnimationChannel {
    private final Targets target;
    private final List<Keyframe> keyframes;

    public Targets getTarget() {
        return target;
    }

    public AnimationChannel(Targets target, Keyframe... keyframes) {
        this.target = target;
        this.keyframes = Arrays.asList(keyframes);
        this.keyframes.sort((k1, k2) -> Float.compare(k2.getTime(), k1.getTime()));
    }

    private Pair<Keyframe, Keyframe> getKeyframePair(float time) {
        Keyframe k1 = null, k2 = null;
        float dt1 = -9999.0f, dt2 = 9999.0f;
        for (var k : keyframes) {
            float dt = k.getTime() - time;
            if (dt <= 0.0f) {
                if (k1 == null || dt > dt1) {
                    k1 = k;
                    dt1 = dt;
                }
            } else {
                if (k2 == null || dt < dt2) {
                    k2 = k;
                    dt2 = dt;
                }
            }
        }

        return Pair.of(k1, k2);
    }

    public void animate(AnimationDefinition definition, ModelPart part, float time) {
        var p = getKeyframePair(time);
        var before = p.left();
        var after = p.right();

        Vector3f result;
        if (before != null && after != null) {
            float lerp = after.getInterpolation().get(Mth.map(time, before.getTime(), after.getTime(), 0.0f, 1.0f));
            result = before.getValue().copy();
            result.lerp(after.getValue(), lerp);
        } else if (before != null) {
            float lerp = before.getInterpolation().get(Mth.map(time, before.getTime(), definition.length, 0.0f, 1.0f));
            result = before.getValue().copy();
            result.lerp(Vector3f.ZERO, lerp);
        } else if (after != null) {
            float lerp = after.getInterpolation().get(Mth.map(time, 0.0f, after.getTime(), 0.0f, 1.0f));
            result = Vector3f.ZERO.copy();
            result.lerp(after.getValue(), lerp);
        } else {
            result = Vector3f.ZERO;
        }

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

    public enum Interpolations implements Float2FloatFunction {
        LINEAR(Transition::linear),
        CATMULLROM(Transition::easeInOutSine);

        private final Float2FloatFunction fn;

        Interpolations(Float2FloatFunction fn) {
            this.fn = fn;
        }

        @Override
        public float get(float key) {
            return fn.get(key);
        }
    }
}
