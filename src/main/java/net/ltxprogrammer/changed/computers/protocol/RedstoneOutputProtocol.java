package net.ltxprogrammer.changed.computers.protocol;

import net.minecraft.core.Direction;

import java.util.Set;

public final class RedstoneOutputProtocol extends Packet {
    private final int signal;
    private final int duration;
    private final Set<Direction> directions;

    public static final int UNLIMITED_DURATION = -1;
    public static final Set<Direction> ALL_DIRECTIONS = Set.of(Direction.UP, Direction.DOWN, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST);

    public static RedstoneOutputProtocol of(int signal, int duration) {
        return new RedstoneOutputProtocol(signal, duration, ALL_DIRECTIONS);
    }

    public static RedstoneOutputProtocol of(int signal, int duration, Set<Direction> directions) {
        return new RedstoneOutputProtocol(signal, duration, directions);
    }

    private RedstoneOutputProtocol(int signal, int duration, Set<Direction> directions) {
        this.signal = signal;
        this.duration = duration;
        this.directions = directions;
    }

    public boolean isDurationUnlimited() {
        return duration == UNLIMITED_DURATION;
    }

    public boolean hasSignal() {
        return signal > 0;
    }

    public int getSignal() {
        return signal;
    }

    public Set<Direction> getDirections() {
        return directions;
    }
}
