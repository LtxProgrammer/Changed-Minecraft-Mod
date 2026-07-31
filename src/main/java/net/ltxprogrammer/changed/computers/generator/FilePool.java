package net.ltxprogrammer.changed.computers.generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.computers.DiscData;
import net.ltxprogrammer.changed.computers.Folder;
import net.ltxprogrammer.changed.util.StreamUtil;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandomList;

import java.util.Optional;

public class FilePool {
    public static final Codec<FilePool> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            WeightedRandomList.codec(FileGenerator.CODEC).fieldOf("files").forGetter(pool -> pool.fileGenerators),
            Codec.INT.optionalFieldOf("minRolls").forGetter(pool -> Optional.of(pool.minRolls)),
            Codec.INT.optionalFieldOf("maxRolls").forGetter(pool -> Optional.of(pool.maxRolls)),
            Codec.INT.optionalFieldOf("rolls").forGetter(pool -> Optional.empty())
    ).apply(instance, FilePool::new));

    protected final WeightedRandomList<FileGenerator> fileGenerators;
    protected final int minRolls;
    protected final int maxRolls;

    protected FilePool(WeightedRandomList<FileGenerator> fileGenerators, Optional<Integer> minRolls, Optional<Integer> maxRolls, Optional<Integer> rolls) {
        this(fileGenerators, minRolls.orElseGet(rolls::orElseThrow), maxRolls.orElseGet(rolls::orElseThrow));
    }

    public FilePool(WeightedRandomList<FileGenerator> fileGenerators, int rolls) {
        this.fileGenerators = fileGenerators;
        this.minRolls = rolls;
        this.maxRolls = rolls;
    }

    public FilePool(WeightedRandomList<FileGenerator> fileGenerators, int minRolls, int maxRolls) {
        this.fileGenerators = fileGenerators;
        this.minRolls = minRolls;
        this.maxRolls = maxRolls;
    }

    public void generate(RandomSource random, DiscData data, Folder folder) {
        StreamUtil.weightedShuffledStream(this.fileGenerators, random)
                .limit(random.nextInt(minRolls, maxRolls + 1))
                .forEach(fileGenerator -> {
            fileGenerator.generate(random, data, folder);
        });
    }
}
