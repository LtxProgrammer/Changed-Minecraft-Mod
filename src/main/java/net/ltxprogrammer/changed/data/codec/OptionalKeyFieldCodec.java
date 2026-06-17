package net.ltxprogrammer.changed.data.codec;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.OptionalFieldCodec;

import java.util.Objects;
import java.util.Optional;

/// Allows for missing key from MapLike input, but passes failure through
public class OptionalKeyFieldCodec<A> extends OptionalFieldCodec<A> {
    private final String name;
    private final Codec<A> elementCodec;

    public OptionalKeyFieldCodec(String name, Codec<A> elementCodec) {
        super(name, elementCodec);
        this.name = name;
        this.elementCodec = elementCodec;
    }

    @Override
    public <T> DataResult<Optional<A>> decode(DynamicOps<T> ops, MapLike<T> input) {
        final T value = input.get(name);
        if (value == null) { // No key, continue as empty
            return DataResult.success(Optional.empty());
        }
        return elementCodec.parse(ops, value).map(Optional::of);
    }

    /// Creates a codec that optionally gets a field called `name`, or passes an error if decoding fails
    public static <A> OptionalKeyFieldCodec<A> keyOptionalFieldOf(String name, Codec<A> codec) {
        return new OptionalKeyFieldCodec<>(name, codec);
    }

    /// Creates a codec that optionally gets a field called `name` which defaults to `defaultValue`, or passes an error if decoding fails
    public static <A> MapCodec<A> keyOptionalFieldOf(String name, Codec<A> codec, A defaultValue) {
        return new OptionalKeyFieldCodec<>(name, codec).xmap(
                o -> o.orElse(defaultValue),
                a -> Objects.equals(a, defaultValue) ? Optional.empty() : Optional.of(a)
        );
    }
}
