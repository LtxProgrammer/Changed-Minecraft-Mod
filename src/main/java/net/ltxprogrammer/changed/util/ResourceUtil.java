package net.ltxprogrammer.changed.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class ResourceUtil {
    public interface ResourceConsumer<T> {
        void accept(T builder, @NotNull ResourceManager resources, ResourceLocation fullResourceName, ResourceLocation registryName);
    }

    public interface JSONResourceConsumer<T> {
        void accept(T builder, ResourceLocation fullResourceName, ResourceLocation registryName, JsonObject root) throws Exception;
    }

    public interface JSONFileConsumer<T> {
        void accept(T builder, ResourceLocation fullResourceName, JsonObject root) throws Exception;
    }

    public static <T> T processResources(T builder, @NotNull ResourceManager resources, @NotNull String path, @NotNull String extension, ResourceConsumer<T> consumer) {
        resources.listResources(path, filename -> ResourceLocation.isValidResourceLocation(filename) && filename.endsWith(extension))
                .forEach(filename -> {
                    ResourceLocation registryName = new ResourceLocation(filename.getNamespace(),
                            Path.of(path).relativize(Path.of(filename.getPath())).toString()
                                    .replace(extension, "")
                                    .replace('\\', '/'));

                    consumer.accept(builder, resources, filename, registryName);
                });

        return builder;
    }

    public static <T> T processJSONResources(T builder, @NotNull ResourceManager resources, @NotNull String path, JSONResourceConsumer<T> consumer, BiConsumer<Exception, ResourceLocation> onException) {
        resources.listResources(path, filename -> ResourceLocation.isValidResourceLocation(filename) && filename.endsWith(".json"))
                .forEach(filename -> {
                    ResourceLocation registryName = new ResourceLocation(filename.getNamespace(),
                            Path.of(path).relativize(Path.of(filename.getPath())).toString()
                                    .replace(".json", "")
                                    .replace('\\', '/'));


                    try {
                        final Resource content = resources.getResource(filename);

                        try {
                            final Reader reader = new InputStreamReader(content.getInputStream(), StandardCharsets.UTF_8);

                            consumer.accept(builder, filename, registryName, JsonParser.parseReader(reader).getAsJsonObject());

                            reader.close();
                        } catch (Exception e) {
                            content.close();
                            throw e;
                        }

                        content.close();
                    } catch (Exception e) {
                        onException.accept(e, filename);
                    }
                });

        return builder;
    }

    public static <T> T processJSONFiles(T builder, @NotNull ResourceManager resources, @NotNull String fullName, JSONFileConsumer<T> consumer, BiConsumer<Exception, ResourceLocation> onException) {
        resources.getNamespaces().stream().map(namespace -> new ResourceLocation(namespace, fullName))
                .filter(resources::hasResource)
                .forEach(filename -> {
                    try {
                        final Resource content = resources.getResource(filename);

                        try {
                            final Reader reader = new InputStreamReader(content.getInputStream(), StandardCharsets.UTF_8);

                            consumer.accept(builder, filename, JsonParser.parseReader(reader).getAsJsonObject());

                            reader.close();
                        } catch (Exception e) {
                            content.close();
                            throw e;
                        }

                        content.close();
                    } catch (Exception e) {
                        onException.accept(e, filename);
                    }
                });

        return builder;
    }
}
