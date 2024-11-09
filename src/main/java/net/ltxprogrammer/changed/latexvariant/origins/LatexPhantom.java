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

@Mod.EventBusSubscriber
public class LatexPhantom {
    private final ServerPlayer player;
    public LatexPhantom(ServerPlayer player){
        this.player = player;
    }

    /**
     * Handles the living entity update event and performs logic based on the player's origin and variant type.
     *
     * <p>This method is invoked every time a living entity's state is updated. It checks if the player has a specific origin
     * (such as `phantom`), and if the player's variant type meets the conditions, and if the entity is on fire, it clears the fire.</p>
     *
     * @param event The living entity update event that contains the entity information.
     * @param server The current Minecraft server instance, used to retrieve server-related resources, such as the origin layer and origins registry.
     */
    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event, MinecraftServer server) {
        if(ProcessTransfur.isPlayerTransfurred(player)){
            if((ModList.get().isLoaded("origins"))) {
                if (Changed.config.server.enableTransfurringOrigins.get()) {
                OriginLayer layer = OriginsAPI.getLayersRegistry(server).get(new ResourceLocation("origins:origin"));
                Origin origin = OriginsAPI.getOriginsRegistry(server).get(new ResourceLocation("origins:phantom"));
                boolean nowPhantom = IOriginContainer.get(player)
                        .map(container -> Objects.equals(container.getOrigin(layer), origin))
                        .orElse(false);
                ServerPlayer player = (ServerPlayer) event.getEntity();
                CheckCondition compatibility = new CheckCondition(player);
                compatibility.VariantTypeNumber();
                    if (nowPhantom) {
                        if (compatibility.VariantTypeNumber() == 1) {
                        LivingEntity entity = (LivingEntity) event.getEntity();
                            if (entity.isOnFire()) {
                            entity.clearFire();
                            }
                        }
                    }
                }
            }
        }
    }
}

