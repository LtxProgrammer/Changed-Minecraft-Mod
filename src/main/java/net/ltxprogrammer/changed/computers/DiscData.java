package net.ltxprogrammer.changed.computers;

import com.mojang.datafixers.util.Either;
import net.ltxprogrammer.changed.computers.generator.ConfiguredFileSystemGenerators;
import net.ltxprogrammer.changed.computers.generator.FileSystemGenerator;
import net.ltxprogrammer.changed.util.TagUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.Optional;

public class DiscData {
    protected boolean modified = false;
    protected String name;
    protected @Nullable ResourceLocation generatorId;
    protected long generatorSeed;
    protected final Folder rootFolder;
    protected final Runnable modifiedListener;

    protected Permissions permissions = Permissions.READ_WRITE;

    public DiscData(Runnable modifiedListener) {
        this.modifiedListener = modifiedListener;

        name = "New Disk";
        generatorId = null;
        rootFolder = createFolder();
    }

    public static DiscData copyOf(Folder folder, Runnable modifiedListener) {
        return new DiscData(
                new Folder(folder.serialize(), modifiedListener),
                modifiedListener
        );
    }

    private DiscData(Folder rootFolder, Runnable modifiedListener) {
        this.modifiedListener = modifiedListener;

        name = "New Disk";
        generatorId = null;
        this.rootFolder = rootFolder;
    }

    public DiscData(CompoundTag tag, Runnable modifiedListener) {
        this.modifiedListener = modifiedListener;

        if (!this.tryLoadGenerator(tag)) {
            name = tag.getString("n");
            rootFolder = new Folder(tag.getCompound("r"), this::markModified);
        } else {
            rootFolder = createFolder();
        }

        permissions = tag.contains("p") ? Permissions.fromByte(tag.getByte("p")) : Permissions.READ_WRITE;
    }

    protected boolean tryLoadGenerator(CompoundTag tag) {
        if (tag.contains("generator")) {
            generatorId = TagUtil.getResourceLocation(tag, "generator");
            generatorSeed = tag.getLong("generatorSeed");
            return true;
        }

        return false;
    }

    public CompoundTag serialize() {
        var tag = new CompoundTag();
        if (generatorId != null) {
            TagUtil.putResourceLocation(tag, "generator", generatorId);
            tag.putLong("generatorSeed", generatorSeed);
        } else {
            tag.putString("n", name);
            tag.put("r", rootFolder.serialize());
        }
        tag.putByte("p", permissions.toByte());
        return tag;
    }

    public DiscData generateIfNecessary(FileSystemGenerator.DirectoryConsumer consumer) {
        if (generatorId != null) {
            var generator = ConfiguredFileSystemGenerators.getGenerator(generatorId);
            generatorId = null;

            if (generator != null)
                generator.generate(RandomSource.create(generatorSeed), this, consumer);

            this.markModified();
        }

        return this;
    }

    public Folder getRootFolder() {
        return rootFolder;
    }

    public static @NotNull String getName(@Nullable CompoundTag tag) {
        return tag != null ? tag.getString("n") : "";
    }

    public @NotNull String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
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
