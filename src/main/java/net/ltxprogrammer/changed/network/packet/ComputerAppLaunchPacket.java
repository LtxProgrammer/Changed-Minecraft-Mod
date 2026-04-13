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

public class ComputerAppLaunchPacket implements ChangedPacket {
    private final int appId;
    private final List<String> args;

    public ComputerAppLaunchPacket(ApplicationType<?> application, List<String> args) {
        this.appId = ChangedRegistry.APPLICATION_TYPES.getID(application);
        this.args = args;
    }

    public ComputerAppLaunchPacket(FriendlyByteBuf buffer) {
        this.appId = buffer.readVarInt();
        this.args = buffer.readList(FriendlyByteBuf::readUtf);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.appId);
        buffer.writeCollection(this.args, FriendlyByteBuf::writeUtf);
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

                var app = computerMenu.launchApplication(ChangedRegistry.APPLICATION_TYPES.getValue(this.appId), args);
                computerScreen.pushApplicationScreen(ApplicationScreens.createScreen(app, computerScreen));
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

                computerMenu.launchApplication(ChangedRegistry.APPLICATION_TYPES.getValue(this.appId), args);
                Changed.PACKET_HANDLER.send(PacketDistributor.PLAYER.with(context::getSender), this);
            });
        }
    }

    public static ComputerAppLaunchPacket launchApplication(ApplicationType<?> application, String... args) {
        return launchApplication(application, Arrays.asList(args));
    }

    public static ComputerAppLaunchPacket launchApplication(ApplicationType<?> application, List<String> args) {
        return new ComputerAppLaunchPacket(application, args);
    }
}
