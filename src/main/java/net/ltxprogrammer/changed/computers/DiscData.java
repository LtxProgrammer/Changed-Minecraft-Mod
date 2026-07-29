package net.ltxprogrammer.changed.computers;

import com.mojang.datafixers.util.Either;
import net.ltxprogrammer.changed.init.ChangedApplications;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Optional;

public class DiscData {
    protected boolean modified = false;
    protected String name;
    protected final Folder rootFolder;
    protected final Runnable modifiedListener;

    public DiscData(Runnable modifiedListener) {
        this.modifiedListener = modifiedListener;

        name = "New Disk";
        rootFolder = createFolder();
    }

    public DiscData(CompoundTag tag, Runnable modifiedListener) {
        this.modifiedListener = modifiedListener;

        name = tag.getString("n");
        rootFolder = new Folder(tag.getCompound("r"), this::markModified);
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

    public @NotNull String getName() {
        return name;
    }

    public Either<File, File.Error> getFile(Path path) {
        return rootFolder.getFile(path);
    }

    public Either<File, File.Error> createFile(Path path, File.Type type) {
        return rootFolder.createFile(path, type);
    }

    public @Nullable Folder getFolder(Path path) {
        return rootFolder.getFolder(path);
    }

    public Optional<Folder> getFolderSafe(Path path) {
        return Optional.ofNullable(rootFolder.getFolder(path));
    }

    protected Folder createFolder() {
        return new Folder(this::markModified);
    }

    protected File createFile(File.Type type, String content) {
        return new File(type, content, this::markModified);
    }

    public static Path generatePCFileSystem(DiscData data, RandomSource random) {
        data.name = "C:";
        data.rootFolder.folders.put("OperatingSystem", data.createFolder()
                .addFile("krnl.bin", data.createFile(File.Type.DATA, ""))
                .addFile("resources.dat", data.createFile(File.Type.DATA, "")));
        data.rootFolder.folders.put("Binaries", data.createFolder()
                .addFile("explorer.app", data.createFile(File.Type.APP, ChangedApplications.FILE_EXPLORER.getId().toString())));
        data.rootFolder.folders.put("Users", Util.make(data.createFolder(), usersFolder -> {
            usersFolder.folders.put("TSCUser", Util.make(data.createFolder(), userFolder -> {
                userFolder.folders.put("Desktop", data.createFolder()
                        .addFile("lore2.txt", data.createFile(File.Type.TEXT, "I'm a lore2 thing")));
                userFolder.folders.put("Documents", data.createFolder()
                        .addFile("lore.txt", data.createFile(File.Type.TEXT, "I'm a lore thing"))
                        .addFile("dl_wolf.rcp", data.createFile(File.Type.RECIPE, "changed:form_dark_latex_wolf")));
            }));
        }));
        return Path.of("C:/Users/TSCUser/");
    }

    public void markModified() {
        if (!this.modified) {
            this.modified = true;
            if (modifiedListener != null) {
                modifiedListener.run();
            }
        }
    }

    public void clearModified() {
        this.modified = false;
    }

    public boolean isModified() {
        return modified;
    }
}
