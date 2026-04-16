package net.ltxprogrammer.changed.computers.protocol;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/// Used to communicate which protocols a device will respond to. This is useful to list devices that self elect to respond
public final class DiscoveryProtocol extends Packet {
    private final Set<Class<?>> implementedProtocols;
    private final boolean isReply;

    public DiscoveryProtocol(Set<Class<?>> implementedProtocols, boolean isReply) {
        this.implementedProtocols = implementedProtocols;
        this.isReply = isReply;
    }

    public static DiscoveryProtocol create(Class<?> implementedProtocol) {
        return new DiscoveryProtocol(Set.of(implementedProtocol), false);
    }

    public static DiscoveryProtocol create(Set<Class<?>> implementedProtocols) {
        return new DiscoveryProtocol(implementedProtocols, false);
    }

    public DiscoveryProtocol intersect(Class<?> implementedProtocol) {
        return intersect(Set.of(implementedProtocol));
    }

    public DiscoveryProtocol intersect(Set<Class<?>> otherImplementedProtocols) {
        Set<Class<?>> intersection = new HashSet<>();
        implementedProtocols.forEach(protocol -> {
            if (otherImplementedProtocols.contains(protocol))
                intersection.add(protocol);
        });
        return new DiscoveryProtocol(intersection, true);
    }

    public boolean contains(Class<?> protocol) {
        return implementedProtocols.contains(protocol);
    }

    public Set<Class<?>> implementedProtocols() {
        return implementedProtocols;
    }

    public boolean isReply() {
        return isReply;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (DiscoveryProtocol) obj;
        return Objects.equals(this.implementedProtocols, that.implementedProtocols) &&
                this.isReply == that.isReply;
    }

    @Override
    public int hashCode() {
        return Objects.hash(implementedProtocols, isReply);
    }

    @Override
    public String toString() {
        return "DiscoveryProtocol[" +
                "implementedProtocols=" + implementedProtocols + ", " +
                "isReply=" + isReply + ']';
    }

}
