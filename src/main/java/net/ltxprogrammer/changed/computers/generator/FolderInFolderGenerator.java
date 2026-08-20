package net.ltxprogrammer.changed.computers.generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.computers.DiscData;
import net.ltxprogrammer.changed.computers.File;
import net.ltxprogrammer.changed.computers.Folder;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weight;

/// Generates a folder and its contents instead of a file.
public class FolderInFolderGenerator extends FileGenerator {
    public static final Codec<FolderInFolderGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StringGenerator.CODEC.fieldOf("name").forGetter(generator -> generator.nameGenerator),
            FolderGenerator.CODEC.fieldOf("content").forGetter(generator -> generator.contentGenerator),
            Weight.CODEC.fieldOf("weight").orElse(Weight.of(10)).forGetter(generator -> generator.weight)
    ).apply(instance, FolderInFolderGenerator::new));

    protected final FolderGenerator contentGenerator;

    public FolderInFolderGenerator(StringGenerator nameGenerator, FolderGenerator contentGenerator, Weight weight) {
        super(File.Type.FOLDER, nameGenerator, StringGenerator.empty(), weight);
        this.contentGenerator = contentGenerator;
    }

    @Override
    public void generate(RandomSource random, DiscData data, Folder folder) {
        Folder newFolder = new Folder(data::markModified);
        contentGenerator.generate(random, data, newFolder);
        folder.folders.put(nameGenerator.generate(random), newFolder);
    }
}
