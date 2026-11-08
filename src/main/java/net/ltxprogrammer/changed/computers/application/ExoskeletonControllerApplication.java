package net.ltxprogrammer.changed.computers.application;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.computers.protocol.DeviceInfoProtocol;
import net.ltxprogrammer.changed.computers.protocol.ExoControlProtocol;
import net.ltxprogrammer.changed.computers.protocol.NetworkInterface;
import net.ltxprogrammer.changed.init.ChangedApplications;
import net.ltxprogrammer.changed.network.packet.ComputerAppSyncPacket;
import net.ltxprogrammer.changed.util.TagUtil;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

// Maybe add remote control option? Player views world from exo POV, and their inputs are mapped to the exo
public class ExoskeletonControllerApplication extends NetworkDeviceControllerApplication {
    public final Map<NetworkInterface.Address, DeviceInfoProtocol> reachableDevices = new HashMap<>();
    public boolean devicesDirty = true;

    public enum Command {
        NOOP((app, logicalDevice) -> {}),
        STARTUP((app, logicalDevice) -> {
            app.queueFrame(logicalDevice, ExoControlProtocol.STARTUP);
        }),
        SHUTDOWN((app, logicalDevice) -> {
            app.queueFrame(logicalDevice, ExoControlProtocol.SHUTDOWN);
        }),
        NOT_HOSTILE((app, logicalDevice) -> {
            app.queueFrame(logicalDevice, ExoControlProtocol.NOT_HOSTILE);
        }),
        HOSTILE((app, logicalDevice) -> {
            app.queueFrame(logicalDevice, ExoControlProtocol.HOSTILE);
        });

        private final BiConsumer<ExoskeletonControllerApplication, NetworkInterface.Address> handler;

        Command(BiConsumer<ExoskeletonControllerApplication, NetworkInterface.Address> handler) {
            this.handler = handler;
        }

        public void handle(ExoskeletonControllerApplication app, NetworkInterface.Address logicalDevice) {
            handler.accept(app, logicalDevice);
        }
    }

    public ExoskeletonControllerApplication(ComputerMenu menu, List<String> args) {
        super(menu);
    }

    @Override
    public ApplicationType<?> getType() {
        return ChangedApplications.EXOSKELETON_CONTROLLER.get();
    }

    @Override
    protected Class<?> getNetworkDeviceProtocol() {
        return ExoControlProtocol.class;
    }

    @Override
    public void update(CompoundTag payload, LogicalSide receiver, @Nullable ServerPlayer origin) {
        if (receiver.isServer()) {
            String control = payload.getString("control");
            if ("command".equals(control)) {
                int commandId = payload.getInt("command");
                if (commandId < 0 || commandId >= Command.values().length)
                    return;
                var address = NetworkInterface.Address.decode(payload.get("address"));
                Command.values()[commandId].handle(this, address);
            }
        } else {
            String control = payload.getString("control");
            if ("addDevice".equals(control)) {
                reachableDevices.put(NetworkInterface.Address.decode(payload.get("address")), new DeviceInfoProtocol(
                        Component.Serializer.fromJson(payload.getString("name")),
                        TagUtil.getBlockPos(payload, "pos"),
                        TagUtil.getResourceLocation(payload, "icon")
                ));
                devicesDirty = true;
            } else if ("removeDevice".equals(control)) {
                reachableDevices.remove(NetworkInterface.Address.decode(payload.get("address")));
                devicesDirty = true;
            }
        }
    }

    @Override
    protected void deviceAdded(ServerLevel level, NetworkInterface.Address address, DeviceInfoProtocol deviceInfoProtocol) {
        CompoundTag payload = new CompoundTag();
        payload.putString("control", "addDevice");
        payload.put("address", address.encode());
        payload.putString("name", Component.Serializer.toJson(deviceInfoProtocol.deviceName()));
        TagUtil.putBlockPos(payload, "pos", deviceInfoProtocol.position());
        TagUtil.putResourceLocation(payload, "icon", deviceInfoProtocol.icon());
        Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)menu.getPlayer()),
                ComputerAppSyncPacket.syncApplication(this.getType(), payload));
    }

    @Override
    protected void deviceRemoved(ServerLevel level, NetworkInterface.Address address) {
        CompoundTag payload = new CompoundTag();
        payload.putString("control", "removeDevice");
        payload.put("address", address.encode());
        Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)menu.getPlayer()),
                ComputerAppSyncPacket.syncApplication(this.getType(), payload));
    }

    public void requestCommand(Command command, NetworkInterface.Address address) {
        CompoundTag payload = new CompoundTag();
        payload.putString("control", "command");
        payload.putInt("command", command.ordinal());
        payload.put("address", address.encode());
        Changed.PACKET_HANDLER.sendToServer(ComputerAppSyncPacket.syncApplication(this.getType(), payload));
    }
}
