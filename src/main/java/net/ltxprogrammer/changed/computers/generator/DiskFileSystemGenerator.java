package net.ltxprogrammer.changed.computers.generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.computers.DiscData;
import net.minecraft.util.RandomSource;

public class DiskFileSystemGenerator implements FileSystemGenerator {
    public static final Codec<DiskFileSystemGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StringGenerator.CODEC.fieldOf("diskName").forGetter(generator -> generator.diskName),
            FolderGenerator.CODEC.fieldOf("rootFolder").forGetter(generator -> generator.rootFolderGenerator)
    ).apply(instance, DiskFileSystemGenerator::new));

    protected final StringGenerator diskName;
    protected final FolderGenerator rootFolderGenerator;

    public DiskFileSystemGenerator(StringGenerator diskName, FolderGenerator rootFolderGenerator) {
        this.diskName = diskName;
        this.rootFolderGenerator = rootFolderGenerator;
    }

    @Override
    public void generate(RandomSource random, DiscData data, DirectoryConsumer consumer) {
        data.setName(diskName.generate(random));
        rootFolderGenerator.generate(random, data, data.getRootFolder());
    }

    @Override
    public Codec<? extends FileSystemGenerator> getCodec() {
        return CODEC;
    }
}
