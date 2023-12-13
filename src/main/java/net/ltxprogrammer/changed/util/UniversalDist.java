package net.ltxprogrammer.changed.util;

import net.ltxprogrammer.changed.block.entity.GluBlockEntity;
import net.ltxprogrammer.changed.client.gui.GluBlockEditScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;

public class UniversalDist {
    public static class ClientDist {
        public static Level getLevel() {
            return Minecraft.getInstance().level;
        }
        public static Player getLocalPlayer() {
            return Minecraft.getInstance().player;
        }
        public static void displayClientMessage(Component component, boolean notInChat) {
            Minecraft.getInstance().player.displayClientMessage(component, notInChat);
        }
        public static Entity getCameraEntity() {
            return Minecraft.getInstance().cameraEntity;
        }
        public static HitResult getLocalHitResult() {
            return Minecraft.getInstance().hitResult;
        }
        public static void openGluBlock(Player player, GluBlockEntity gluBlockEntity) {
            if (player == getLocalPlayer())
                Minecraft.getInstance().setScreen(new GluBlockEditScreen(gluBlockEntity));
        }
    }

    public static class ServerDist {
        public static Level getLevel() {
            return ServerLifecycleHooks.getCurrentServer().overworld();
        }
    }

    public static Level getLevel() {
        Level level = DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> ClientDist::getLevel);
        return level != null ? level :
                DistExecutor.unsafeCallWhenOn(Dist.DEDICATED_SERVER, () -> ServerDist::getLevel);
    }

    public static void displayClientMessage(Component component, boolean notInChat) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientDist.displayClientMessage(component, notInChat));
    }

    public static @Nullable Player getLocalPlayer() {
        return DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> ClientDist::getLocalPlayer);
    }

    public static @Nullable Entity getCameraEntity() {
        return DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> ClientDist::getCameraEntity);
    }

    public static boolean isLocalPlayer(Player player) {
        return player == getLocalPlayer();
    }

    public static HitResult getLocalHitResult() {
        return DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> ClientDist::getLocalHitResult);
    }

    public static void openGluBlock(Player player, GluBlockEntity gluBlockEntity) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientDist.openGluBlock(player, gluBlockEntity));
    }
}
