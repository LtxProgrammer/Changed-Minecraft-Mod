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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class LatexPhantom {

    private static final Map<ServerPlayer, Integer> immunePlayers = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        LivingEntity entity = (LivingEntity) event.getEntity();
        if (!(entity instanceof ServerPlayer player)) return;
        MinecraftServer server = player.getServer();
        if (ModList.get().isLoaded("origins") && ProcessTransfur.isPlayerTransfurred(player)) {
            if (Changed.config.server.enableTransfurringOrigins.get()) {
                OriginLayer layer = OriginsAPI.getLayersRegistry(server).get(new ResourceLocation("origins:origin"));
                Origin origin = OriginsAPI.getOriginsRegistry(server).get(new ResourceLocation("origins:phantom"));
                boolean nowPhantom = IOriginContainer.get(player)
                        .map(container -> Objects.equals(container.getOrigin(layer), origin))
                        .orElse(false);
                CheckCondition compatibility = new CheckCondition(player);
                compatibility.checkOriginCondition(server);
                boolean inSunlight = player.level.isDay() &&
                        player.level.canSeeSky(player.blockPosition()) &&
                        !player.level.isRaining();
                if(player.isOnFire()) {
                    if (nowPhantom && compatibility.VariantTypeNumber() == 1 && inSunlight) {
                            player.clearFire();
                            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0));
                        } else {
                        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 0, 0));
                    }
                }
            }
        }
    }
}