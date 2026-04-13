package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.client.gui.ComputerScreen;
import net.ltxprogrammer.changed.client.gui.computer.ApplicationScreens;
import net.ltxprogrammer.changed.computers.application.ApplicationType;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ComputerAppClosePacket implements ChangedPacket {
    private final int appId;

    public ComputerAppClosePacket(ApplicationType<?> application) {
        this.appId = ChangedRegistry.APPLICATION_TYPES.getID(application);
    }

    public ComputerAppClosePacket(FriendlyByteBuf buffer) {
        this.appId = buffer.readVarInt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.appId);
    }

    @Override
    public CompletableFuture<Void> handle(NetworkEvent.Context context, CompletableFuture<Level> levelFuture, Executor sidedExecutor) {
        if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                if (!(UniversalDist.getLocalPlayer() instanceof LocalPlayer localPlayer))
                    return;
                if (!(localPlayer.containerMenu instanceof ComputerMenu computerMenu))
                    return;
                if (!(Minecraft.getInstance().screen instanceof ComputerScreen computerScreen))
                    return;

                computerMenu.closeApplication(ChangedRegistry.APPLICATION_TYPES.getValue(this.appId));
                computerScreen.popApplicationScreen();
            });
        }

        else {
            context.setPacketHandled(true);
            return levelFuture.thenAccept(level -> {
                ServerPlayer sender = context.getSender();
                if (sender == null)
                    return;
                if (!(sender.containerMenu instanceof ComputerMenu computerMenu))
                    return;

                computerMenu.closeApplication(ChangedRegistry.APPLICATION_TYPES.getValue(this.appId));
                Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(context::getSender), this);
            });
        }
    }

    public static ComputerAppClosePacket closeApplication(ApplicationType<?> application) {
        return new ComputerAppClosePacket(application);
    }
}
