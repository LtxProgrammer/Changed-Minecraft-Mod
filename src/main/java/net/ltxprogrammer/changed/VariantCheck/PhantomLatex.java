package net.ltxprogrammer.changed.VariantCheck;

import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;

import java.util.Objects;

public class PhantomLatex {
    private final ServerPlayer player;
    private boolean nowPhantom;

    public PhantomLatex(ServerPlayer player) {
        this.player = player;
        this.nowPhantom = false;
        checkOriginCondition(player.getServer());
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PhantomLatex phantomLatex = new PhantomLatex(player);
            if (event.getSource() == DamageSource.IN_WALL) {
                checkOriginCondition(player.getServer());
                if (phantomLatex.nowPhantom) {
                    if (isPlayerSurrounded(player)) {
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    private static boolean isPlayerSurrounded(Player player) {
        var level = player.level;
        var pos = player.blockPosition();


        return level.getBlockState(pos.above()).isSolidRender(level, pos.above()) &&
                level.getBlockState(pos.below()).isSolidRender(level, pos.below()) &&
                level.getBlockState(pos.north()).isSolidRender(level, pos.north()) &&
                level.getBlockState(pos.south()).isSolidRender(level, pos.south()) &&
                level.getBlockState(pos.west()).isSolidRender(level, pos.west()) &&
                level.getBlockState(pos.east()).isSolidRender(level, pos.east());
    }

    private void checkOriginCondition(MinecraftServer server) {
        OriginLayer layer = getOriginLayer(server, "origins:origin");
        Origin origin = getOrigin(server, "origins:phantom");

        if (layer == null || origin == null) {
            System.err.println("Failed to retrieve Origin layer or origin from registry.");
            return;
        }

        this.nowPhantom = hasOrigin(player, layer, origin);
    }

    private boolean hasOrigin(ServerPlayer player, OriginLayer layer, Origin origin) {
        return IOriginContainer.get(player)
                .map(container -> Objects.equals(container.getOrigin(layer), origin))
                .orElse(false);
    }

    private OriginLayer getOriginLayer(MinecraftServer server, String layerPath) {
        return OriginsAPI.getLayersRegistry(server).get(new ResourceLocation(layerPath));
    }

    private Origin getOrigin(MinecraftServer server, String originPath) {
        return OriginsAPI.getOriginsRegistry(server).get(new ResourceLocation(originPath));
    }
}
