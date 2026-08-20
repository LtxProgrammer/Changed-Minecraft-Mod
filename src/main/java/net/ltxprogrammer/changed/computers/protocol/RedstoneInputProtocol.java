package net.ltxprogrammer.changed.computers.protocol;

import net.minecraft.core.Direction;

public final class RedstoneInputProtocol extends Packet {
    private final int signal;
    private final Direction direction;

    public static RedstoneInputProtocol of(int signal, Direction direction) {
        return new RedstoneInputProtocol(signal, direction);
    }

    private RedstoneInputProtocol(int signal, Direction direction) {
        this.signal = signal;
        this.direction = direction;
    }

    public boolean hasSignal() {
        return signal > 0;
    }

    public int getSignal() {
        return signal;
    }

    public Direction getDirection() {
        return direction;
    }
}
