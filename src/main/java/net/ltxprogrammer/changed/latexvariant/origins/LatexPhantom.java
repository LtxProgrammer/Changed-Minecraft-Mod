package net.ltxprogrammer.changed.latexvariant.origins;

import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.ltxprogrammer.changed.Changed;
import net.ltxprogrammer.changed.latexvariant.CheckCondition;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

public class LatexPhantom {
    /**
     * Handles the living entity update event and prevents sunburn if the player has a specific origin and variant type.
     *
     * <p>This method is invoked every time a living entity's state is updated. It checks if the player has the `phantom` origin
     * and a specific variant type. If these conditions are met, the player is protected from sunlight-induced burning.</p>
     *
     * @param event The living entity update event containing entity information.
     * @param server The current Minecraft server instance, used to retrieve server-related resources.
     */
    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event, MinecraftServer server) {
        LivingEntity entity = (LivingEntity) event.getEntity();
        if (!(entity instanceof ServerPlayer player)) return;
        if (ModList.get().isLoaded("origins") && ProcessTransfur.isPlayerTransfurred(player)) {
            if (Changed.config.server.enableTransfurringOrigins.get()) {
                OriginLayer layer = OriginsAPI.getLayersRegistry(server).get(new ResourceLocation("origins:origin"));
                Origin origin = OriginsAPI.getOriginsRegistry(server).get(new ResourceLocation("origins:phantom"));
                boolean nowPhantom = IOriginContainer.get(player)
                        .map(container -> Objects.equals(container.getOrigin(layer), origin))
                        .orElse(false);
                CheckCondition compatibility = new CheckCondition(player);
                if (nowPhantom && compatibility.VariantTypeNumber() == 1) {
                    boolean inSunlight = player.level.isDay() &&
                            player.level.canSeeSky(player.blockPosition()) &&
                            !player.level.isRaining();
                    if (inSunlight && player.isOnFire()) {
                        player.clearFire();
                    }
                }
            }
        }
    }
}
