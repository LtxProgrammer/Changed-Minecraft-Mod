package net.ltxprogrammer.changed.computers;

import net.ltxprogrammer.changed.init.ChangedApplications;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Optional;

public class DiscData {
    protected String name;
    protected final Folder rootFolder;

    public DiscData() {
        name = "New Disk";
        rootFolder = new Folder();
    }

    public DiscData(CompoundTag tag) {
        name = tag.getString("n");
        rootFolder = new Folder(tag.getCompound("r"));
    }

    public CompoundTag serialize() {
        var tag = new CompoundTag();
        tag.putString("n", name);
        tag.put("r", rootFolder.serialize());
        return tag;
    }

    public static @NotNull String getName(@Nullable CompoundTag tag) {
        return tag != null ? tag.getString("n") : "";
    }

    public @Nullable File getFile(Path path) {
        return rootFolder.getFile(path);
    }

    public Optional<File> getFileSafe(Path path) {
        return Optional.ofNullable(rootFolder.getFile(path));
    }

    public @Nullable Folder getFolder(Path path) {
        return rootFolder.getFolder(path);
    }

    public Optional<Folder> getFolderSafe(Path path) {
        return Optional.ofNullable(rootFolder.getFolder(path));
    }

    public static Path generatePCFileSystem(DiscData data, RandomSource random) {
        data.name = "C:";
        data.rootFolder.folders.put("OperatingSystem", new Folder()
                .addFile("krnl.bin", new File(File.Type.DATA, ""))
                .addFile("resources.dat", new File(File.Type.DATA, "")));
        data.rootFolder.folders.put("Binaries", new Folder()
                .addFile("explorer.app", new File(File.Type.APP, ChangedApplications.FILE_EXPLORER.getId().toString())));
        data.rootFolder.folders.put("Users", Util.make(new Folder(), usersFolder -> {
            usersFolder.folders.put("TSCUser", Util.make(new Folder(), userFolder -> {
                userFolder.folders.put("Desktop", new Folder()
                        .addFile("lore2.txt", new File(File.Type.TEXT, "I'm a lore2 thing")));
                userFolder.folders.put("Documents", new Folder()
                        .addFile("lore.txt", new File(File.Type.TEXT, "I'm a lore thing"))
                        .addFile("dl_wolf.rcp", new File(File.Type.RECIPE, "changed:form_dark_latex_wolf")));
            }));
        }));
        return Path.of("C:/Users/TSCUser/");
    }
}
