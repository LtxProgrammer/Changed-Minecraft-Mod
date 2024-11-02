package net.ltxprogrammer.changed.VariantCheck;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber
public class TickEventProcess {
    private static MinecraftServer server;

    public static void setServer(MinecraftServer server) {
        TickEventProcess.server = server;
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            execute(event);
        }
    }

    public static void execute() {
        execute(null);
    }

    private static void execute(@Nullable Event event) {
        // 确保服务器实例不为空
        if (server == null) {
            return;
        }

        // 获取所有在线玩家
        List<ServerPlayer> players = server.getPlayerList().getPlayers();

        for (ServerPlayer player : players) {
            // 创建 PatienceCompatibility 实例，并调用方法更新条件
            PatienceCompatibility compatibility = new PatienceCompatibility(player);
            compatibility.checkOriginCondition(server); // 使用 server 更新变身条件
        }
    }
}
