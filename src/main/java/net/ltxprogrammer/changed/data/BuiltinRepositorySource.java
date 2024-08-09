package net.ltxprogrammer.changed.data;

import net.minecraft.Util;
import net.minecraft.server.packs.FolderPackResources;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraftforge.fml.loading.FMLLoader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class BuiltinRepositorySource implements RepositorySource {
    private final String modId;

    private final Path modFile;
    private final boolean isJar;
    private final Set<String> packIds = new HashSet<>();
    private final String packsFolder;
    private static final EnumMap<PackType, String> NAMED_FOLDERS = Util.make(new EnumMap<>(PackType.class), map -> {
        map.put(PackType.CLIENT_RESOURCES, "resourcepacks");
        map.put(PackType.SERVER_DATA, "datapacks");
    });
    private static final String MCMETA = "pack.mcmeta";

    public BuiltinRepositorySource(PackType type, String modId) throws IOException, NullPointerException {
        this.modId = modId;
        this.modFile = FMLLoader.getLoadingModList().getModFileById(modId).getFile().getFilePath();
        this.packsFolder = NAMED_FOLDERS.getOrDefault(type, type.getDirectory());
        var file = this.modFile.toFile();
        if (file.isDirectory()) {
            this.isJar = false;
            Files.walk(this.modFile.resolve(packsFolder), 1).filter(path -> {
                return path.resolve(MCMETA).toFile().isFile();
            }).forEach(path -> packIds.add(path.getFileName().toString()));
        }

        else if (file.isFile()) { // Check jar file
            this.isJar = true;
            ZipFile jar = new ZipFile(this.modFile.toFile());
            jar.stream().filter(ZipEntry::isDirectory).filter(entry -> {
                return entry.getName().startsWith(packsFolder + "/") &&
                        jar.getEntry(entry.getName() + MCMETA) != null;
            }).forEach(entry -> packIds.add(new File(entry.getName()).getName()));
            jar.close();
        }

        else
            throw new IOException("Invalid mod format");
    }

    @Override
    public void loadPacks(Consumer<Pack> out, Pack.PackConstructor constructor) {
        for(String id : packIds) {
            Pack pack = Pack.create(modId + ":" + id, false,
                    this.createSupplier(this.modFile.toFile(), id), constructor, Pack.Position.TOP, PackSource.BUILT_IN);
            if (pack instanceof PackExtender ext)
                ext.setIncludeByDefault(false);
            if (pack != null) {
                out.accept(pack);
            }
        }
    }

    private Supplier<PackResources> createSupplier(File file, String packName) {
        return isJar ? () -> {
            return new BuiltinPackResources(file, packsFolder + "/" + packName + "/");
        } : () -> {
            return new FolderPackResources(new File(file, packsFolder + "/" + packName));
        };
    }
}
