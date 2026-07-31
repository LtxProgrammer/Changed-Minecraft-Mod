package net.ltxprogrammer.changed.computers.generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.computers.DiscData;
import net.ltxprogrammer.changed.computers.File;
import net.ltxprogrammer.changed.computers.Folder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;

public class FileGenerator implements WeightedEntry {
    public static final Codec<FileGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            File.Type.CODEC.fieldOf("type").forGetter(generator -> generator.type),
            StringGenerator.CODEC.fieldOf("name").forGetter(generator -> generator.nameGenerator),
            StringGenerator.CODEC.fieldOf("content").forGetter(generator -> generator.contentGenerator),
            Weight.CODEC.fieldOf("weight").orElse(Weight.of(10)).forGetter(generator -> generator.weight)
    ).apply(instance, FileGenerator::new));

    protected final File.Type type;
    protected final StringGenerator nameGenerator;
    protected final StringGenerator contentGenerator;
    protected final Weight weight;

    public FileGenerator(File.Type type, StringGenerator nameGenerator, StringGenerator contentGenerator, Weight weight) {
        this.type = type;
        this.nameGenerator = nameGenerator;
        this.contentGenerator = contentGenerator;
        this.weight = weight;
    }

    public void generate(RandomSource random, DiscData data, Folder folder) {
        folder.addFile(nameGenerator.generate(random), new File(type, contentGenerator.generate(random), data::markModified));
    }

    @Override
    public Weight getWeight() {
        return weight;
    }
}
