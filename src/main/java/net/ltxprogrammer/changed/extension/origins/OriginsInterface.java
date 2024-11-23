package net.ltxprogrammer.changed.extension.origins;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.common.registry.OriginRegisters;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * This class should not be loaded if origins is not present.
 */
public class OriginsInterface extends SimplePreparableReloadListener<Multimap<TagKey<Origin>, ResourceLocation>> {
    public static final OriginsInterface INSTANCE = new OriginsInterface();

    private OriginsInterface() {}

    public static TagKey<Origin> TRANSFURABLE = OriginRegisters.ORIGINS.createTagKey(Changed.modResource("transfurable"));

    private static final Multimap<TagKey<Origin>, ResourceLocation> namedTags = HashMultimap.create();
    private static final Multimap<TagKey<Origin>, Origin> localTags = HashMultimap.create();

    private static void convertTags(Registry<Origin> registry) {
        namedTags.forEach((tagKey, id) -> localTags.put(tagKey, registry.get(id)));
        namedTags.clear();
    }

    private static boolean isOriginTagged(Registry<Origin> registry, TagKey<Origin> tagKey, Origin origin) {
        convertTags(registry);
        return localTags.get(tagKey).contains(origin);
    }

    public static boolean doesPlayerHaveAnyOrigins(ServerPlayer player) {
        final MinecraftServer server = player.getServer();
        if (server == null)
            return false;

        Registry<Origin> registry = OriginsAPI.getOriginsRegistry(server);
        return IOriginContainer.get(player)
                .map(container -> !container.getOrigins().isEmpty()).orElse(false);
    }

    public static boolean isPlayerOrigin(ServerPlayer player, TagKey<Origin> tag) {
        final MinecraftServer server = player.getServer();
        if (server == null)
            return false;

        Registry<Origin> registry = OriginsAPI.getOriginsRegistry(server);
        return IOriginContainer.get(player)
                .map(container ->
                        container.getOrigins().values().stream().anyMatch(origin -> isOriginTagged(registry, tag, origin))
                ).orElse(false);
    }

    public static boolean isPlayerOrigin(ServerPlayer player, ResourceLocation originName) {
        final MinecraftServer server = player.getServer();
        if (server == null)
            return false;

        Origin origin = Objects.requireNonNull(OriginsAPI.getOriginsRegistry(server).get(originName));
        return IOriginContainer.get(player)
                .map(container ->
                        container.getOrigins().values().stream().anyMatch(origin::equals))
                .orElse(false);
    }

    private void processJSONFile(JsonObject root, Consumer<ResourceLocation> listed) {
        root.getAsJsonArray("values").forEach(element -> {
            listed.accept(new ResourceLocation(element.getAsString()));
        });
    }

    @Override
    protected Multimap<TagKey<Origin>, ResourceLocation> prepare(ResourceManager resources, ProfilerFiller profiler) {
        final var entries = resources.listResources("tags/origins/origin", filename ->
                ResourceLocation.isValidResourceLocation(filename) &&
                        filename.endsWith(".json"));

        Multimap<TagKey<Origin>, ResourceLocation> working = HashMultimap.create();

        entries.forEach(filename -> {
            try {
                final String id = Path.of(filename.getPath()).getFileName().toString().replace(".json", "");
                final Resource content = resources.getResource(filename);

                try {
                    final Reader reader = new InputStreamReader(content.getInputStream(), StandardCharsets.UTF_8);
                    final ResourceLocation tagName = new ResourceLocation(filename.getNamespace(), id);
                    final TagKey<Origin> tagKey = OriginRegisters.ORIGINS.createTagKey(tagName);

                    processJSONFile(JsonParser.parseReader(reader).getAsJsonObject(), origin -> {
                        working.put(tagKey, origin);
                    });

                    reader.close();
                } catch (Exception e) {
                    content.close();
                    throw e;
                }

                content.close();
            } catch (Exception e) {
                Changed.LOGGER.error("Failed to load origin tag from \"{}\"", filename);
            }
        });

        return working;
    }

    @Override
    protected void apply(Multimap<TagKey<Origin>, ResourceLocation> map, ResourceManager resources, ProfilerFiller profiler) {
        namedTags.clear();
        namedTags.putAll(map);
        localTags.clear();
    }
}
