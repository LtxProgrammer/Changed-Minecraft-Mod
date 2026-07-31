package net.ltxprogrammer.changed.computers.generator;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.computers.DiscData;
import net.ltxprogrammer.changed.computers.Folder;
import net.minecraft.util.RandomSource;

import java.util.Collection;
import java.util.List;

public class FolderGenerator {
    public static final Codec<FolderGenerator> CODEC = FilePool.CODEC.listOf().xmap(
            FolderGenerator::new,
            generator -> List.copyOf(generator.filePools)
    );

    protected final Collection<FilePool> filePools;

    public FolderGenerator(Collection<FilePool> filePools) {
        this.filePools = filePools;
    }

    public Folder generate(RandomSource random, DiscData data, Folder folder) {
        filePools.forEach(filePool -> {
            filePool.generate(random, data, folder);
        });
        return folder;
    }
}
