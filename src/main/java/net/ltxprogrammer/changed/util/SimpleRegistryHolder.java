package net.ltxprogrammer.changed.util;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

/// Self registering object holder. Used for deferring creation and registration for objects not associated with an official registry.
public class SimpleRegistryHolder<T> implements Supplier<T> {
    private Supplier<T> registryFunction;
    private @Nullable T value;

    public SimpleRegistryHolder(Supplier<T> registryFunction) {
        this.registryFunction = registryFunction;
    }

    @Override
    public T get() {
        if (value == null)
            throw new IllegalStateException("get() called before holder was registered");
        return value;
    }

    private static <T> T invalidSupplier() {
        throw new IllegalStateException("register() called on holder twice");
    }

    /// Call to invoke object creation and registration
    /// ONLY CALL ONCE
    public void register() {
        value = registryFunction.get();
        Objects.requireNonNull(value, "registry function provided a null value");

        // Prevent double registry, and free captured variables from lambdas
        registryFunction = SimpleRegistryHolder::invalidSupplier;
    }
}
