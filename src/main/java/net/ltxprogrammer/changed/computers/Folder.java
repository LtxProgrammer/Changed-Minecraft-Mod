package net.ltxprogrammer.changed.computers;

import com.mojang.datafixers.util.Either;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;
import java.util.Map;

public class Folder {
    protected final Runnable markModified;
    public Map<String, Folder> folders = new Object2ObjectArrayMap<>();
    public Map<String, File> files = new Object2ObjectArrayMap<>();

    public Folder(Runnable markModified) {
        this.markModified = markModified;
    }

    public Folder(Map<String, Folder> folders, Map<String, File> files, Runnable markModified) {
        this.folders.putAll(folders);
        this.files.putAll(files);
        this.markModified = markModified;
    }

    public Folder(CompoundTag tag, Runnable markModified) {
        tag.getAllKeys().forEach(key -> {
            var instance = tag.getCompound(key);
            if (instance.contains("//folders"))
                folders.put(key, new Folder(instance.getCompound("//folders"), markModified));
            else
                files.put(key, new File(instance, markModified));
        });
        this.markModified = markModified;
    }

    public CompoundTag serialize() {
        var tag = new CompoundTag();
        folders.forEach((name, subFolder) -> {
            var folderTag = new CompoundTag();
            folderTag.put("//folders", subFolder.serialize());
            tag.put(name, folderTag);
        });
        files.forEach((name, file) -> {
            tag.put(name, file.serialize());
        });
        return tag;
    }

    public Either<File, File.Error> getFile(LexicalPath path) {
        var it = path.iterator();
        if (!it.hasNext())
            return Either.right(File.Error.INVALID_PATH);
        LexicalPath p = it.next();
        String rep = p.toString();
        if (rep.isEmpty())
            return Either.right(File.Error.NO_READ_PERMISSION);
        if (!it.hasNext() && files.containsKey(rep))
            return Either.left(files.get(rep));
        if (folders.containsKey(rep))
            return folders.get(rep).getFile(p.relativize(path));
        return Either.right(File.Error.FILE_NOT_FOUND);
    }

    public Either<File, File.Error> createFile(LexicalPath path, File.Type type) {
        var it = path.iterator();
        if (!it.hasNext())
            return Either.right(File.Error.INVALID_PATH);
        LexicalPath p = it.next();
        String rep = p.toString();
        if (rep.isEmpty())
            return Either.right(File.Error.NO_WRITE_PERMISSION);
        if (!it.hasNext() && files.containsKey(rep))
            return Either.right(File.Error.FILE_ALREADY_EXISTS);
        if (!it.hasNext()) {
            var f = new File(type, "", this.markModified);
            files.put(rep, f);
            return Either.left(f);
        }
        if (folders.containsKey(rep))
            return folders.get(rep).createFile(p.relativize(path), type);
        return Either.right(File.Error.FILE_NOT_FOUND);
    }

    public @Nullable Folder getFolder(LexicalPath path) {
        var it = path.iterator();
        if (!it.hasNext())
            return this;
        LexicalPath p = it.next();
        String rep = p.toString();
        if (rep.isEmpty())
            return this;
        if (folders.containsKey(rep))
            return folders.get(rep).getFolder(p.relativize(path));
        return null;
    }

    public Folder addFile(String fileName, File file) {
        this.files.put(fileName, file);
        return this;
    }
}
