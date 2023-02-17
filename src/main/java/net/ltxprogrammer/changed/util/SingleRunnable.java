package net.ltxprogrammer.changed.util;

import java.util.concurrent.atomic.AtomicBoolean;

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
