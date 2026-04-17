package net.ltxprogrammer.changed.computers.application;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.computers.protocol.DeviceInfoProtocol;
import net.ltxprogrammer.changed.computers.protocol.DoorControlProtocol;
import net.ltxprogrammer.changed.init.ChangedApplications;
import net.ltxprogrammer.changed.network.packet.ComputerAppSyncPacket;
import net.ltxprogrammer.changed.util.TagUtil;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.ltxprogrammer.changed.world.inventory.StasisChamberMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class DoorControllerApplication extends NetworkDeviceControllerApplication {
    public final Map<Integer, DeviceInfoProtocol> reachableDevices = new HashMap<>();
    public boolean devicesDirty = true;

    public enum Command {
        NOOP((app, logicalDevice) -> {}),
        AUTOMATIC((app, logicalDevice) -> {
            app.queuePacket(logicalDevice, DoorControlProtocol.AUTOMATIC);
        }),
        MANUAL((app, logicalDevice) -> {
            app.queuePacket(logicalDevice, DoorControlProtocol.MANUAL);
        }),
        OPEN_DOOR((app, logicalDevice) -> {
            app.queuePacket(logicalDevice, DoorControlProtocol.OPEN_DOOR);
        }),
        CLOSE_DOOR((app, logicalDevice) -> {
            app.queuePacket(logicalDevice, DoorControlProtocol.CLOSE_DOOR);
        });

        private final BiConsumer<DoorControllerApplication, Integer> handler;

        Command(BiConsumer<DoorControllerApplication, Integer> handler) {
            this.handler = handler;
        }

        public void handle(DoorControllerApplication app, int logicalDevice) {
            handler.accept(app, logicalDevice);
        }
    }

    public DoorControllerApplication(ComputerMenu menu, List<String> args) {
        super(menu);
    }

    @Override
    public ApplicationType<?> getType() {
        return ChangedApplications.DOOR_CONTROLLER.get();
    }

    @Override
    protected Class<?> getNetworkDeviceProtocol() {
        return DoorControlProtocol.class;
    }

    @Override
    public void update(CompoundTag payload, LogicalSide receiver, @Nullable ServerPlayer origin) {
        if (receiver.isServer()) {
            String control = payload.getString("control");
            if ("command".equals(control)) {
                int commandId = payload.getInt("command");
                if (commandId < 0 || commandId >= Command.values().length)
                    return;
                int address = payload.getInt("address");
                Command.values()[commandId].handle(this, address);
            }
        } else {
            String control = payload.getString("control");
            if ("addDevice".equals(control)) {
                reachableDevices.put(payload.getInt("address"), new DeviceInfoProtocol(
                        Component.Serializer.fromJson(payload.getString("name")),
                        TagUtil.getBlockPos(payload, "pos"),
                        TagUtil.getResourceLocation(payload, "icon")
                ));
                devicesDirty = true;
            } else if ("removeDevice".equals(control)) {
                reachableDevices.remove(payload.getInt("address"));
                devicesDirty = true;
            }
        }
    }

    @Override
    protected void deviceAdded(ServerLevel level, int logicalDevice, DeviceInfoProtocol deviceInfoProtocol) {
        CompoundTag payload = new CompoundTag();
        payload.putString("control", "addDevice");
        payload.putInt("address", logicalDevice);
        payload.putString("name", Component.Serializer.toJson(deviceInfoProtocol.deviceName()));
        TagUtil.putBlockPos(payload, "pos", deviceInfoProtocol.position());
        TagUtil.putResourceLocation(payload, "icon", deviceInfoProtocol.icon());
        Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)menu.getPlayer()),
                ComputerAppSyncPacket.syncApplication(this.getType(), payload));
    }

    @Override
    protected void deviceRemoved(ServerLevel level, int logicalDevice) {
        CompoundTag payload = new CompoundTag();
        payload.putString("control", "removeDevice");
        payload.putInt("address", logicalDevice);
        Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)menu.getPlayer()),
                ComputerAppSyncPacket.syncApplication(this.getType(), payload));
    }

    public void requestCommand(Command command, Integer logicalDevice) {
        CompoundTag payload = new CompoundTag();
        payload.putString("control", "command");
        payload.putInt("command", command.ordinal());
        payload.putInt("address", logicalDevice);
        Changed.PACKET_HANDLER.sendToServer(ComputerAppSyncPacket.syncApplication(this.getType(), payload));
    }
}
