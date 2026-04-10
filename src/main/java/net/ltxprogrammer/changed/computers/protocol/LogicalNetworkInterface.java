package net.ltxprogrammer.changed.computers.protocol;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;

/// Represents a device that can accept IP packets to its own logical address, such as a computer or router
public interface LogicalNetworkInterface extends NetworkInterface {
    void acceptPacket(ServerLevel level, int logicalSource, CompoundTag packet);

    @Override
    default void acceptFrame(ServerLevel level, CompoundTag dataFrame) {
        if (dataFrame.getInt("dst") == this.getLogicalAddress())
            acceptPacket(level, dataFrame.getInt("src"), dataFrame.getCompound("data"));
    }

    default IntList discoverConnectedDevices() {
        return IntList.of();
    }

    int getLogicalAddress();

    default void sendPacket(ServerLevel level, int logicalDestination, CompoundTag packet) {
        CompoundTag frame = new CompoundTag();
        frame.putInt("dst", logicalDestination);
        frame.putInt("src", this.getLogicalAddress());
        frame.put("data", packet);
        sendFrame(level, frame);
    }
}
