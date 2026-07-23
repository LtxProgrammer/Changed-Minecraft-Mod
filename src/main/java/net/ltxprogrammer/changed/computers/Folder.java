package net.ltxprogrammer.changed.computers;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.HashMap;
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

    public @Nullable File getFile(Path path) {
        var it = path.iterator();
        if (!it.hasNext())
            return null;
        Path p = it.next();
        String rep = p.toString();
        if (rep.isEmpty())
            return null;
        if (files.containsKey(rep))
            return files.get(rep);
        if (folders.containsKey(rep))
            return folders.get(rep).getFile(p.relativize(path));
        return null;
    }

    public @Nullable Folder getFolder(Path path) {
        var it = path.iterator();
        if (!it.hasNext())
            return this;
        Path p = it.next();
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
