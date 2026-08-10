package net.ltxprogrammer.changed.computers.generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.ltxprogrammer.changed.computers.DiscData;
import net.ltxprogrammer.changed.computers.File;
import net.ltxprogrammer.changed.computers.Folder;
import net.ltxprogrammer.changed.computers.RecognizedDirectory;
import net.ltxprogrammer.changed.init.ChangedApplications;
import net.minecraft.Util;
import net.minecraft.util.RandomSource;

import java.nio.file.Path;

public class ServerFileSystemGenerator implements FileSystemGenerator {
    public static final Codec<ServerFileSystemGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StringGenerator.CODEC.fieldOf("diskName").forGetter(generator -> generator.diskName),
            FolderGenerator.CODEC.fieldOf("documents").forGetter(generator -> generator.documentsGenerator)
    ).apply(instance, ServerFileSystemGenerator::new));

    protected final StringGenerator diskName;
    protected final FolderGenerator documentsGenerator;

    public ServerFileSystemGenerator(StringGenerator diskName, FolderGenerator documentsGenerator) {
        this.diskName = diskName;
        this.documentsGenerator = documentsGenerator;
    }

    @Override
    public void generate(RandomSource random, DiscData data, DirectoryConsumer consumer) {
        data.setName(diskName.generate(random));
        data.getRootFolder().folders.put("OperatingSystem", new Folder(data::markModified)
                .addFile("krnl.bin", new File(File.Type.DATA, "", data::markModified))
                .addFile("resources.dat", new File(File.Type.DATA, "", data::markModified)));
        data.getRootFolder().folders.put("Binaries", new Folder(data::markModified)
                /*.addFile("explorer.app", new File(File.Type.APP, ChangedApplications.FILE_EXPLORER.getId().toString(), data::markModified))*/);
        data.getRootFolder().folders.put("Documents", documentsGenerator.generate(random, data, new Folder(data::markModified)));

        consumer.accept(RecognizedDirectory.BIN_DIR, Path.of("/Binaries"));
    }

    @Override
    public Codec<? extends FileSystemGenerator> getCodec() {
        return CODEC;
    }
}
