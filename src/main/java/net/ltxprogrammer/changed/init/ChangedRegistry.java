package net.ltxprogrammer.changed.init;

import com.mojang.serialization.Lifecycle;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.ability.AbstractAbility;
import net.ltxprogrammer.changed.entity.variant.LatexVariant;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public abstract class ChangedRegistry<T> extends Registry<T> {
    private static final Logger LOGGER = LogManager.getLogger(ChangedRegistry.class);

    private static final int MAX_VARINT = Integer.MAX_VALUE - 1;
    private static final HashMap<ResourceKey<Registry<?>>, Supplier<ForgeRegistry<?>>> REGISTRY_HOLDERS = new HashMap<>();

    public static final ResourceKey<Registry<LatexVariant<?>>> LATEX_VARIANT_REGISTRY = registryKey("latex_variant");
    public static final ResourceKey<Registry<AbstractAbility<?>>> ABILITY_REGISTRY = registryKey("ability");
    public static final Supplier<ForgeRegistry<LatexVariant<?>>> LATEX_VARIANT = registerHolder(LATEX_VARIANT_REGISTRY);
    public static final Supplier<ForgeRegistry<AbstractAbility<?>>> ABILITY = registerHolder(ABILITY_REGISTRY);

    @SubscribeEvent
    public static void onCreateRegistries(NewRegistryEvent event) {
        createRegistry(event, LATEX_VARIANT_REGISTRY, c(LatexVariant.class), builder -> {
            builder.missing((key, network) -> LatexVariant.FALLBACK_VARIANT);
        }, null);
        createRegistry(event, ABILITY_REGISTRY, c(AbstractAbility.class));
    }

    private static <T extends IForgeRegistryEntry<T>> Supplier<ForgeRegistry<T>> registerHolder(ResourceKey<Registry<T>> key) {
        return () -> (ForgeRegistry<T>) REGISTRY_HOLDERS.get(key).get();
    }

    private static <T extends IForgeRegistryEntry<T>> void createRegistry(NewRegistryEvent event, ResourceKey<? extends Registry<T>> key, Class<T> type) {
        createRegistry(event, key, type, null, null);
    }

    private static <T extends IForgeRegistryEntry<T>> void createRegistry(NewRegistryEvent event, ResourceKey<? extends Registry<T>> key, Class<T> type,
                                                                          @Nullable Consumer<RegistryBuilder<T>> additionalBuilder,
                                                                          @Nullable Consumer<IForgeRegistry<T>> onFill) {
        var builder = makeRegistry(key, type);
        if (additionalBuilder != null)
            additionalBuilder.accept(builder);
        Supplier<IForgeRegistry<T>> holder = event.create(builder, onFill);
        REGISTRY_HOLDERS.put((ResourceKey)key, () -> (ForgeRegistry<?>)holder.get());
        LOGGER.info("Created registry {}", key);
    }

    static <T> Class<T> c(Class<?> cls) { return (Class<T>)cls; }
    private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(ResourceKey<? extends Registry<T>> key, Class<T> type) {
        return new RegistryBuilder<T>().setName(key.location()).setType(type).setMaxID(MAX_VARINT);
    }

    private ChangedRegistry(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle) {
        super(key, lifecycle);
    }

    private static <T> ResourceKey<Registry<T>> registryKey(String name) {
        return ResourceKey.createRegistryKey(Changed.modResource(name));
    }
}
