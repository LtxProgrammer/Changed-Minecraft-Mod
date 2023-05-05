package net.ltxprogrammer.changed.util;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class Cacheable<T> implements Supplier<T> {
    @Nullable
    private Optional<T> value = null;

    protected abstract T initialGet();

    @Override
    public final T get() {
        if (value == null)
            value = Optional.ofNullable(initialGet());
        return value.orElse(null);
    }

    public final T getOrThrow() {
        var v = get();
        if (v != null)
            return v;
        throw new NullPointerException();
    }

    protected static class Impl<T> extends Cacheable<T> {
        private final Supplier<T> init;

        protected Impl(Supplier<T> init) {
            this.init = init;
        }

        @Override
        protected T initialGet() {
            return init.get();
        }
    }

    public static <T> Cacheable<T> of(Supplier<T> supplier) {
        return new Impl<>(supplier);
    }
}
