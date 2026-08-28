package net.ltxprogrammer.changed.extension.vivecraft;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

/**
 * This class is safe to load without vivecraft
 */
public abstract class VivecraftHelper {
    public static final String MODID = "vivecraft";

    public static boolean isVivecraftPresent() {
        return ModList.get().isLoaded(MODID);
    }

    public static boolean isPlayerVR(Player player) {
        if (!isVivecraftPresent())
            return false;

        if (player instanceof ServerPlayer serverPlayer)
            return VivecraftInterface.isPlayerServerVR(serverPlayer);
        return VivecraftInterface.isPlayerClientVR(player);
    }

    public static float vivecraft$offsetAdd = 0.0f;
    public static float vivecraft$offsetMul = 1.0f;
}
