package net.ltxprogrammer.changed.client;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.renderer.*;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@OnlyIn(Dist.CLIENT)
public final class RendererOverride {
    private static final Logger LOGGER = LogUtils.getLogger();

    private RendererOverride() {}

    private static final Map<Class<?>, List<ConditionalOverrideProvider<?>>> PROVIDERS = new Object2ObjectArrayMap<>();
    private static final Map<Class<?>, List<ConditionalOverride<?>>> RENDERERS = new Object2ObjectArrayMap<>();

    private record ConditionalOverride<T extends Entity>(int priority, Predicate<? super T> shouldOverride, EntityRenderer<? super T> entityRenderer, Optional<PlayerRenderer> asPlayerRenderer) {
        @SuppressWarnings("unchecked")
        EntityRenderer<? super T> getRenderer(T entity) {
            if (entity instanceof AbstractClientPlayer)
                return (EntityRenderer) asPlayerRenderer.orElseThrow();
            return entityRenderer;
        }
    }

    private record ConditionalOverrideProvider<T extends Entity>(int priority, Predicate<? super T> shouldOverride, EntityRendererProvider<? super T> entityRendererProvider) {
        @SuppressWarnings("unchecked")
        Optional<PlayerRenderer> createWrapper(Class<T> clazz, EntityRenderer<? super T> entityRenderer, EntityRendererProvider.Context context) {
            if (!clazz.isAssignableFrom(AbstractClientPlayer.class))
                return Optional.empty();
            if (entityRenderer instanceof PlayerRenderer playerRenderer)
                return Optional.of(playerRenderer);
            return Optional.of(new WrappedPlayerRenderer(context, (EntityRenderer) entityRenderer));
        }

        ConditionalOverride<T> create(Class<T> clazz, EntityRendererProvider.Context context) {
            var renderer = entityRendererProvider.create(context);
            return new ConditionalOverride<>(priority, shouldOverride, renderer, createWrapper(clazz, renderer, context));
        }
    }

    private static <T extends Entity> Stream<ConditionalOverride<?>> getOverridesForClass(Class<T> clazz) {
        return RENDERERS.entrySet().stream()
                .filter(entry -> entry.getKey().isAssignableFrom(clazz))
                .map(Map.Entry::getValue)
                .flatMap(List::stream);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Entity> Optional<EntityRenderer<? super T>> getRenderer(T entity) {
        var foundOverride = getOverridesForClass(entity.getClass())
                .sorted(Comparator.comparingInt(ConditionalOverride::priority))
                .filter(override -> ((ConditionalOverride) override).shouldOverride().test(entity))
                .findFirst();

        return foundOverride.map(override -> (EntityRenderer<? super T>) ((ConditionalOverride) override).getRenderer(entity));
    }

    @ApiStatus.Internal
    public static void gatherOverrides() {
        PROVIDERS.clear();
        RENDERERS.clear();

        RegisterEvent event = new RegisterEvent(PROVIDERS);
        addChangedRendererOverrides(event);
        Changed.postModLoadingEvent(event);
    }

    @ApiStatus.Internal
    @SuppressWarnings("unchecked")
    public static void createEntityRenderers(EntityRendererProvider.Context context) {
        PROVIDERS.forEach((clazz, providerList) -> {
            List<ConditionalOverride<?>> overrideList = new ObjectArrayList<>();
            for (ConditionalOverrideProvider provider : providerList) {
                overrideList.add(provider.create(clazz, context));
            }
            RENDERERS.put(clazz, overrideList);
        });
    }

    public static class RegisterEvent extends Event implements IModBusEvent {
        private final Map<Class<?>, List<ConditionalOverrideProvider<?>>> providers;

        private RegisterEvent(Map<Class<?>, List<ConditionalOverrideProvider<?>>> providers) {
            this.providers = providers;
        }

        public <T extends Entity> void registerOverride(Class<? extends T> entityType, EntityRendererProvider<? super T> entityRendererProvider, Predicate<? super T> shouldOverride, int priority) {
            this.providers.computeIfAbsent(entityType, key -> new ObjectArrayList<>())
                    .add(new ConditionalOverrideProvider<T>(priority, shouldOverride, entityRendererProvider));
        }

        public <T extends Entity> void registerOverride(Class<? extends T> entityType, EntityRendererProvider<? super T> entityRendererProvider, Predicate<? super T> shouldOverride) {
            this.registerOverride(entityType, entityRendererProvider, shouldOverride, 100);
        }

        public <T extends Entity> void registerOverride(Class<? extends T> entityType, ConditionalEntityRendererProvider<? super T> conditionalEntityRendererProvider, int priority) {
            this.registerOverride(entityType, conditionalEntityRendererProvider, conditionalEntityRendererProvider::shouldOverride, priority);
        }

        public <T extends Entity> void registerOverride(Class<? extends T> entityType, ConditionalEntityRendererProvider<? super T> conditionalEntityRendererProvider) {
            this.registerOverride(entityType, conditionalEntityRendererProvider, conditionalEntityRendererProvider::shouldOverride);
        }

        @Override
        public boolean isCancelable() {
            return false;
        }
    }

    private static void addChangedRendererOverrides(RegisterEvent event) {
        event.registerOverride(AbstractClientPlayer.class, EntityInDuctRenderer::new, EntityInDuctRenderer::wantsToOverride);
        event.registerOverride(AbstractClientPlayer.class, EntitySwimmingInLatexRenderer::new, EntitySwimmingInLatexRenderer::wantsToOverride);
    }
}
