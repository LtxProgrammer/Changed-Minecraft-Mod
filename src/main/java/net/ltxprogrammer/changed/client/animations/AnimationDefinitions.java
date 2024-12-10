package net.ltxprogrammer.changed.client.animations;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.util.ResourceUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

public class AnimationDefinitions extends SimplePreparableReloadListener<ImmutableMap<ResourceLocation, AnimationDefinition>> {
    public static AnimationDefinitions INSTANCE = new AnimationDefinitions();

    private static ImmutableMap<ResourceLocation, AnimationDefinition> definitions;

    public static AnimationDefinition getAnimation(ResourceLocation id) {
        return definitions.get(id);
    }

    public static class GatherAnimationsEvent extends Event {
        private final ImmutableMap.Builder<ResourceLocation, AnimationDefinition> builder;

        public GatherAnimationsEvent(ImmutableMap.Builder<ResourceLocation, AnimationDefinition> builder) {
            this.builder = builder;
        }

        public void addAnimationDefinition(ResourceLocation id, AnimationDefinition definition) {
            builder.put(id, definition);
        }
    }

    private AnimationDefinition processJSONFile(JsonObject root) {
        return AnimationDefinition.CODEC.parse(JsonOps.INSTANCE, root)
                .getOrThrow(false, string -> {});
    }

    @Override
    @NotNull
    protected ImmutableMap<ResourceLocation, AnimationDefinition> prepare(@NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        final ImmutableMap.Builder<ResourceLocation, AnimationDefinition> builder = ResourceUtil.processJSONResources(new ImmutableMap.Builder<>(),
                resourceManager, "animation_definitions",
                (map, filename, id, json) -> map.put(id, processJSONFile(json)),
                (exception, filename) -> Changed.LOGGER.error("Failed to load animation definition from \"{}\" : {}", filename, exception));

        Changed.postModEvent(new GatherAnimationsEvent(builder));

        return builder.build();
    }

    @Override
    protected void apply(@NotNull ImmutableMap<ResourceLocation, AnimationDefinition> output, @NotNull ResourceManager resources, @NotNull ProfilerFiller profiler) {
        definitions = output;
    }
}
