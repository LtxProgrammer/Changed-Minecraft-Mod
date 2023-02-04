package net.ltxprogrammer.changed.util;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

public class ClientDistUtil {
    public static boolean isLocalPlayer(Player player) {
        return player instanceof LocalPlayer;
    }
}
