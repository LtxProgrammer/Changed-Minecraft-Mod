package net.ltxprogrammer.changed.computers;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.computers.protocol.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Queue;

public class BasicNIC implements LogicalNetworkInterface {
    private final NetworkInterface.Address physicalAddress;
    public int logicalAddress;
    public @Nullable NetworkInterface.Address remoteConnectedPhysicalAddress;
    public Queue<Pair<Integer, Packet>> unprocessedPackets;

    public BasicNIC(Address physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public interface PacketHandler {
        void handlePacket(ServerLevel level, int logicalSource, Packet packet);
    }

    @Override
    public void acceptFrame(ServerLevel level, Address physicalSource, Frame dataFrame) {
        LogicalNetworkInterface.super.acceptFrame(level, physicalSource, dataFrame);
        if (dataFrame instanceof NetworkDiscoveryFrame networkDiscoveryFrame && networkDiscoveryFrame.isReply()) {
            if (!networkDiscoveryFrame.commitConnection()) {
                this.remoteConnectedPhysicalAddress = physicalSource;
                NetworkInterface.sendFrameToAddress(level, physicalSource, this.physicalAddress, NetworkDiscoveryFrame.CONNECT);
            }
        }
    }

    @Override
    public void acceptPacket(ServerLevel level, int logicalSource, Packet packet) {
        if (unprocessedPackets == null)
            unprocessedPackets = new ArrayDeque<>();
        unprocessedPackets.add(Pair.of(logicalSource, packet));
    }

    @Override
    public int getLogicalAddress() {
        return logicalAddress;
    }

    @Override
    public void sendFrame(ServerLevel level, Frame dataFrame) {
        if (remoteConnectedPhysicalAddress != null)
            NetworkInterface.sendFrameToAddress(level, remoteConnectedPhysicalAddress, this.physicalAddress, dataFrame);
    }

    public void tick(ServerLevel level, BlockPos pos) {
        if (remoteConnectedPhysicalAddress != null)
            return;

        NetworkInterface.findNearbyAddresses(level, pos, 16).forEach(address -> {
            if (address.equals(this.physicalAddress))
                return;
            NetworkInterface.sendFrameToAddress(level, address, this.physicalAddress, NetworkDiscoveryFrame.PROBE);
        });
    }

    public void processPackets(ServerLevel level, PacketHandler handler) {
        if (unprocessedPackets == null)
            return;

        Pair<Integer, Packet> nextPacket;
        while ((nextPacket = unprocessedPackets.poll()) != null) {
            handler.handlePacket(level, nextPacket.getFirst(), nextPacket.getSecond());
        }
    }
}
