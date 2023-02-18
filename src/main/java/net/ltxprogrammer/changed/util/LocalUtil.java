package net.ltxprogrammer.changed.util;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class LocalUtil {
    public static void mulInputImpulse(Player player, float mul) {
        if (player instanceof LocalPlayer localPlayer) {
            localPlayer.input.forwardImpulse *= mul;
            localPlayer.input.leftImpulse *= mul;
        }
    }
}
