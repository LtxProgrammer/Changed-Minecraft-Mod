package net.ltxprogrammer.changed.computers.protocol;

import java.util.Objects;
import java.util.Optional;

public final class DoorControlProtocol extends Packet {
    public static final DoorControlProtocol AUTOMATIC = new DoorControlProtocol(Optional.of(true), Optional.empty(), Optional.empty());
    public static final DoorControlProtocol MANUAL = new DoorControlProtocol(Optional.of(false), Optional.empty(), Optional.empty());
    public static final DoorControlProtocol OPEN_DOOR = new DoorControlProtocol(Optional.of(false), Optional.of(true), Optional.empty());
    public static final DoorControlProtocol CLOSE_DOOR = new DoorControlProtocol(Optional.of(false), Optional.of(false), Optional.empty());
    private final Optional<Boolean> automaticState;
    private final Optional<Boolean> openState;
    private final Optional<Boolean> lockState;

    public DoorControlProtocol(Optional<Boolean> automaticState, Optional<Boolean> openState, Optional<Boolean> lockState) {
        this.automaticState = automaticState;
        this.openState = openState;
        this.lockState = lockState;
    }

    public Optional<Boolean> automaticState() {
        return automaticState;
    }

    public Optional<Boolean> openState() {
        return openState;
    }

    public Optional<Boolean> lockState() {
        return lockState;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DoorControlProtocol) obj;
        return Objects.equals(this.automaticState, that.automaticState) &&
                Objects.equals(this.openState, that.openState) &&
                Objects.equals(this.lockState, that.lockState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(automaticState, openState, lockState);
    }

    @Override
    public String toString() {
        return "DoorControlProtocol[" +
                "automaticState=" + automaticState + ", " +
                "openState=" + openState + ", " +
                "lockState=" + lockState + ']';
    }

}
