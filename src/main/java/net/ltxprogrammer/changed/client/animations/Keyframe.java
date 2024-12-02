package net.ltxprogrammer.changed.client.animations;

import com.mojang.math.Vector3f;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.entity.Emote;
import net.minecraft.util.Mth;

import java.security.InvalidParameterException;
import java.util.Optional;
import java.util.function.BiConsumer;

public class Keyframe {
    private final float time;
    private final Vector3f value;
    private final AnimationChannel.Interpolation interpolation;

    public static final Codec<Keyframe> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.FLOAT.fieldOf("time").forGetter(keyframe -> keyframe.time),
            Vector3f.CODEC.optionalFieldOf("degrees").forGetter(keyframe -> Optional.empty()),
            Vector3f.CODEC.optionalFieldOf("radians").forGetter(keyframe -> Optional.empty()),
            Vector3f.CODEC.optionalFieldOf("position").forGetter(keyframe -> Optional.empty()),
            Vector3f.CODEC.optionalFieldOf("value").forGetter(keyframe -> Optional.of(keyframe.value)),
            AnimationChannel.Interpolation.CODEC.fieldOf("interpolation").forGetter(option -> option.interpolation)
    ).apply(builder, (time, degrees, radians, position, value, interpolation) -> {
        Vector3f actual;
        if (degrees.isPresent()) {
            actual = degrees.get();
            actual.mul(Mth.DEG_TO_RAD);
        } else if (radians.isPresent())
            actual = radians.get();
        else if (position.isPresent())
            actual = position.get();
        else if (value.isPresent())
            actual = value.get();
        else
            throw new InvalidParameterException("At least one of degrees, radians, position, or value must be specified");

        return new Keyframe(time, actual, interpolation);
    }));

    public Keyframe(float time, Vector3f value, AnimationChannel.Interpolation interpolation) {
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
