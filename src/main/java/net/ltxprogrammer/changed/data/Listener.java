package net.ltxprogrammer.changed.data;

public class Listener<T> {
    private final Signaler<T> signaler;

    public Listener(Signaler<T> signaler) {
        this.signaler = signaler;
    }
}
