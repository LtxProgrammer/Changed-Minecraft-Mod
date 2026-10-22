package net.ltxprogrammer.changed.computers.application;

import com.mojang.datafixers.util.Pair;
import net.ltxprogrammer.changed.computers.protocol.*;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.server.level.ServerLevel;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class NetworkDeviceControllerApplication implements Application {
    protected final Map<NetworkInterface.Address, AtomicInteger> availableDevices = new HashMap<>();
    protected final ComputerMenu menu;
    protected int ticksSinceLastDiscovery = 1000000;
    private final Queue<Pair<NetworkInterface.Address, Frame>> frameQueue = new ArrayDeque<>();

    protected NetworkDeviceControllerApplication(ComputerMenu menu) {
        this.menu = menu;
    }

    protected abstract Class<?> getNetworkDeviceProtocol();

    protected void queueFrame(NetworkInterface.Address logicalDestination, Frame packet) {
        frameQueue.add(Pair.of(logicalDestination, packet));
    }

    protected boolean isDeviceReachable(NetworkInterface.Address logicalAddress) {
        return availableDevices.containsKey(logicalAddress);
    }

    protected void deviceAdded(ServerLevel level, NetworkInterface.Address logicalDevice, DeviceInfoProtocol deviceInfoProtocol) {

    }

    protected void deviceRemoved(ServerLevel level, NetworkInterface.Address logicalDevice) {

    }

    @Override
    public void handlePacket(ServerLevel level, NetworkInterface.Address physicalSource, Frame frame) {
        if (frame instanceof DiscoveryProtocol discoveryProtocol && discoveryProtocol.isReply() &&
            discoveryProtocol.contains(this.getNetworkDeviceProtocol()) &&
            discoveryProtocol.contains(DeviceInfoProtocol.Query.class)) {
            boolean addDevice = !availableDevices.containsKey(physicalSource);
            if (addDevice) { // Query device info before adding
                menu.computer.sendOutFrame(level, physicalSource, DeviceInfoProtocol.Query.INSTANCE);
            } else { // Subsequent DiscoveryProtocol replies do not require a DeviceInfoProtocol reply
                availableDevices.computeIfAbsent(physicalSource, source -> new AtomicInteger(0)).set(0);
            }
        } else if (frame instanceof DeviceInfoProtocol deviceInfoProtocol) {
            boolean addDevice = !availableDevices.containsKey(physicalSource);
            if (addDevice) {
                availableDevices.computeIfAbsent(physicalSource, source -> new AtomicInteger(0)).set(0);
                this.deviceAdded(level, physicalSource, deviceInfoProtocol);
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
            menu.computer.sendOutFrame(level, NetworkInterface.BROADCAST, DiscoveryProtocol.create(Set.of(
                    this.getNetworkDeviceProtocol(),
                    DeviceInfoProtocol.Query.class
            )));
            this.ticksSinceLastDiscovery = 0;
        }

        while (!frameQueue.isEmpty()) {
            var packet = frameQueue.poll();
            menu.computer.sendOutFrame(level, packet.getFirst(), packet.getSecond());
        }
    }
}
