package net.ltxprogrammer.changed.network.packet;

import net.ltxprogrammer.changed.client.gui.ComputerScreen;
import net.ltxprogrammer.changed.client.gui.computer.ApplicationScreens;
import net.ltxprogrammer.changed.computers.application.ApplicationType;
import net.ltxprogrammer.changed.init.ChangedRegistry;
import net.ltxprogrammer.changed.util.UniversalDist;
import net.ltxprogrammer.changed.world.inventory.ComputerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ComputerAppSyncPacket implements ChangedPacket {
    private final int appId;
    private final CompoundTag data;

    public ComputerAppSyncPacket(ApplicationType<?> application, CompoundTag data) {
        this.appId = ChangedRegistry.APPLICATION_TYPES.getID(application);
        this.data = data;
    }

    public ComputerAppSyncPacket(FriendlyByteBuf buffer) {
        this.appId = buffer.readVarInt();
        this.data = buffer.readAnySizeNbt();
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.appId);
        buffer.writeNbt(this.data);
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

                var app = computerMenu.currentApplication();
                if (app == null || app.getType() != ChangedRegistry.APPLICATION_TYPES.getValue(this.appId))
                    throw new IllegalArgumentException("Application type mismatch");
                app.acceptPayload(this.data);
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

                var app = computerMenu.currentApplication();
                if (app == null || app.getType() != ChangedRegistry.APPLICATION_TYPES.getValue(this.appId))
                    throw new IllegalArgumentException("Application type mismatch");
                app.acceptPayload(this.data);
            });
        }
    }

    public static ComputerAppSyncPacket syncApplication(ApplicationType<?> application, CompoundTag data) {
        return new ComputerAppSyncPacket(application, data);
    }
}
