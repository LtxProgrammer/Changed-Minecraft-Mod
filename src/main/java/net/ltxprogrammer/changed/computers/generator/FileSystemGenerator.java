package net.ltxprogrammer.changed.computers.generator;

import com.mojang.serialization.Codec;
import net.ltxprogrammer.changed.computers.DiscData;
import net.ltxprogrammer.changed.computers.RecognizedDirectory;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.minecraft.util.RandomSource;

import java.nio.file.Path;
import java.util.function.Function;

public interface FileSystemGenerator {
    Codec<FileSystemGenerator> TOP_CODEC = ChangedRegistry.FILE_SYSTEM_GENERATORS.get().getCodec().dispatch("type",
            FileSystemGenerator::getCodec, Function.identity());

    interface DirectoryConsumer {
        void accept(RecognizedDirectory label, Path path);
    }

    void generate(RandomSource random, DiscData data, DirectoryConsumer consumer);
    Codec<? extends FileSystemGenerator> getCodec();

    default void generate(RandomSource random, DiscData data) {
        this.generate(random, data, ($1, $2) -> {});
    }
}
