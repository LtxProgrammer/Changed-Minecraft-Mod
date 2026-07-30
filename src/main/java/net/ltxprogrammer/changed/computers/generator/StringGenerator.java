package net.ltxprogrammer.changed.computers.generator;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.Util;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;

import java.util.List;
import java.util.function.Function;

public interface StringGenerator {
    /**
     * Accepts a "single", [ "array", "of", "strings" ], or [ { "weight": 1, "data": "weighted strings" } ]
     */
    Codec<StringGenerator> CODEC = Codec.either(Codec.STRING, Codec.either(Codec.STRING.listOf(), WeightedEntry.Wrapper.codec(Codec.STRING).listOf()))
            .<Either<List<String>, List<WeightedEntry.Wrapper<String>>>>xmap(either -> {
                return either.map(single -> Either.left(List.of(single)), Function.identity());
            }, either -> {
                return Either.right(either);
            })
            .xmap(
                    either -> either.map(StringGenerator::random, StringGenerator::randomWeighted),
                    StringGenerator::unwrap
            );

    String generate(RandomSource random);
    Either<List<String>, List<WeightedEntry.Wrapper<String>>> unwrap();

    static StringGenerator fixed(String name) {
        return new StringGenerator() {
            @Override
            public String generate(RandomSource random) {
                return name;
            }

            @Override
            public Either<List<String>, List<WeightedEntry.Wrapper<String>>> unwrap() {
                return Either.left(List.of(name));
            }
        };
    }

    static StringGenerator random(String name1, String... names) {
        return random(ImmutableList.<String>builder()
                .add(name1).add(names).build());
    }

    static StringGenerator random(List<String> names) {
        if (names.size() == 1)
            return fixed(names.get(0));

        return new StringGenerator() {
            @Override
            public String generate(RandomSource random) {
                return Util.getRandom(names, random);
            }

            @Override
            public Either<List<String>, List<WeightedEntry.Wrapper<String>>> unwrap() {
                return Either.left(names);
            }
        };
    }

    static StringGenerator randomWeighted(List<WeightedEntry.Wrapper<String>> names) {
        if (names.size() == 1)
            return fixed(names.get(0).getData());

        return new StringGenerator() {
            @Override
            public String generate(RandomSource random) {
                return WeightedRandom.getRandomItem(random, names).map(WeightedEntry.Wrapper::getData).orElse(null);
            }

            @Override
            public Either<List<String>, List<WeightedEntry.Wrapper<String>>> unwrap() {
                return Either.right(names);
            }
        };
    }

    static StringGenerator randomWeighted(SimpleWeightedRandomList<String> names) {

        return new StringGenerator() {
            @Override
            public String generate(RandomSource random) {
                return names.getRandomValue(random).orElse(null);
            }

            @Override
            public Either<List<String>, List<WeightedEntry.Wrapper<String>>> unwrap() {
                return Either.right(names.unwrap());
            }
        };
    }
}
