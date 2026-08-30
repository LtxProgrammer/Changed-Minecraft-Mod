package net.ltxprogrammer.changed.extension.vivecraft;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.vivecraft.client.ClientVRPlayers;
import org.vivecraft.server.ServerVRPlayers;

/**
 * This class should not be loaded if vivecraft is not present.
 */
class VivecraftInterface {
    private VivecraftInterface() {}

    static boolean isPlayerClientVR(Player player) {
        return ClientVRPlayers.getInstance().isVRPlayer(player);
    }

    static boolean isPlayerServerVR(ServerPlayer player) {
        return ServerVRPlayers.isVRPlayer(player);
    }
}
