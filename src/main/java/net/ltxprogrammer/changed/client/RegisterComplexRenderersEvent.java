package net.ltxprogrammer.changed.client;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.entity.ComplexRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;

import java.util.HashMap;
import java.util.Map;

public class RegisterComplexRenderersEvent extends Event implements IModBusEvent {
    private final Map<EntityType<? extends ComplexRenderer>,
            ImmutableMap.Builder<String, EntityRendererProvider<? extends ComplexRenderer>>> builder = new HashMap<>();

    public <T extends Entity & ComplexRenderer> void registerDefaultEntityRenderer(EntityType<T> entityType, EntityRendererProvider<T> provider) {
        registerEntityRenderer(entityType, "default", provider);
    }

    public <T extends Entity & ComplexRenderer> void registerEntityRenderer(EntityType<T> entityType, String modelName, EntityRendererProvider<T> provider) {
        builder.computeIfAbsent(entityType, type -> new ImmutableMap.Builder<>()).put(modelName, provider);
    }

    public <T extends Entity & ComplexRenderer> void registerEntityRenderer(EntityType<T> entityType, Map<String, EntityRendererProvider<T>> providers) {
        final var mapBuilder = builder.computeIfAbsent(entityType, type -> new ImmutableMap.Builder<>());
        providers.forEach(mapBuilder::put);
    }

    public Map<EntityType<? extends ComplexRenderer>, Map<String, EntityRenderer<? extends ComplexRenderer>>> build(EntityRendererProvider.Context context) {
        final var finalized = new ImmutableMap.Builder<EntityType<? extends ComplexRenderer>, Map<String, EntityRenderer<? extends ComplexRenderer>>>();
        builder.forEach((type, builder) -> {
            final var renderers = new ImmutableMap.Builder<String, EntityRenderer<? extends ComplexRenderer>>();
            builder.buildOrThrow().entrySet().stream().map(entry -> Pair.of(entry.getKey(), entry.getValue().create(context))).forEach(entry -> {
                renderers.put(entry.getFirst(), entry.getSecond());
            });

            finalized.put(type, renderers.buildOrThrow());
        });
        return finalized.buildOrThrow();
    }
}
