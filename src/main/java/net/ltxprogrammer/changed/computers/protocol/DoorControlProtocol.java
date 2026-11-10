package net.ltxprogrammer.changed.computers.protocol;

import net.ltxprogrammer.changed.util.OptionalBoolean;

import java.util.Objects;

public final class DoorControlProtocol extends Frame {
    public static final DoorControlProtocol AUTOMATIC = new DoorControlProtocol(OptionalBoolean.of(true), OptionalBoolean.empty(), OptionalBoolean.empty());
    public static final DoorControlProtocol MANUAL = new DoorControlProtocol(OptionalBoolean.of(false), OptionalBoolean.empty(), OptionalBoolean.empty());
    public static final DoorControlProtocol OPEN_DOOR = new DoorControlProtocol(OptionalBoolean.of(false), OptionalBoolean.of(true), OptionalBoolean.empty());
    public static final DoorControlProtocol CLOSE_DOOR = new DoorControlProtocol(OptionalBoolean.of(false), OptionalBoolean.of(false), OptionalBoolean.empty());
    private final OptionalBoolean automaticState;
    private final OptionalBoolean openState;
    private final OptionalBoolean lockState;

    public DoorControlProtocol(OptionalBoolean automaticState, OptionalBoolean openState, OptionalBoolean lockState) {
        this.automaticState = automaticState;
        this.openState = openState;
        this.lockState = lockState;
    }

    public OptionalBoolean automaticState() {
        return automaticState;
    }

    public OptionalBoolean openState() {
        return openState;
    }

    public OptionalBoolean lockState() {
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
