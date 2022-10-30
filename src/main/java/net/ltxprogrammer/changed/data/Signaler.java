package net.ltxprogrammer.changed.data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Signaler<T> {
    private final List<Consumer<T>> consumers = new ArrayList<>();

    public Listener<T> addListener(Consumer<T> consumer) {
        consumers.add(consumer);
        return new Listener<>(this);
    }

    public void invoke(T param) {
        consumers.forEach(consumer -> consumer.accept(param));
    }
}
