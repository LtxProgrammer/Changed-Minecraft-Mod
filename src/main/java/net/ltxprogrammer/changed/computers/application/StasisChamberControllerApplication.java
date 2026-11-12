package net.ltxprogrammer.changed.computers.application;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.block.entity.StasisChamberBlockEntity;
import net.ltxprogrammer.changed.computers.protocol.*;
import net.ltxprogrammer.changed.init.ChangedApplications;
import net.ltxprogrammer.changed.init.ChangedBlockEntities;
import net.ltxprogrammer.changed.network.packet.ComputerAppSyncPacket;
import net.ltxprogrammer.changed.util.TagUtil;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.ltxprogrammer.changed.world.inventory.StasisChamberMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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

public class StasisChamberControllerApplication extends NetworkDeviceControllerApplication {
    public final Map<NetworkInterface.Address, DeviceInfoProtocol> reachableDevices = new HashMap<>();
    public boolean devicesDirty = true;

    public @Nullable NetworkInterface.Address inspectingChamberAddress;
    public @Nullable StasisChamberBlockEntity inspectingChamber;

    public StasisChamberControllerApplication(ComputerMenu menu, List<String> args) {
        super(menu);
    }

    @Override
    public ApplicationType<?> getType() {
        return ChangedApplications.STASIS_CHAMBER_CONTROLLER.get();
    }

    @Override
    protected Class<?> getNetworkDeviceProtocol() {
        return StasisChamberControlProtocol.class;
    }

    protected void updateInspectingChamber(@Nullable NetworkInterface.Address address, @Nullable ServerPlayer origin) {
        if (inspectingChamberAddress == address)
            return;

        if (inspectingChamber != null && origin != null) {
            inspectingChamber.stopOpen(origin);
        }

        inspectingChamberAddress = address;
        if (address == null || origin == null)
            return;

        var position = address.blockPosOrID().left();
        if (position.isEmpty())
            return;
        var blockEntity = origin.level().getBlockEntity(position.get(), ChangedBlockEntities.STASIS_CHAMBER.get());
        if (blockEntity.isEmpty())
            return;
        inspectingChamber = blockEntity.get();
        inspectingChamber.startOpen(origin);
    }

    @Override
    public void update(CompoundTag payload, LogicalSide receiver, @Nullable ServerPlayer origin) {
        if (receiver.isServer()) {
            String control = payload.getString("control");
            switch (control) {
                case "inspect" -> {
                    var address = NetworkInterface.Address.decode(payload.get("address"));
                    this.updateInspectingChamber(address, origin);
                }
                case "stopInspect" -> {
                    this.updateInspectingChamber(null, origin);
                }
                case "command" -> {
                    var address = NetworkInterface.Address.decode(payload.get("address"));
                    var command = StasisChamberMenu.Command.values()[payload.getInt("command")];
                    switch (command) {
                        case OPEN_DOOR -> queueFrame(address, DoorControlProtocol.OPEN_DOOR);
                        case CLOSE_DOOR -> queueFrame(address, DoorControlProtocol.CLOSE_DOOR);
                    }
                }
                case "inputProgram" -> {
                    var address = NetworkInterface.Address.decode(payload.get("address"));
                    var program = StasisChamberBlockEntity.Program.getFromID(TagUtil.getResourceLocation(payload, "program"));
                    if (program != null)
                        queueFrame(address, new StasisChamberControlProtocol.InputProgram(program, origin));
                }
                case "setModifications" -> {
                    var address = NetworkInterface.Address.decode(payload.get("address"));
                    CompoundTag modifications = payload.getCompound("modifications");
                    queueFrame(address, new StasisChamberControlProtocol.SetModifications(modifications));
                }
                case "setWaitDuration" -> {
                    var address = NetworkInterface.Address.decode(payload.get("address"));
                    int ticks = payload.getInt("ticks");
                    queueFrame(address, new StasisChamberControlProtocol.SetWaitDuration(ticks, origin));
                }
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

    public void inspectDevice(NetworkInterface.Address address) {
        CompoundTag payload = new CompoundTag();
        if (address != null) {
            payload.putString("control", "inspect");
            payload.put("address", address.encode());
        } else {
            payload.putString("control", "stopInspect");
        }
        Changed.PACKET_HANDLER.sendToServer(ComputerAppSyncPacket.syncApplication(this.getType(), payload));
    }

    public void requestSimpleCommand(StasisChamberMenu.Command command, NetworkInterface.Address address) {
        if (address == null) return;

        CompoundTag payload = new CompoundTag();
        payload.putString("control", "command");
        payload.putInt("command", command.ordinal());
        payload.put("address", address.encode());
        Changed.PACKET_HANDLER.sendToServer(ComputerAppSyncPacket.syncApplication(this.getType(), payload));
    }

    public void requestInputProgram(StasisChamberBlockEntity.Program program, NetworkInterface.Address address) {
        if (address == null) return;

        CompoundTag payload = new CompoundTag();
        payload.putString("control", "inputProgram");
        TagUtil.putResourceLocation(payload, "program", program.getId());
        payload.put("address", address.encode());
        Changed.PACKET_HANDLER.sendToServer(ComputerAppSyncPacket.syncApplication(this.getType(), payload));
    }

    public void requestSetModifications(CompoundTag modifications, NetworkInterface.Address address) {
        if (address == null) return;

        CompoundTag payload = new CompoundTag();
        payload.putString("control", "setModifications");
        payload.put("modifications", modifications);
        payload.put("address", address.encode());
        Changed.PACKET_HANDLER.sendToServer(ComputerAppSyncPacket.syncApplication(this.getType(), payload));
    }

    public void requestInputModification(String key, Tag tag, NetworkInterface.Address address) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.put(key, tag);
        this.requestSetModifications(compoundTag, address);
    }

    public void requestSetWaitDuration(int waitTicks, NetworkInterface.Address address) {
        if (address == null) return;

        CompoundTag payload = new CompoundTag();
        payload.putString("control", "setWaitDuration");
        payload.putInt("ticks", waitTicks);
        payload.put("address", address.encode());
        Changed.PACKET_HANDLER.sendToServer(ComputerAppSyncPacket.syncApplication(this.getType(), payload));
    }
}
