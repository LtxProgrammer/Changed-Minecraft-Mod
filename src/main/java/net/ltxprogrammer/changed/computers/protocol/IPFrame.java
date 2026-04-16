package net.ltxprogrammer.changed.computers.protocol;

import java.util.Objects;

public final class IPFrame<T extends Packet> extends Frame {
    private final T packet;
    private final int logicalSource;
    private final int logicalDestination;

    public IPFrame(T packet, int logicalSource, int logicalDestination) {
        this.packet = packet;
        this.logicalSource = logicalSource;
        this.logicalDestination = logicalDestination;
    }

    public boolean isBroadcast() {
        return logicalDestination == 0;
    }

    public static <T extends Packet> IPFrame<T> wrap(T packet, int logicalSource, int logicalDestination) {
        return new IPFrame<>(packet, logicalSource, logicalDestination);
    }

    public static <T extends Packet> IPFrame<T> broadcast(T packet, int logicalSource) {
        return new IPFrame<>(packet, logicalSource, 0);
    }

    public T packet() {
        return packet;
    }

    public int logicalSource() {
        return logicalSource;
    }

    public int logicalDestination() {
        return logicalDestination;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (IPFrame) obj;
        return Objects.equals(this.packet, that.packet) &&
                this.logicalSource == that.logicalSource &&
                this.logicalDestination == that.logicalDestination;
    }

    @Override
    public int hashCode() {
        return Objects.hash(packet, logicalSource, logicalDestination);
    }

    @Override
    public String toString() {
        return "IPFrame[" +
                "packet=" + packet + ", " +
                "logicalSource=" + logicalSource + ", " +
                "logicalDestination=" + logicalDestination + ']';
    }

}
