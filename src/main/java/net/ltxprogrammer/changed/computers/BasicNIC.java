package net.ltxprogrammer.changed.computers;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.computers.protocol.*;
import net.ltxprogrammer.changed.util.CollectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Queue;

public class BasicNIC implements LogicalNetworkInterface {
    private final NetworkInterface.Address physicalAddress;
    public int logicalAddress;
    public int ticksSpentConnecting = 0;
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
                ticksSpentConnecting = 0;
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
        if (remoteConnectedPhysicalAddress != null) {
            boolean success = NetworkInterface.sendFrameToAddress(level, remoteConnectedPhysicalAddress, this.physicalAddress, dataFrame);
            if (!success) // Dropped frame
                remoteConnectedPhysicalAddress = null;
        }
    }

    public void tick(ServerLevel level, BlockPos pos) {
        if (remoteConnectedPhysicalAddress != null && NetworkInterface.findAtAddress(level, remoteConnectedPhysicalAddress) == null)
            remoteConnectedPhysicalAddress = null;
        if (remoteConnectedPhysicalAddress != null)
            return;

        ticksSpentConnecting++;
        int tickMod = ticksSpentConnecting % 64;
        int posMod = Math.abs(pos.hashCode()) % 64;
        if (tickMod != posMod)
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

        CollectionUtil.deplete(unprocessedPackets, packet -> {
            handler.handlePacket(level, packet.getFirst(), packet.getSecond());
        });
    }
}
