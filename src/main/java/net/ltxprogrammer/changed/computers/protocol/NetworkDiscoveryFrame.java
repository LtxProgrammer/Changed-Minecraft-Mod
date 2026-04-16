package net.ltxprogrammer.changed.computers.protocol;

import java.util.Objects;

/// Used to find nearby routers to establish a network
public final class NetworkDiscoveryFrame extends Frame {
    private final boolean isReply;
    private final boolean commitConnection;

    public static final NetworkDiscoveryFrame PROBE = new NetworkDiscoveryFrame(false, false);
    public static final NetworkDiscoveryFrame PROBE_REPLY = new NetworkDiscoveryFrame(true, false);
    public static final NetworkDiscoveryFrame CONNECT = new NetworkDiscoveryFrame(false, true);
    public static final NetworkDiscoveryFrame CONNECT_REPLY = new NetworkDiscoveryFrame(true, true);

    private NetworkDiscoveryFrame(boolean isReply, boolean commitConnection) {
        this.isReply = isReply;
        this.commitConnection = commitConnection;
    }

    public boolean isReply() {
        return isReply;
    }

    public boolean commitConnection() {
        return commitConnection;
    }

    public NetworkDiscoveryFrame reply() {
        return commitConnection ? CONNECT_REPLY : PROBE_REPLY;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (NetworkDiscoveryFrame) obj;
        return this.isReply == that.isReply &&
                this.commitConnection == that.commitConnection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isReply, commitConnection);
    }

    @Override
    public String toString() {
        return "NetworkDiscoveryFrame[" +
                "isReply=" + isReply + ", " +
                "commitConnection=" + commitConnection + ']';
    }

}
