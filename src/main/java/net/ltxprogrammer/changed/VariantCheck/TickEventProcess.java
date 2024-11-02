package net.ltxprogrammer.changed.VariantCheck;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

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

        // 遍历所有在线玩家并调用 condition 方法
        for (Player player : server.getPlayerList().getPlayers()) {
            // 创建 PatienceCompatibility 实例并调用其 condition 方法
            PatienceCompatibility compatibility = new PatienceCompatibility(player);
            compatibility.OriginCondition(server); // 确保 condition 方法是实例方法
        }
    }
}
