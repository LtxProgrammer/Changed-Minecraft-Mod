package net.ltxprogrammer.changed.computers;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.ltxprogrammer.changed.computers.protocol.*;
import net.ltxprogrammer.changed.util.CollectionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;

public class BasicNIC implements NetworkInterface {
    public final NetworkInterface.Address physicalAddress;
    protected long tickCount;
    public final Set<Address> nearbyAddresses = new ObjectArraySet<>();
    public Queue<Pair<Address, Frame>> unprocessedFrames;

    public BasicNIC(Address physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public interface FrameHandler {
        void handleFrame(ServerLevel level, Address physicalSource, Frame dataFrame);
    }

    @Override
    public void acceptNeighbor(ServerLevel level, Address physicalSource) {
        nearbyAddresses.add(physicalSource);
    }

    @Override
    public void acceptFrame(ServerLevel level, Address physicalSource, Frame dataFrame) {
        if (unprocessedFrames == null)
            unprocessedFrames = new ArrayDeque<>();
        unprocessedFrames.add(Pair.of(physicalSource, dataFrame));
    }

    @Override
    public void sendOutFrame(ServerLevel level, @Nullable Address destination, Frame frame, int ttl) {
        NetworkInterface.sendFrameToAddress(level, destination, this.physicalAddress, frame, ttl, this.nearbyAddresses);
    }

    public void tick(ServerLevel level, BlockPos pos) {
        tickCount++;
        int updateRate = nearbyAddresses.isEmpty() ? 64 : 256;
        long tickMod = tickCount % updateRate;
        int posMod = Math.abs(pos.hashCode()) % updateRate;
        if (tickMod != posMod)
            return;

        nearbyAddresses.removeIf(address -> NetworkInterface.findAtAddress(level, address) == null);
        NetworkInterface.findNearbyAddresses(level, pos, MAX_LOCAL_TRAVEL_DISTANCE).forEach(address -> {
            if (address.equals(this.physicalAddress))
                return;
            NetworkInterface.findAtAddress(level, address).acceptNeighbor(level, address);
            nearbyAddresses.add(address);
        });
    }

    public void processFrames(ServerLevel level, FrameHandler handler) {
        if (unprocessedFrames == null)
            return;

        CollectionUtil.deplete(unprocessedFrames, packet -> {
            handler.handleFrame(level, packet.getFirst(), packet.getSecond());
        });
    }
}
