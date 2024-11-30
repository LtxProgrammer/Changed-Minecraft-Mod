package net.ltxprogrammer.changed.computers;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class DiscData {
    protected String name;
    protected Folder rootFolder;

    public DiscData() {
        name = "New Disk";
        rootFolder = new Folder();
    }

    public DiscData(CompoundTag tag) {
        name = tag.getString("n");
        rootFolder = new Folder(tag.getCompound("r"));
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
}
