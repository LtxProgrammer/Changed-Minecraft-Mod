package net.ltxprogrammer.changed.data;

public class BiListener<T1, T2> {
    private final BiSignaler<T1, T2> signaler;

    public BiListener(BiSignaler<T1, T2> signaler) {
        this.signaler = signaler;
    }
}
