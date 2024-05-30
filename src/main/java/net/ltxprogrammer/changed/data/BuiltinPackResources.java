package net.ltxprogrammer.changed.data;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.ResourcePackFileNotFoundException;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class BuiltinPackResources extends AbstractPackResources {
    private final File file;
    private final String prefix;
    private final Splitter splitter;
    private final int prefixDirCount;

    @Nullable
    private ZipFile zipFile;

    public BuiltinPackResources(File file, String prefix) {
        super(new File(file, prefix));
        this.file = file;
        this.prefix = prefix;
        this.prefixDirCount = Path.of(prefix).getNameCount();
        this.splitter = Splitter.on('/').omitEmptyStrings().limit(3 + Path.of(prefix).getNameCount());
    }

    private ZipFile getOrCreateZipFile() throws IOException {
        if (this.zipFile == null) {
            this.zipFile = new ZipFile(this.file);
        }

        return this.zipFile;
    }

    protected InputStream getResource(String name) throws IOException {
        ZipFile zipfile = this.getOrCreateZipFile();
        ZipEntry zipentry = zipfile.getEntry(prefix + name);
        if (zipentry == null) {
            throw new ResourcePackFileNotFoundException(this.file, name);
        } else {
            return zipfile.getInputStream(zipentry);
        }
    }

    public boolean hasResource(String name) {
        try {
            return this.getOrCreateZipFile().getEntry(prefix + name) != null;
        } catch (IOException ioexception) {
            return false;
        }
    }

    public Set<String> getNamespaces(PackType type) {
        ZipFile zipfile;
        try {
            zipfile = this.getOrCreateZipFile();
        } catch (IOException ioexception) {
            return Collections.emptySet();
        }

        Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
        Set<String> set = Sets.newHashSet();

        while(enumeration.hasMoreElements()) {
            ZipEntry zipentry = enumeration.nextElement();
            String s = zipentry.getName();
            if (s.startsWith(prefix + type.getDirectory() + "/")) {
                List<String> list = Lists.newArrayList(splitter.split(s));
                if (list.size() > 1 + prefixDirCount) {
                    String s1 = list.get(1 + prefixDirCount);
                    if (s1.equals(s1.toLowerCase(Locale.ROOT))) {
                        set.add(s1);
                    } else {
                        this.logWarning(s1);
                    }
                }
            }
        }

        return set;
    }

    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    public void close() {
        if (this.zipFile != null) {
            IOUtils.closeQuietly((Closeable)this.zipFile);
            this.zipFile = null;
        }

    }

    public Collection<ResourceLocation> getResources(PackType type, String namespace, String path, int maxDepth, Predicate<String> filter) {
        ZipFile zipfile;
        try {
            zipfile = this.getOrCreateZipFile();
        } catch (IOException ioexception) {
            return Collections.emptySet();
        }

        Enumeration<? extends ZipEntry> enumeration = zipfile.entries();
        List<ResourceLocation> list = Lists.newArrayList();
        String s = prefix + type.getDirectory() + "/" + namespace + "/";
        String s1 = s + path + "/";

        while(enumeration.hasMoreElements()) {
            ZipEntry zipentry = enumeration.nextElement();
            if (!zipentry.isDirectory()) {
                String s2 = zipentry.getName();
                if (!s2.startsWith(prefix))
                    continue;

                if (!s2.endsWith(".mcmeta") && s2.startsWith(s1)) {
                    String s3 = s2.substring(s.length());
                    String[] astring = s3.split("/");
                    if (astring.length >= maxDepth + 1 && filter.test(astring[astring.length - 1])) {
                        list.add(new ResourceLocation(namespace, s3));
                    }
                }
            }
        }

        return list;
    }
}
