package net.ltxprogrammer.changed.computers.application;

import net.ltxprogrammer.changed.computers.protocol.DoorControlProtocol;
import net.ltxprogrammer.changed.init.ChangedApplications;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.ltxprogrammer.changed.world.inventory.StasisChamberMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class DoorControllerApplication extends NetworkDeviceControllerApplication {
    protected @Nullable Integer currentDoor;

    public enum Command {
        NOOP(app -> {}),
        OPEN_DOOR(app -> {
            if (app.currentDoor != null)
                app.queuePacket(app.currentDoor, DoorControlProtocol.OPEN_DOOR);
        }),
        CLOSE_DOOR(app -> {
            if (app.currentDoor != null)
                app.queuePacket(app.currentDoor, DoorControlProtocol.CLOSE_DOOR);
        });

        private final Consumer<DoorControllerApplication> handler;

        Command(Consumer<DoorControllerApplication> handler) {
            this.handler = handler;
        }

        public void handle(DoorControllerApplication app) {
            handler.accept(app);
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
                Command.values()[commandId].handle(this);
            }
        }
    }

    @Override
    public void serverTick(ServerLevel level) {
        super.serverTick(level);

        if (currentDoor != null && !this.isDeviceReachable(currentDoor))
            currentDoor = null;
    }
}
