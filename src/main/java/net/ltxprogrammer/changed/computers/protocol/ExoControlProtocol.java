package net.ltxprogrammer.changed.computers.protocol;

import net.ltxprogrammer.changed.util.OptionalBoolean;

import java.util.Objects;

public final class ExoControlProtocol extends Frame {
    public static final ExoControlProtocol SHUTDOWN = new ExoControlProtocol(OptionalBoolean.of(false), OptionalBoolean.empty());
    public static final ExoControlProtocol STARTUP = new ExoControlProtocol(OptionalBoolean.of(true), OptionalBoolean.empty());
    public static final ExoControlProtocol NOT_HOSTILE = new ExoControlProtocol(OptionalBoolean.empty(), OptionalBoolean.of(false));
    public static final ExoControlProtocol HOSTILE = new ExoControlProtocol(OptionalBoolean.empty(), OptionalBoolean.of(true));
    private final OptionalBoolean enabled;
    private final OptionalBoolean hostile;

    public ExoControlProtocol(OptionalBoolean enabled, OptionalBoolean hostile) {
        this.enabled = enabled;
        this.hostile = hostile;
    }

    public OptionalBoolean enabled() {
        return enabled;
    }

    public OptionalBoolean hostile() {
        return hostile;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ExoControlProtocol) obj;
        return Objects.equals(this.enabled, that.enabled) &&
                Objects.equals(this.hostile, that.hostile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enabled, hostile);
    }

    @Override
    public String toString() {
        return "ExoControlProtocol[" +
                "enabled=" + enabled + ',' +
                "hostile=" + hostile + ']';
    }
}
