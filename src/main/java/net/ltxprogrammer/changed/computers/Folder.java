package net.ltxprogrammer.changed.computers;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;

import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Folder {
    public Map<String, Folder> folders = new HashMap<>();
    public Map<String, File> files = new HashMap<>();

    public Folder() {}

    public Folder(Map<String, Folder> folders, Map<String, File> files) {
        this.folders.putAll(folders);
        this.files.putAll(files);
    }

    public Folder(CompoundTag tag) {
        tag.getAllKeys().forEach(key -> {
            var instance = tag.getCompound(key);
            if (instance.contains("//folders"))
                folders.put(key, new Folder(instance.getCompound("//folders")));
            else
                files.put(key, new File(instance));
        });
    }

    public @Nullable File getFile(Path path) {
        var it = path.iterator();
        if (!it.hasNext())
            return null;
        Path p = it.next();
        String rep = p.toString();
        if (files.containsKey(rep))
            return files.get(rep);
        if (folders.containsKey(rep))
            return folders.get(rep).getFile(path.relativize(p));
        return null;
    }

    public @Nullable Folder getFolder(Path path) {
        var it = path.iterator();
        if (!it.hasNext())
            return this;
        Path p = it.next();
        String rep = p.toString();
        if (folders.containsKey(rep))
            return folders.get(rep).getFolder(path.relativize(p));
        return null;
    }
}
