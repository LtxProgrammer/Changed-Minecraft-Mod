package net.ltxprogrammer.changed.util;

import com.google.common.collect.HashMultimap;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

public abstract class Cacheable<T> implements Supplier<T> {
    @Nullable
    private Optional<T> value = null;

    public void clear() {
        value = null;
    }

    protected abstract T initialGet();

    @Override
    public final T get() {
        if (value == null)
            value = Optional.ofNullable(initialGet());
        return value.orElse(null);
    }

    public final Optional<T> getOptional() {
        if (value == null)
            value = Optional.ofNullable(initialGet());
        return value;
    }

    public void forceValue(@Nullable T v) {
        value = Optional.ofNullable(v);
    }

    public final boolean isResolved() {
        return value != null;
    }

    public final T getOrThrow() {
        var v = get();
        if (v != null)
            return v;
        throw new NullPointerException();
    }

    public Cacheable<T> preResolved(T value) {
        var nval = Cacheable.of(this);
        nval.forceValue(value);
        return nval;
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

        @Override
        public Cacheable<T> preResolved(T value) {
            var nval = new Impl<>(init);
            nval.forceValue(value);
            return nval;
        }
    }

    public static <T> Cacheable<T> of(Supplier<T> supplier) {
        return new Impl<>(supplier);
    }
}
