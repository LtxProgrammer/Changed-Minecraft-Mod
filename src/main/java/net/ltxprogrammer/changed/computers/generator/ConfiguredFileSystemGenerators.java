package net.ltxprogrammer.changed.computers.generator;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.ltxprogrammer.changed.util.ResourceUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import javax.annotation.Nonnull;
import java.util.*;

public class ConfiguredFileSystemGenerators extends SimplePreparableReloadListener<Map<ResourceLocation, FileSystemGenerator>> {
    public static ConfiguredFileSystemGenerators INSTANCE = new ConfiguredFileSystemGenerators();
    private static final Logger LOGGER = LogUtils.getLogger();

    private ConfiguredFileSystemGenerators() {}

    private final Map<ResourceLocation, FileSystemGenerator> fileSystemGenerators = new HashMap<>();

    private FileSystemGenerator processJSONFile(JsonObject root) {
        return FileSystemGenerator.TOP_CODEC.decode(JsonOps.INSTANCE, root)
                .getOrThrow(false, error -> { throw new RuntimeException(error); }).getFirst();
    }

    @Override
    @NotNull
    public Map<ResourceLocation, FileSystemGenerator> prepare(ResourceManager resources, @Nonnull ProfilerFiller profiler) {
        return ResourceUtil.processJSONResources(new HashMap<>(), resources, "file_system_generators", (list, filename, id, json) -> {
            list.put(id, processJSONFile(json));
        }, (exception, filename) -> LOGGER.error("Failed to load file system generator from \"{}\" : {}", filename, exception));
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, FileSystemGenerator> output, @NotNull ResourceManager resources, @NotNull ProfilerFiller profiler) {
        fileSystemGenerators.clear();
        fileSystemGenerators.putAll(output);
    }

    @Nullable
    public FileSystemGenerator getFileSystemGenerator(ResourceLocation id) {
        return this.fileSystemGenerators.get(id);
    }

    @Nullable
    public static FileSystemGenerator getGenerator(ResourceLocation id) {
        return INSTANCE.getFileSystemGenerator(id);
    }
}
