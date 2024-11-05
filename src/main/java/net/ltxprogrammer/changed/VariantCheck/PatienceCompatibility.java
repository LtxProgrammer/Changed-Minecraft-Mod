package net.ltxprogrammer.changed.VariantCheck;

import io.github.edwinmindcraft.origins.api.OriginsAPI;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.Origin;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.ltxprogrammer.changed.Changed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

enum VariantType {
    LATEX_RESISTANCE, COMPATIBILITY, DEFAULT
}

public class PatienceCompatibility {
    private final ServerPlayer player;
    private boolean conditionValue = false;
    private VariantType variantType = VariantType.DEFAULT;

    public PatienceCompatibility(ServerPlayer player) {
        this.player = player;
        checkOriginCondition(player.getServer());
    }

    private void applyVariantLogic() {
        switch (variantType) {
            case LATEX_RESISTANCE -> conditionValue = true;
            case COMPATIBILITY -> conditionValue = false;
            default -> conditionValue = false;
        }
    }

    public boolean isConditionMet() {
        return conditionValue;
    }

    public void checkOriginCondition(MinecraftServer server) {
        OriginLayer layer = getOriginLayer(server, "origins:origin");
        Origin origin = getOrigin(server, "origins:human");

        if (layer == null || origin == null) {
            System.err.println("Failed to retrieve Origin layer or origin from registry.");
            return;
        }

        boolean nowHuman = hasOrigin(player, layer, origin);
        variantType = nowHuman ? VariantType.LATEX_RESISTANCE : VariantType.COMPATIBILITY;

        if (!(Changed.config.common.openOrigin.get())){
            applyVariantLogic();
        }
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
