package net.ltxprogrammer.changed.computers.application;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.computers.protocol.DeviceInfoProtocol;
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

    protected void deviceAdded(ServerLevel level, int logicalDevice, DeviceInfoProtocol deviceInfoProtocol) {

    }

    protected void deviceRemoved(ServerLevel level, int logicalDevice) {

    }

    @Override
    public void handlePacket(ServerLevel level, int logicalSource, Packet packet) {
        if (packet instanceof DiscoveryProtocol discoveryProtocol && discoveryProtocol.isReply() &&
            discoveryProtocol.contains(this.getNetworkDeviceProtocol()) &&
            discoveryProtocol.contains(DeviceInfoProtocol.Query.class)) {
            boolean addDevice = !availableDevices.containsKey(logicalSource);
            if (addDevice) { // Query device info before adding
                menu.computer.nic.sendPacket(level, logicalSource, DeviceInfoProtocol.Query.INSTANCE);
            } else { // Subsequent DiscoveryProtocol replies do not require a DeviceInfoProtocol reply
                availableDevices.computeIfAbsent(logicalSource, source -> new AtomicInteger(0)).set(0);
            }
        } else if (packet instanceof DeviceInfoProtocol deviceInfoProtocol) {
            boolean addDevice = !availableDevices.containsKey(logicalSource);
            if (addDevice) {
                availableDevices.computeIfAbsent(logicalSource, source -> new AtomicInteger(0)).set(0);
                this.deviceAdded(level, logicalSource, deviceInfoProtocol);
            }
        }
    }

    @Override
    public void serverTick(ServerLevel level) {
        // Timeout
        availableDevices.entrySet().removeIf(entry -> {
            boolean removeDevice = entry.getValue().addAndGet(1) > 80;
            if (removeDevice)
                this.deviceRemoved(level, entry.getKey());
            return removeDevice;
        });

        if (this.ticksSinceLastDiscovery++ > 40) {
            menu.computer.nic.broadcastPacket(level, DiscoveryProtocol.create(Set.of(
                    this.getNetworkDeviceProtocol(),
                    DeviceInfoProtocol.Query.class
            )));
            this.ticksSinceLastDiscovery = 0;
        }

        while (!packetQueue.isEmpty()) {
            var packet = packetQueue.poll();
            menu.computer.nic.sendPacket(level, packet.getFirst(), packet.getSecond());
        }
    }
}
