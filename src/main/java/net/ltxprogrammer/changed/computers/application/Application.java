package net.ltxprogrammer.changed.computers.application;

import net.ltxprogrammer.changed.computers.protocol.Packet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Synced state between server and client
 */
public interface Application {
    ApplicationType<?> getType();

    default Set<Class<?>> getNetworkProtocols() {
        return Set.of();
    }

    /// Called to handle a packet from an in-game device
    default void handlePacket(ServerLevel level, int logicalSource, Packet packet) {

    }

    default void serverTick(ServerLevel level) {

    }

    /// Called to handle a payload of data from the opposite
    default void update(CompoundTag payload, LogicalSide receiver, @Nullable ServerPlayer origin) {

    }

    default void onClose() {

    }
}
