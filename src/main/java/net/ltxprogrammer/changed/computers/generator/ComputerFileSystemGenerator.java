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

public class ComputerFileSystemGenerator implements FileSystemGenerator {
    public static final Codec<ComputerFileSystemGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            StringGenerator.CODEC.fieldOf("diskName").forGetter(generator -> generator.diskName),
            StringGenerator.CODEC.fieldOf("username").forGetter(generator -> generator.username),
            FolderGenerator.CODEC.fieldOf("desktop").forGetter(generator -> generator.desktopGenerator),
            FolderGenerator.CODEC.fieldOf("documents").forGetter(generator -> generator.documentsGenerator)
    ).apply(instance, ComputerFileSystemGenerator::new));

    protected final StringGenerator diskName;
    protected final StringGenerator username;
    protected final FolderGenerator desktopGenerator;
    protected final FolderGenerator documentsGenerator;

    public ComputerFileSystemGenerator(StringGenerator diskName, StringGenerator username, FolderGenerator desktopGenerator, FolderGenerator documentsGenerator) {
        this.diskName = diskName;
        this.username = username;
        this.desktopGenerator = desktopGenerator;
        this.documentsGenerator = documentsGenerator;
    }

    @Override
    public void generate(RandomSource random, DiscData data, DirectoryConsumer consumer) {
        var resolvedUsername = username.generate(random);

        data.setName(diskName.generate(random));
        data.getRootFolder().folders.put("OperatingSystem", new Folder(data::markModified)
                .addFile("krnl.bin", new File(File.Type.DATA, "", data::markModified))
                .addFile("resources.dat", new File(File.Type.DATA, "", data::markModified)));
        data.getRootFolder().folders.put("Binaries", new Folder(data::markModified)
                .addFile("explorer.app", new File(File.Type.APP, ChangedApplications.FILE_EXPLORER.getId().toString(), data::markModified)));
        data.getRootFolder().folders.put("Users", Util.make(new Folder(data::markModified), usersFolder -> {
            usersFolder.folders.put(resolvedUsername, Util.make(new Folder(data::markModified), userFolder -> {
                userFolder.folders.put("Desktop", desktopGenerator.generate(random, data, new Folder(data::markModified)));
                userFolder.folders.put("Documents", documentsGenerator.generate(random, data, new Folder(data::markModified)));
            }));
        }));

        consumer.accept(RecognizedDirectory.HOME_DIR, Path.of("/Users", resolvedUsername));
        consumer.accept(RecognizedDirectory.BIN_DIR, Path.of("/Binaries"));
    }

    @Override
    public Codec<? extends FileSystemGenerator> getCodec() {
        return CODEC;
    }
}
