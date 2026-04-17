package net.ltxprogrammer.changed.util;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.computers.protocol.Frame;
import net.ltxprogrammer.changed.computers.protocol.NetworkInterface;
import net.ltxprogrammer.changed.computers.protocol.Packet;
import net.minecraft.util.RandomSource;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

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

    public static <T> Stream<T> shuffle(Stream<T> stream, RandomSource random) {
        return stream.sorted((elemA, elemB) -> {
            return random.nextInt();
        });
    }

    private static void swap(Object[] arr, int i, int j) {
        Object tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void shuffle(List<?> list, RandomSource rnd) {
        int size = list.size();
        if (size < 5 || list instanceof RandomAccess) {
            for (int i=size; i>1; i--)
                Collections.swap(list, i-1, rnd.nextInt(i));
        } else {
            Object[] arr = list.toArray();

            // Shuffle array
            for (int i=size; i>1; i--)
                swap(arr, i-1, rnd.nextInt(i));

            // Dump array back into list
            // instead of using a raw type here, it's possible to capture
            // the wildcard but it will require a call to a supplementary
            // private method
            ListIterator it = list.listIterator();
            for (Object e : arr) {
                it.next();
                it.set(e);
            }
        }
    }

    public static <T, R> Iterator<R> mapIterator(Iterator<T> iterator, Function<T, R> mapper) {
        return new Iterator<R>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public R next() {
                return mapper.apply(iterator.next());
            }
        };
    }

    /// Iterates over the elements in the queue and provides them to consumer in order, until {@link Queue#poll()} returns `null`.
    public static <T> void deplete(Queue<T> queue, Consumer<T> consumer) {
        T nextElem;
        while ((nextElem = queue.poll()) != null) {
            consumer.accept(nextElem);
        }
    }
}
