package net.ltxprogrammer.changed.computers;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.computers.protocol.LogicalNetworkInterface;
import net.ltxprogrammer.changed.computers.protocol.NetworkInterface;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.BiConsumer;

public class BasicNIC implements LogicalNetworkInterface {
    public int logicalAddress;
    public @Nullable NetworkInterface.Address remoteConnectedPhysicalAddress;
    public Queue<Pair<Integer, CompoundTag>> unprocessedPackets;

    @Override
    public void acceptPacket(ServerLevel level, int logicalSource, CompoundTag packet) {
        if (unprocessedPackets == null)
            unprocessedPackets = new ArrayDeque<>();
        unprocessedPackets.add(Pair.of(logicalSource, packet));
    }

    @Override
    public int getLogicalAddress() {
        return logicalAddress;
    }

    @Override
    public void sendFrame(ServerLevel level, CompoundTag dataFrame) {
        if (remoteConnectedPhysicalAddress != null)
            NetworkInterface.sendFrameToAddress(level, remoteConnectedPhysicalAddress, dataFrame);
    }

    public void processPackets(BiConsumer<Integer, CompoundTag> handler) {
        if (unprocessedPackets == null)
            return;

        Pair<Integer, CompoundTag> nextPacket;
        while ((nextPacket = unprocessedPackets.poll()) != null) {
            handler.accept(nextPacket.getFirst(), nextPacket.getSecond());
        }
    }
}
