package net.ltxprogrammer.changed.extension.latexvariant.origins;

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
import java.util.UUID;

@Mod.EventBusSubscriber
public class LatexPhantom {
    private static final Map<UUID, Long> playerCooldown = new HashMap<>();

    /**
     * Checks if the player has Phantom origins.
     *
     * @param player Player object
     * @param server Minecraft server
     * @return Returns true if the player is a Phantom, false otherwise.
     */
    public static boolean isPhantom(ServerPlayer player, MinecraftServer server) {
        OriginLayer layer = OriginsAPI.getLayersRegistry(server).get(new ResourceLocation("origins:origin"));
        Origin origin = OriginsAPI.getOriginsRegistry(server).get(new ResourceLocation("origins:phantom"));
        return IOriginContainer.get(player)
                .map(container -> layer != null && Objects.equals(container.getOrigin(layer), origin))
                .orElse(false);
    }
    @SubscribeEvent
    public static void burnInDaylight(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && ModList.get().isLoaded("origins")) {
            Player player = event.player;
            if (player instanceof ServerPlayer serverPlayer) {
                MinecraftServer server = serverPlayer.level.getServer();
                if (isPhantom(serverPlayer, server) && serverPlayer.level.canSeeSky(serverPlayer.blockPosition())
                        && !ProcessTransfur.isPlayerPermTransfurred(serverPlayer)) {
                    OriginLayer layer = OriginsAPI.getLayersRegistry(server).get(new ResourceLocation("origins:origin"));
                    Origin phantomizeOverlayOrigin = OriginsAPI.getOriginsRegistry(server)
                            .get(new ResourceLocation("origins", "phantomize_overlay"));
                    IOriginContainer originContainer = IOriginContainer.get(serverPlayer).orElse(null);
                    if (originContainer != null && layer != null && Objects.equals(originContainer.getOrigin(layer), phantomizeOverlayOrigin)) {
                        if (serverPlayer.level.isDay()) {
                            long currentTime = serverPlayer.level.getGameTime();
                            UUID playerUUID = serverPlayer.getUUID();
                            if (!playerCooldown.containsKey(playerUUID) || currentTime - playerCooldown.get(playerUUID) >= 100) {
                                serverPlayer.setSecondsOnFire(5);
                                playerCooldown.put(playerUUID, currentTime);
                            }
                        }
                    } else {
                        playerCooldown.remove(serverPlayer.getUUID());
                        serverPlayer.setSecondsOnFire(0);
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            playerCooldown.remove(serverPlayer.getUUID());
        }
    }
}
