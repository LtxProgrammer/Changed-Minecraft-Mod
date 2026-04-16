package net.ltxprogrammer.changed.computers.protocol;

import net.minecraft.server.level.ServerLevel;

import java.util.Optional;

/// Represents a device that can accept IP packets to its own logical address, such as a computer or device
public interface LogicalNetworkInterface extends NetworkInterface {
    int LOCALHOST = 0x7F000001;

    static String logicalAddressAsString(int address) {
        return "%s.%s.%s.%s".formatted(
                (address & 0xff000000) >> 24,
                (address & 0x00ff0000) >> 16,
                (address & 0x0000ff00) >> 8,
                address & 0x000000ff);
    }

    static int parseLogicalAddress(String address) throws NumberFormatException {
        var pieces = address.split("\\.");
        if (pieces.length != 4)
            throw new IllegalArgumentException();

        int result = 0;
        for (int i = 0; i < pieces.length; ++i) {
            result |= Integer.parseInt(pieces[i]) << ((3 - i) * 8);
        }

        return result;
    }

    static Optional<Integer> parseLogicalAddressSafe(String address) {
        var pieces = address.split("\\.");
        if (pieces.length != 4)
            return Optional.empty();

        int result = 0;

        try {
            for (int i = 0; i < pieces.length; ++i) {
                result |= Integer.parseInt(pieces[i]) << ((3 - i) * 8);
            }
        } catch (NumberFormatException e) {
            return Optional.empty();
        }

        return Optional.of(result);
    }

    void acceptPacket(ServerLevel level, int logicalSource, Packet packet);

    @Override
    default void acceptFrame(ServerLevel level, Address physicalSource, Frame dataFrame) {
        if (!(dataFrame instanceof IPFrame<?> ipFrame))
            return;
        if (ipFrame.logicalDestination() == this.getLogicalAddress() || ipFrame.isBroadcast())
            acceptPacket(level, ipFrame.logicalSource(), ipFrame.packet());
    }

    int getLogicalAddress();

    default void broadcastPacket(ServerLevel level, Packet packet) {
        sendFrame(level, IPFrame.broadcast(packet, this.getLogicalAddress()));
    }

    default void sendPacket(ServerLevel level, int logicalDestination, Packet packet) {
        if (logicalDestination == LOCALHOST) {
            this.acceptPacket(level, this.getLogicalAddress(), packet);
            return;
        }

        sendFrame(level, IPFrame.wrap(packet, this.getLogicalAddress(), logicalDestination));
    }
}
