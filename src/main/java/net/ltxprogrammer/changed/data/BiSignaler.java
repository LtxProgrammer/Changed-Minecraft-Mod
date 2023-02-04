package net.ltxprogrammer.changed.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class BiSignaler<T1, T2> {
    private final List<BiConsumer<T1, T2>> consumers = new ArrayList<>();

    public BiListener<T1, T2> addListener(BiConsumer<T1, T2> consumer) {
        consumers.add(consumer);
        return new BiListener<>(this);
    }

    public void invoke(T1 param1, T2 param2) {
        consumers.forEach(consumer -> consumer.accept(param1, param2));
    }
}
