package net.ltxprogrammer.changed.extension.origins;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import java.util.stream.Stream;

/**
 * This class is safe to load without the origins mod
 */
public abstract class OriginsHelper {
    public static final String MODID = "origins";

    public static boolean isOriginsPresent() {
        return ModList.get().isLoaded(MODID);
    }

    public static ResourceLocation modResource(String path) {
        return new ResourceLocation(MODID, path);
    }

    public static boolean doesOriginUsePlayer(Player player) {
        if (!isOriginsPresent())
            return false;
        if (!(player instanceof ServerPlayer serverPlayer))
            return false;

        // Check if player has an origin
        if (!OriginsInterface.doesPlayerHaveAnyOrigins(serverPlayer))
            return false;

        // Check if origin is transfurable
        return !OriginsInterface.isPlayerOrigin(serverPlayer, OriginsInterface.TRANSFURABLE);
    }

    public static void addDataListeners(AddReloadListenerEvent event) {
        if (!isOriginsPresent())
            return;
        event.addListener(OriginsInterface.INSTANCE);
    }
}
