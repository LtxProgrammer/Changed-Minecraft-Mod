package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.client.gui.InfuserScreen;
import net.ltxprogrammer.changed.world.inventory.GuiStateProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.function.Supplier;
import java.util.logging.Level;

public class SyncSwitchPacket implements ChangedPacket {
    private final int containerId;
    private final boolean state;
    private final ResourceLocation name;

    public SyncSwitchPacket(InfuserScreen.Switch switchWidget) {
        containerId = switchWidget.containerScreen.getMenu().containerId;
        state = switchWidget.selected();
        name = switchWidget.getName();
    }

    public SyncSwitchPacket(FriendlyByteBuf buffer) {
        containerId = buffer.readInt();
        state = buffer.readBoolean();
        name = buffer.readResourceLocation();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(containerId);
        buffer.writeBoolean(state);
        buffer.writeResourceLocation(name);
    }

    private ServerPlayer findPlayerFromContainerId(int id) {
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
            if (player.containerMenu != null && player.containerMenu.containerId == id)
                return player;
        }

        return null;
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        var player = findPlayerFromContainerId(containerId);
        if (player != null && player.containerMenu instanceof GuiStateProvider menu) {
            menu.getState().put(name.toString(), state);
        }
    }

    public static SyncSwitchPacket of(InfuserScreen.Switch switchWidget) {
        return new SyncSwitchPacket(switchWidget);
    }
}
