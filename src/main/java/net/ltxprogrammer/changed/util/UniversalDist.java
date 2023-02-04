package net.ltxprogrammer.changed.util;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.server.ServerLifecycleHooks;

public class UniversalDist {
    public static class ClientDist {
        public static Level getLevel() {
            return Minecraft.getInstance().level;
        }
        public static void displayClientMessage(Component component, boolean notInChat) {
            Minecraft.getInstance().player.displayClientMessage(component, notInChat);
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
}
