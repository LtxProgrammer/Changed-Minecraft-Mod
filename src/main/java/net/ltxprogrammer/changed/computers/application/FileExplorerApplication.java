package net.ltxprogrammer.changed.computers.application;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.computers.Folder;
import net.ltxprogrammer.changed.computers.SourcedDiscData;
import net.ltxprogrammer.changed.computers.protocol.DeviceInfoProtocol;
import net.ltxprogrammer.changed.computers.protocol.FileSystemShareProtocol;
import net.ltxprogrammer.changed.computers.protocol.Packet;
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

public class FileExplorerApplication extends NetworkDeviceControllerApplication {
    public final Map<Integer, DeviceInfoProtocol> reachableDevices = new HashMap<>();
    public final Map<Integer, FileSystemShareProtocol> deviceFolders = new HashMap<>();
    public boolean devicesDirty = true;
    public boolean listingsDirty = true;
    public Character remoteDriveLetter = null;
    public Character openDriveLetter = null;

    public FileExplorerApplication(ComputerMenu menu, List<String> args) {
        super(menu);
    }

    @Override
    public ApplicationType<?> getType() {
        return ChangedApplications.FILE_EXPLORER.get();
    }

    @Override
    protected Class<?> getNetworkDeviceProtocol() {
        return FileSystemShareProtocol.Query.class;
    }

    private void refreshRemoteListings() {
        CompoundTag response = new CompoundTag();
        response.putString("control", "refreshListings");

        Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) menu.getPlayer()),
                ComputerAppSyncPacket.syncApplication(this.getType(), response));
    }

    @Override
    public void update(CompoundTag payload, LogicalSide receiver, @Nullable ServerPlayer origin) {
        if (receiver.isServer()) {
            String control = payload.getString("control");
            if ("mount".equals(control)) {
                int remoteAddress = payload.getInt("address");
                if (remoteDriveLetter != null)
                    menu.computer.unmountDisc(remoteDriveLetter);
                remoteDriveLetter = menu.computer.mountDisc(SourcedDiscData.fromRemote(
                        remoteAddress,
                        reachableDevices.get(remoteAddress).deviceName().getString(),
                        deviceFolders.get(remoteAddress))).orElse(null);
                menu.syncBlockEntity();

                if (remoteDriveLetter != null) {
                    CompoundTag response = new CompoundTag();
                    response.putString("control", "selectDrive");
                    response.putString("letter", remoteDriveLetter.toString());

                    Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)menu.getPlayer()),
                            ComputerAppSyncPacket.syncApplication(this.getType(), response));
                }
            } else if ("unmount".equals(control)) {
                if (remoteDriveLetter != null) {
                    menu.computer.unmountDisc(remoteDriveLetter);
                    menu.syncBlockEntity();
                    this.refreshRemoteListings();
                }
            } else if ("eject".equals(control)) {
                char driveLetter = payload.getString("letter").charAt(0);
                if (menu.computer.eject(driveLetter)) {
                    menu.syncBlockEntity();
                    this.refreshRemoteListings();
                }
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
            } else if ("selectDrive".equals(control)) {
                openDriveLetter = payload.getString("letter").charAt(0);
            } else if ("refreshListings".equals(control)) {
                listingsDirty = true;
            }
        }
    }

    @Override
    public void handlePacket(ServerLevel level, int logicalSource, Packet packet) {
        super.handlePacket(level, logicalSource, packet);
        if (packet instanceof FileSystemShareProtocol fileSystemShare) {
            deviceFolders.put(logicalSource, fileSystemShare);
        }
    }

    @Override
    protected void deviceAdded(ServerLevel level, int logicalDevice, DeviceInfoProtocol deviceInfoProtocol) {
        reachableDevices.put(logicalDevice, deviceInfoProtocol);

        CompoundTag payload = new CompoundTag();
        payload.putString("control", "addDevice");
        payload.putInt("address", logicalDevice);
        payload.putString("name", Component.Serializer.toJson(deviceInfoProtocol.deviceName()));
        TagUtil.putBlockPos(payload, "pos", deviceInfoProtocol.position());
        TagUtil.putResourceLocation(payload, "icon", deviceInfoProtocol.icon());
        Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)menu.getPlayer()),
                ComputerAppSyncPacket.syncApplication(this.getType(), payload));

        menu.computer.nic.sendPacket(level, logicalDevice, FileSystemShareProtocol.Query.INSTANCE);
    }

    @Override
    protected void deviceRemoved(ServerLevel level, int logicalDevice) {
        reachableDevices.remove(logicalDevice);
        deviceFolders.remove(logicalDevice);

        CompoundTag payload = new CompoundTag();
        payload.putString("control", "removeDevice");
        payload.putInt("address", logicalDevice);
        Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)menu.getPlayer()),
                ComputerAppSyncPacket.syncApplication(this.getType(), payload));
    }

    @Override
    public void onClose() {
        super.onClose();
        if (remoteDriveLetter != null) {
            menu.computer.unmountDisc(remoteDriveLetter);
            menu.syncBlockEntity();
        }
    }
}
