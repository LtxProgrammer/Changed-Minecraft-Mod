package net.ltxprogrammer.changed.util;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class CollectionUtil {
    @Nullable
    public static <T> T find(Collection<T> collection, Predicate<T> predicate) {
        for (T value : collection) {
            if (predicate.test(value))
                return value;
        }

        return null;
    }

    public static <T, C extends Collection<T>> C of(Supplier<C> ctor, T value) {
        C collec = ctor.get();
        collec.add(value);
        return collec;
    }

    public static <T> void concatonate(Collection<T> base, Collection<T> add) {
        base.addAll(add);
    }

    @Nullable
    public static <T> T getAt(Collection<T> collection, int index) {
        AtomicInteger location = new AtomicInteger(0);
        AtomicReference<T> value = new AtomicReference<>(null);
        collection.forEach((elem) -> {
            if (location.getAndIncrement() == index)
                value.compareAndSet(null, elem);
        });
        return value.getAcquire();
    }

    public static <T> void removeAt(Collection<T> collection, int index) {
        AtomicInteger location = new AtomicInteger(0);
        collection.removeIf((elem) -> location.getAndIncrement() == index);
    }

    public static <T> void insert(Collection<T> collection, int index, T value) {
        List<T> split = new ArrayList<>();
        while (index < collection.size()) {
            split.add(getAt(collection, index));
            removeAt(collection, index);
        }
        collection.add(value);
        concatonate(collection, split);
    }

    public static <T> void forEachReverse(Collection<T> collection, Consumer<T> consumer) {
        List<T> reverseList = new ArrayList<>();
        reverseList.addAll(collection);
        Collections.reverse(reverseList);
        reverseList.forEach(consumer);
    }
}
