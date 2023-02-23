package net.ltxprogrammer.changed.util;

/**
 * Acts like a Runnable, but will only run once
 */
public class SingleRunnable implements Runnable {
    boolean check = false;
    protected final Runnable fn;

    public SingleRunnable(Runnable fn) {
        this.fn = fn;
    }

    @Override
    public void run() {
        if (!check) {
            check = true;
            fn.run();
        }
    }
}
