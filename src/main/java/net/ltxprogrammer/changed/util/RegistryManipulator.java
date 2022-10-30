package net.ltxprogrammer.changed.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;

public class RegistryManipulator {
    @Nullable
    public static <T> RegistryObject<T> getEntry(@NotNull DeferredRegister<T> registry, @NotNull ResourceLocation location) {
        final AtomicReference<RegistryObject<T>> result = new AtomicReference<>(null);
        registry.getEntries().forEach((entry) -> {
            if (entry.getId().equals(location))
                result.compareAndSet(null, entry);
        });
        return result.getAcquire();
    }
}
