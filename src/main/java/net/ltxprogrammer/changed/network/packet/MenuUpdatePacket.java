package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.inventory.UpdateableMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.function.Supplier;

public class MenuUpdatePacket implements ChangedPacket {
    private final int containerId;
    private final CompoundTag payload;

    public MenuUpdatePacket(int containerId, CompoundTag payload) {
        this.containerId = containerId;
        this.payload = payload;
    }

    public MenuUpdatePacket(FriendlyByteBuf byteBuf) {
        containerId = byteBuf.readInt();
        payload = byteBuf.readNbt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(containerId);
        buffer.writeNbt(payload);
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
        var context = contextSupplier.get();
        if (context.getDirection().getReceptionSide().isServer()) {
            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers()) {
                if (player.containerMenu instanceof UpdateableMenu updateableMenu && player.containerMenu.containerId == containerId) {
                    updateableMenu.update(payload, context.getDirection().getReceptionSide());
                    context.setPacketHandled(true);
                    return;
                }
            }
        } else {
            var player = UniversalDist.getLocalPlayer();
            if (player == null)
                return;
            if (player.containerMenu == null || player.containerMenu.containerId != containerId)
                return;
            if (player.containerMenu instanceof UpdateableMenu updateableMenu) {
                updateableMenu.update(payload, context.getDirection().getReceptionSide());
                context.setPacketHandled(true);
            }
        }
    }
}
