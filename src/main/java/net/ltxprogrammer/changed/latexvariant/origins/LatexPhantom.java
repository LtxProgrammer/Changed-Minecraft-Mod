package net.ltxprogrammer.changed.latexvariant.origins;

import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Mod.EventBusSubscriber
public class LatexPhantom {
    private final ServerPlayer player;
    private boolean nowphantom = false;
    private static final Map<ServerPlayer, Long> playerCooldown = new HashMap<>(); // 存储玩家和时间戳
    public LatexPhantom(ServerPlayer player) {
        this.player = player;
    }
    public void nowPhantom(MinecraftServer server) {
        OriginLayer layer = OriginsAPI.getLayersRegistry(server).get(new ResourceLocation("origins:origin"));
        Origin origin = OriginsAPI.getOriginsRegistry(server).get(new ResourceLocation("origins:phantom"));
        nowphantom = IOriginContainer.get(player)
                .map(container -> {
                    assert layer != null;
                    return Objects.equals(container.getOrigin(layer), origin);
                })
                .orElse(false);
    }
    /**
     * Handles the player's tick event to simulate burning in daylight for players with the Phantom origin.
     * This method will check if the player is a Phantom, if they can see the sky, and if it's daytime in the game.
     * If the conditions are met, the player will burn for a specified duration every 5 seconds (100 ticks).
     * @param event The PlayerTickEvent triggered on each tick of the player.
     *              This event contains the player and the current phase of the tick (START or END).
     *              The event is used to check if the conditions are met for the player to be set on fire.
     */
    @SubscribeEvent
    public static void burn_in_daylight(TickEvent.PlayerTickEvent event) {
        // 确保在适当的 Tick 阶段执行
        if (event.phase == TickEvent.Phase.END && ModList.get().isLoaded("origins")) {
            Player player = event.player;
            if (player instanceof ServerPlayer serverPlayer) {
                LatexPhantom phantomPlayer = new LatexPhantom(serverPlayer);
                phantomPlayer.nowPhantom(serverPlayer.level.getServer());
                if (phantomPlayer.nowphantom && serverPlayer.level.canSeeSky(serverPlayer.blockPosition())
                        && !ProcessTransfur.isPlayerPermTransfurred(serverPlayer)) {
                    IOriginContainer originContainer = IOriginContainer.get(serverPlayer).orElse(null);
                    Origin phantomizeOverlayOrigin = OriginsAPI.getOriginsRegistry(Objects.requireNonNull(serverPlayer.level.getServer()))
                            .get(new ResourceLocation("origins", "phantomize_overlay"));
                    if (originContainer.getOrigin(Objects.requireNonNull(OriginsAPI.getLayersRegistry(serverPlayer.level.getServer())
                            .get(new ResourceLocation("origins:origin")))) == phantomizeOverlayOrigin) {
                        if (serverPlayer.level.isDay()) {
                            long currentTime = serverPlayer.level.getGameTime();
                            if (!playerCooldown.containsKey(serverPlayer) || currentTime - playerCooldown.get(serverPlayer) >= 100) {
                                serverPlayer.setSecondsOnFire(5);
                                playerCooldown.put(serverPlayer, currentTime);
                            }
                        }
                    } else {
                        playerCooldown.remove(serverPlayer);
                        serverPlayer.setSecondsOnFire(0);
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            playerCooldown.remove(serverPlayer);
        }
    }
}