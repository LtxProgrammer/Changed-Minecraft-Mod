package net.ltxprogrammer.changed.computers.application;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.computers.protocol.DiscoveryProtocol;
import net.ltxprogrammer.changed.computers.protocol.DoorControlProtocol;
import net.ltxprogrammer.changed.computers.protocol.Packet;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class NetworkDeviceControllerApplication implements Application {
    protected final Map<Integer, AtomicInteger> availableDevices = new HashMap<>();
    protected final ComputerMenu menu;
    protected int ticksSinceLastDiscovery = 1000000;
    private final Queue<Pair<Integer, Packet>> packetQueue = new ArrayDeque<>();

    protected NetworkDeviceControllerApplication(ComputerMenu menu) {
        this.menu = menu;
    }

    protected abstract Class<?> getNetworkDeviceProtocol();

    protected void queuePacket(int logicalDestination, Packet packet) {
        packetQueue.add(Pair.of(logicalDestination, packet));
    }

    protected boolean isDeviceReachable(int logicalAddress) {
        return availableDevices.containsKey(logicalAddress);
    }

    @Override
    public void handlePacket(ServerLevel level, int logicalSource, Object packet) {
        if (packet instanceof DiscoveryProtocol discoveryProtocol && discoveryProtocol.isReply()) {
            availableDevices.computeIfAbsent(logicalSource, source -> new AtomicInteger(0)).set(0);
        }
    }

    @Override
    public void serverTick(ServerLevel level) {
        // Timeout
        availableDevices.entrySet().removeIf(entry -> entry.getValue().addAndGet(1) > 80);

        if (this.ticksSinceLastDiscovery++ > 40) {
            menu.computer.nic.broadcastPacket(level, DiscoveryProtocol.create(this.getNetworkDeviceProtocol()));
            this.ticksSinceLastDiscovery = 0;
        }

        while (!packetQueue.isEmpty()) {
            var packet = packetQueue.poll();
            menu.computer.nic.sendPacket(level, packet.getFirst(), packet.getSecond());
        }
    }
}
